package eu.greencom.mgmbroker.gateway.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
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
import eu.greencom.mgmbroker.gateway.CommunicationLayerService;
import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.api.Method;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;

/**
 * The components is an event handler for these events:
 * <ul>
 * <li>Configuration Updated: a local configuration have been updated, notify
 * the MGM</li>
 * <li>Configuration Deleted: a local configuration have been deleted, try to
 * get a fresh one from MGM</li>
 * <li>JSONRPC POST Notification: the MGM is updating a local copy of the
 * configuration</li>
 * </ul>
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class CommandLayerGW implements EventHandler {

	// to get configuration updates
	public static final String OSGI_CONFIGURATIONUPDATED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_UPDATED";
	public static final String OSGI_CONFIGURATIONDELETED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_DELETED";
	// To receive JSONRPC notifications
	public static final String JSONRPC_POST_NOTIFICATION_EVENT_TOPIC = "event/jsonrpc/notification/POST";

	private ConfigurationAdmin configurationAdmin;
	private JsonRpcClientFactory clientFactory;
	private CommunicationLayerService communicationLayerService;

	private ComponentContext context;// NOSONAR squid:S1068 - JPU: Context used
										// to create fileter etc.

	private ObjectMapper mapper;

	private static final Logger LOG = Logger.getLogger(CommandLayerGW.class);

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void bindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void unbindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {// NOSONAR
																				// squid:S1172
																				// -
																				// JPU:
																				// Parameter
																				// expected
																				// by
																				// SCR
																				// Runtime

		this.clientFactory = null;
	}

	public void bindCommunicationLayerService(CommunicationLayerService communicationLayerService) {
		this.communicationLayerService = communicationLayerService;
	}

	public void unbindCommunicationLayerService(CommunicationLayerService communicationLayerService) {// NOSONAR
																										// squid:S1172
																										// -
																										// JPU:
																										// Parameter
																										// expected
																										// by
																										// SCR
																										// Runtime

		this.communicationLayerService = null;
	}

	@SuppressWarnings("unchecked")
	public void activate(ComponentContext context) {
		this.context = context;
		LOG.info("Activating " + context.getBundleContext().getBundle().getSymbolicName());

		mapper = new ObjectMapper();

		// properly serialize and deserialize Joda's DateTime
		mapper.registerModule(new JodaModule());

		// register as received of configuration POST events
		String[] topics = new String[] {
				// To receive JSONRPC notifications
				JSONRPC_POST_NOTIFICATION_EVENT_TOPIC,
				// To get notified when a local component configuration have
				// been updated
				OSGI_CONFIGURATIONUPDATED_TOPIC,
				// Liste to configuration deleted event: CommandLayer will try
				// to obtain a new configuration from the MGM
				OSGI_CONFIGURATIONDELETED_TOPIC };
		@SuppressWarnings("rawtypes")
		Dictionary properties = new Hashtable();// NOSONAR squid:S1319 - JPU:
												// Concrete type expected by
												// method signature
		properties.put(EventConstants.EVENT_TOPIC, topics);
		String filterString = "("
				// Events could be either remote ConfigurationUpdate messages or
				// local updates of configurations to be reported to the MGM
				+ "|" + "(uri=gc:ConfigurationMessage)" + "(cm.pid=" + CommandLayerUtils.GW_CONFIGURATION_PID_PREFIX
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

	private void handleConfigurationRequest(Event event) {
		JsonRpcNotification notification = OSGiUtils.toNotification(event);
		LOG.info("Received a request to update local configuration from manager "
				+ notification.getParams().getSource());
		// update local configuration with received data
		try {
			ConfigurationObject conf = mapper.readValue(notification.getParams().getData(), ConfigurationObject.class);
			sendConfigurationToComponents(conf);
		} catch (Exception e) {
			LOG.error("Error updating configuration", e);
			return;
		}
	}

	private void handleConfigurationUpdate(Event event) {
		String pid = (String) event.getProperty("cm.pid");// get the PID of
		// the
		// configuration
		Configuration conf = null;
		try {
			conf = configurationAdmin.getConfiguration(pid);
		} catch (IOException e) {
			LOG.error("Error getting configuration for pid: " + pid, e);
			return;
		}
		sendConfigurationEvent(new ConfigurationObject(pid, OSGiUtils.dictionaryToMap(conf.getProperties())));
	}

	private void handleConfigurationDelete(Event event) {
		// Get the PID of the configuration
		String cmPid = (String) event.getProperty("cm.pid");
		/*
		 * If a local configuration have been deleted, try to get a new one from
		 * the manager
		 */
		JsonRpcRequest request = new JsonRpcRequest();
		request.getParams().setUri("gc:ConfigurationMessage");
		request.setMethod(Method.GET);
		try {
			request.getParams().setData(mapper.writeValueAsString(new ConfigurationObject(cmPid, null)));
		} catch (JsonProcessingException e) {
			LOG.error("Error serializing Configuration Object", e);
			return;
		}
		JsonRpcResponse resp = this.clientFactory.createJsonRpcClient(communicationLayerService.getManagerID()).post(
				request);
		if (resp == null) {
			LOG.error("Error receiving response");
			return;
		}
		if (resp.getError() != null) {
			LOG.warn("Could not get a valid configuration from the Manager. ERROR code: " + resp.getError().getCode()
					+ " data:" + resp.getError().getData());
			return;
		}

		ConfigurationObject newConf = null;

		try {
			newConf = mapper.readValue(resp.getResult().getData(), ConfigurationObject.class);
			sendConfigurationToComponents(newConf);
		} catch (IOException | InvalidSyntaxException e) {
			LOG.error("Error updating configuration configuration from response", e);
			return;
		}

	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {// NOSONAR squid:S1151 - JPU: Most of the
									// lines are explanatory comments
		case JSONRPC_POST_NOTIFICATION_EVENT_TOPIC:// NOSONAR squid:S1151
			/*
			 * If a configuration event was received from MGM update local
			 * configuration
			 */
			handleConfigurationRequest(event);
			break;
		case OSGI_CONFIGURATIONUPDATED_TOPIC:// NOSONAR squid:S1151
			/*
			 * If a local configuration has been updated, send it to the manager
			 * in case there is a new property
			 */
			handleConfigurationUpdate(event);
			break;

		case OSGI_CONFIGURATIONDELETED_TOPIC:
			handleConfigurationDelete(event);
			break;

		default:
			LOG.warn("Unhandled topic" + event.getTopic());
			return;
		}

	}

	private void sendConfigurationToComponents(ConfigurationObject newConfiguration) throws InvalidSyntaxException,
			IOException {
		// find configuration PID to be updated all configurations to be Updated
		// Location argument (second) set to null: so the configuration is not
		// bound to this bundle!
		Configuration configuration = configurationAdmin.getConfiguration(newConfiguration.getPID(), null);

		// update the values
		Dictionary newValues = OSGiUtils.mapToDictionary(newConfiguration.getValues());
		if (newValues != null) {
			configuration.update(newValues);
		}

	}

	public void sendConfigurationEvent(ConfigurationObject newConfiguration) {
		JsonRpcNotification notification = new JsonRpcNotification();
		notification.setMethod(Method.POST);
		notification.getParams().setUri("gc:ConfigurationMessage");
		try {
			notification.getParams().setData(mapper.writeValueAsString(newConfiguration));
		} catch (JsonProcessingException e) {
			LOG.error("Unable to serialize configuration", e);
			return;
		}
		this.clientFactory.createJsonRpcClient(communicationLayerService.getManagerID()).post(notification);
	}

}
