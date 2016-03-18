package eu.greencom.mgmbroker.manager.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import eu.greencom.mgmbroker.commandlayer.CommandLayerUtils;
import eu.greencom.mgmbroker.commandlayer.ConfigurationObject;
import eu.greencom.mgmbroker.manager.api.CommunicationLayerMGR;
import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.api.Method;
import eu.linksmart.network.jsonrpc.api.Error;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;

/**
 * A component registering as eventHandler for:
 * <ul>
 * <li>Local copy of configuration changes: in this case the new values will be
 * sent to gateways</li>
 * <li>A JSONRPC POST Notification: A gw informs that its local configuration is
 * changed</li>
 * <li>A JSONRPC GET Request: A GW is asking for a configuration because its
 * local copy have beend deleted</li>
 * <ul>
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class CommandLayerMGRImpl implements EventHandler {

	// to get configuration updates
	public static final String OSGI_CONFIGURATIONUPDATED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_UPDATED";

	// To receive JSONRPC POST notifications, a GW sends it when its own
	// configuration have been updated
	public static final String JSONRPC_POST_NOTIFICATION_EVENT_TOPIC = "event/jsonrpc/notification/POST";

	// To receive JSONRPC GET request, the GW asks for values stored in the MGM.
	// The GW sends this massege when its config have beend deleted
	private static final String JSONRPC_GET_REQUEST_EVENT_TOPIC = "event/jsonrpc/request/GET";

	private ComponentContext context;
	private JsonRpcClientFactory clientFactory;
	private CommunicationLayerMGR communicationLayerMGR;
	private ConfigurationAdmin configurationAdmin;

	private static final Logger LOG = Logger.getLogger(CommandLayerMGRImpl.class);

	private ObjectMapper mapper;

	public void activate(ComponentContext context) {

		this.context = context;

		mapper = new ObjectMapper();

		// properly serialize and deserialize Joda's DateTime
		mapper.registerModule(new JodaModule());

		// register as received of configuration POST events
		String[] topics = new String[] {
				// To receive JSONRPC notifications
				JSONRPC_POST_NOTIFICATION_EVENT_TOPIC,
				// A get request can arrive from a GW: the requested
				// configuration will be sent as response
				JSONRPC_GET_REQUEST_EVENT_TOPIC,
				// To get notified when a local copy of GWs configuration
				// configuration have been updated
				OSGI_CONFIGURATIONUPDATED_TOPIC };
		@SuppressWarnings("rawtypes")
		Dictionary properties = new Hashtable();//NOSONAR squid:S1149 - JPU: Required by OSGi API
		properties.put(EventConstants.EVENT_TOPIC, topics);
		String filterString = " ("
				// Events could be either remote ConfigurationMessage messages
				// or local updates of configurations to be reported to the GWs
				+ "|" + "(uri=gc:ConfigurationMessage)" + "(cm.pid=" + CommandLayerUtils.MGM_CONFIGURATION_PID_PREFIX
				+ "*)" + ")";
		Filter filter;
		try {
			filter = context.getBundleContext().createFilter(filterString);
		} catch (InvalidSyntaxException e) {
			LOG.error("Error creating filter", e);
			return;
		}
		properties.put(EventConstants.EVENT_FILTER, filter.toString());

		context.getBundleContext().registerService(EventHandler.class, this, properties);

		LOG.debug("Started " + context.getBundleContext().getBundle().getSymbolicName());

	}

	private JsonRpcNotification prepareNotification(Configuration config) {
		JsonRpcNotification notification = new JsonRpcNotification();
		notification.setMethod(Method.POST);
		notification.getParams().setUri("gc:ConfigurationMessage");

		try {
			notification.getParams().setData(
					mapper.writeValueAsString(new ConfigurationObject(
					// convert PID to the one used by the Gateway
							CommandLayerUtils.toGatewayConfigurationPID(config.getPid()), OSGiUtils
									.dictionaryToMap(config.getProperties()))));
		} catch (JsonProcessingException e) {
			LOG.error("Error serializing properties", e);
			return null;
		}
		return notification;
	}

	private void updateOsgiConfiguration(Event event) {
		// a local copy of a configuration
		// have been updated
		// update gateways configuration with a JsonRPC notification

		// get the configuration values
		String pid = (String) event.getProperty("cm.pid");
		Configuration newConfig;
		try {
			newConfig = configurationAdmin.getConfiguration(pid);
		} catch (IOException e1) {
			LOG.error("Error getting configuration for PID:" + pid, e1);
			return;
		}
		// prepare the notification
		JsonRpcNotification notification = prepareNotification(newConfig);
		// send updates to all Gateways
		for (String gw : communicationLayerMGR.getGateways().keySet()) {
			LOG.debug("Notifying gateway " + gw + " with new configuration");
			this.clientFactory.createJsonRpcClient(gw).post(notification);
		}
	}

	private void postNotificationEvent(Event event) {
		// a gateway warns that its
		// configuration is changed.
		// Store a local copy if not
		// available or update local
		// copy with new properties.
		// If the configuration is not stored locally or there is a new
		// property, the property values are copied from received
		// configuration

		JsonRpcNotification notification = OSGiUtils.toNotification(event);
		try {
			// parse the received configuration object
			ConfigurationObject configurationObject = mapper.readValue(notification.getParams().getData(),
					ConfigurationObject.class);

			// get local copy of configuration for the target component by
			// PID
			Configuration configuration = configurationAdmin.getConfiguration(

			// translate the PID specified by the gateway to the one to be
			// specified in the manager
					CommandLayerUtils.toManagerConfigurationPID(configurationObject.getPID()));

			Dictionary dict = configuration.getProperties();

			// this flag must be set to true when the MGM needs to update
			// local configuration
			Boolean update = false;

			// check if the MGM already have the configuration for the
			// component
			if (dict == null) {
				// no previous configuration, creating a new Dictionary
				dict = new Hashtable<String, Object>();
				// the configuration needs to be updated
				update = true;
			}

			// check if there is a new property
			Set props = new HashSet<String>(Collections.list(dict.keys()));
			for (String propertyName : configurationObject.getValues().keySet()) {

				if (!props.contains(propertyName)) {
					// add the new property and its value in the
					// configuration to be stored on the MGM side
					dict.put(propertyName, configurationObject.getValues().get(propertyName));
					// the configuration needs to be updated
					update = true;
				}
			}

			if (update) {
				// update local configuration
				configuration.update(dict);
			}

		} catch (IOException e) {
			LOG.error("Error parsing new configuiration values", e);
		}
	}

	private void requestConfiguration(Event event) {
		// a GW is asking for a
		// configuration because a local
		// one (on the GW) have been
		// deleted. Return the local
		// copy of matching
		// configuration
		JsonRpcRequest req = OSGiUtils.toRequest(event);
		// parse the received configuration object
		ConfigurationObject receivedConfigurationObject;
		try {
			receivedConfigurationObject = mapper.readValue(req.getParams().getData(), ConfigurationObject.class);

			// get local copy of configuration for the target component by
			// PID
			Configuration configuration;
			Filter filter = context.getBundleContext().createFilter(
					"(" + Constants.SERVICE_PID + "="
							+ CommandLayerUtils.toManagerConfigurationPID(receivedConfigurationObject.getPID()) + ")");
			Configuration[] configurations = configurationAdmin.listConfigurations(filter.toString());

			JsonRpcResponse resp = new JsonRpcResponse();

			if (configurations == null) {
				LOG.error("Could not find a proper configuration");
				resp.setError(new Error(404, "Could not find any configuration stored on the manager"));
			}

			if (configurations.length > 1) {
				resp.setError(new Error(500, "Found multiple configurations matching the filter" + filter));
			}

			// no error in getting configuration
			if (resp.getError() == null) {
				configuration = configurationAdmin.getConfiguration(
				// translate the PID specified by the gateway to the one to
				// be specified in the manager
						CommandLayerUtils.toManagerConfigurationPID(receivedConfigurationObject.getPID()));

				// prepare the configuration object with properties values
				// for the gateway
				ConfigurationObject newConfigurationObject = new ConfigurationObject(
						receivedConfigurationObject.getPID(), OSGiUtils.dictionaryToMap(configuration.getProperties()));

				// put it inside the response
				resp.getResult().setData(mapper.writeValueAsString(newConfigurationObject));
			}

			// set the right ID in the response
			resp.setId(req.getId());

			// send the response
			this.clientFactory.createJsonRpcClient(req.getParams().getSource()).post(resp);

		} catch (IOException | InvalidSyntaxException e) {
			LOG.error("Error, respond to GW configuration request from " + req.getParams().getSource(), e);
		}

	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case OSGI_CONFIGURATIONUPDATED_TOPIC:
			updateOsgiConfiguration(event);
			break;
		case JSONRPC_POST_NOTIFICATION_EVENT_TOPIC:
			postNotificationEvent(event);
			break;
		case JSONRPC_GET_REQUEST_EVENT_TOPIC:
			requestConfiguration(event);
			break;
		default:
			LOG.error("Unhandled topic: " + event.getTopic());
			break;
		}
	}

	public void bindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void unbindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {//NOSONAR squid:S1172
		this.clientFactory = null;
	}

	public void bindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {
		this.communicationLayerMGR = communicationLayerMGR;
	}

	public void unbindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {//NOSONAR squid:S1172
		this.communicationLayerMGR = null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172
		this.configurationAdmin = null;
	}

}
