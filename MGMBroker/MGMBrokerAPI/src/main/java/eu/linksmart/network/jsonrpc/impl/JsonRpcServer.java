package eu.linksmart.network.jsonrpc.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.linksmart.network.jsonrpc.api.JsonRpcMessageManager;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

/**
 * The class implementing a @JsonRpcServer that creates LinkSmart Data Backbone
 * endpoint, delegating to a @JsonRpcMessageManager the message flow
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcServer implements ManagedService {

	private static final String SERVER_ID_PROPERTY = "JSONRPC_SERVER_ID";

	private ComponentContext context;

	private ConfigurationAdmin configAdmin;
	private NetworkManager networkManager;
	private EventAdmin eventAdmin;
	private JsonRpcMessageManager jsonRpcMessageManager;

	private LinkSmartDataServiceProxy notificationProxy;
	private LinkSmartDataServiceProxy requestProxy;
	private LinkSmartDataServiceProxy responseProxy;

	private String serverId;
	private ServiceRegistration serviceRegistration;

	private static final Logger LOG = Logger.getLogger(JsonRpcServer.class);

	public void activate(ComponentContext context) {
		this.context = context;

		Dictionary<String, Object> serviceProps = new Hashtable<String, Object>();//NOSONAR squid:S1149
		serviceProps.put(Constants.SERVICE_PID, getConfigurationServiceName());
		context.getBundleContext().registerService(ManagedService.class, this, serviceProps);
	}

	public void deactivate(ComponentContext context) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR API
		this.stopEndPoints();
	}

	private void startEndPoints() {
		LOG.info("Starting endpoints");
		notificationProxy.startEndPoint();
		requestProxy.startEndPoint();
		responseProxy.startEndPoint();
	}

	private void registerService() {
		Dictionary<String, Object> serviceProps = new Hashtable<String, Object>();//NOSONAR squid:S1149 - Dictionary required by API
		serviceProps.put(Constants.SERVICE_PID, getServiceName());
		serviceRegistration = context.getBundleContext().registerService(JsonRpcServer.class, this, serviceProps);
	}

	private void unregisterService() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private void stopEndPoints() {
		LOG.info("Stopping endpoints");
		if (notificationProxy != null) {
			notificationProxy.stopEndPoint();
		}
		if (requestProxy != null) {
			requestProxy.stopEndPoint();
		}
		if (responseProxy != null) {
			responseProxy.stopEndPoint();
		}
	}

	// Add attributes to linksmart services
	private void setServiceParts() {
		if (notificationProxy != null) {
			notificationProxy.setServiceParts(new Part[] { new Part("SERVICE_TYPE", "JSON-RPC_NOTIFICATION") });
		}
		if (requestProxy != null) {
			requestProxy.setServiceParts(new Part[] { new Part("SERVICE_TYPE", "JSON-RPC_REQUEST") });
		}
		if (responseProxy != null) {
			responseProxy.setServiceParts(new Part[] { new Part("SERVICE_TYPE", "JSON-RPC_RESPONSE") });
		}
	}

	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		// check configuration and start endpoints if config param is available
		if (properties == null) {
			Properties props = new Properties();
			props.put(SERVER_ID_PROPERTY, "");
			try {
				configAdmin.getConfiguration(getConfigurationServiceName()).update((Dictionary) props);
			} catch (IOException e) {
				LOG.error("Error setting defautl properties",e);
			}
			return;
		}

		checkConfigurationChanges(properties);
	}

	public void checkConfigurationChanges(Dictionary<String, ?> properties) {

		if (serverId != null && serverId.equals(properties.get(SERVER_ID_PROPERTY))) {
			// nothing changed, nothing to do
			return;
		}

		// update SERVER_ID and re-register the LinkSmart service
		serverId = (String) properties.get(SERVER_ID_PROPERTY);

		if ("".equals(serverId)) {
			// not yet configured, do nothing
			return;
		}

		// restart endpoints, configuration has changed (or start them for the
		// first time)
		stopEndPoints();

		unregisterService();

		initEndPoints();

		setServiceParts();

		startEndPoints();

		registerService();
	}

	/**
	 * Creates endpoints which will dispatch as OSGi events received messages
	 */
	private void initEndPoints() {
		notificationProxy = new LinkSmartDataServiceProxy(context, new ObjectMapper(), "NotificationProxy_" + serverId,
				networkManager, new LinkSmartDataServiceCallback<JsonRpcNotification>() {
					private EventAdmin eventAdmin;
					private JsonRpcMessageManager jsonRpcMessageManager;
					private Logger log = Logger.getLogger(LinkSmartDataServiceCallback.class);//NOSONAR squid:S1312 - JPU: o.k. to have local logger instance

					@Override
					public void notify(JsonRpcNotification notification) {
						log.debug("Received a Notification");
						jsonRpcMessageManager.receiveMessage(notification, eventAdmin);
					}

					public LinkSmartDataServiceCallback<JsonRpcNotification> init(EventAdmin eventAdmin,
							JsonRpcMessageManager jsonRpcMessageManager) {
						this.eventAdmin = eventAdmin;
						this.jsonRpcMessageManager = jsonRpcMessageManager;
						return this;
					}
				}.init(eventAdmin, jsonRpcMessageManager), JsonRpcNotification.class);

		requestProxy = new LinkSmartDataServiceProxy(context, new ObjectMapper(), "RequestProxy_" + serverId,
				networkManager, new LinkSmartDataServiceCallback<JsonRpcRequest>() {
					private EventAdmin eventAdmin;
					private JsonRpcMessageManager jsonRpcMessageManager;

					@Override
					public void notify(JsonRpcRequest request) {
						jsonRpcMessageManager.receiveMessage(request, eventAdmin);
					}

					public LinkSmartDataServiceCallback<JsonRpcRequest> init(EventAdmin eventAdmin,
							JsonRpcMessageManager jsonRpcMessageManager) {
						this.eventAdmin = eventAdmin;
						this.jsonRpcMessageManager = jsonRpcMessageManager;
						return this;
					}
				}.init(eventAdmin, jsonRpcMessageManager), JsonRpcRequest.class);

		responseProxy = new LinkSmartDataServiceProxy(context, new ObjectMapper(), "ResponseProxy_" + serverId,
				networkManager, new LinkSmartDataServiceCallback<JsonRpcResponse>() {
					private EventAdmin eventAdmin;
					private JsonRpcMessageManager jsonRpcMessageManager;

					@Override
					public void notify(JsonRpcResponse response) {
						jsonRpcMessageManager.receiveMessage(response, eventAdmin);
					}

					public LinkSmartDataServiceCallback<JsonRpcResponse> init(EventAdmin eventAdmin,
							JsonRpcMessageManager jsonRpcMessageManager) {
						this.eventAdmin = eventAdmin;
						this.jsonRpcMessageManager = jsonRpcMessageManager;
						return this;
					}
				}.init(eventAdmin, jsonRpcMessageManager), JsonRpcResponse.class);
	}

	public void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void unbindEventAdmin(EventAdmin eventAdmin) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR API
		this.eventAdmin = null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configAdmin) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR API
		this.configAdmin = null;
	}

	public void bindNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public void unbindNetworkManager(NetworkManager networkManager) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR API
		this.networkManager = null;
	}

	public void bindJsonRpcMessageManager(JsonRpcMessageManager messageManager) {
		this.jsonRpcMessageManager = messageManager;
	}

	public void unbindJsonRpcMessageManager(JsonRpcMessageManager messageManager) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR API
		this.jsonRpcMessageManager = null;
	}

	public String getServerId() {
		return this.serverId;
	}

	public NetworkManager getNetworkManager() {
		return this.networkManager;
	}

	public JsonRpcMessageManager getJsonRpcMessageManager() {
		return this.jsonRpcMessageManager;
	}

	private String getConfigurationServiceName() {
		return "eu.linksmart.network.jsonrpc.server.configuration";
	}

	private String getServiceName() {
		return "eu.linksmart.network.jsonrpc.server";
	}

}
