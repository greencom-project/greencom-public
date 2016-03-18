package eu.greencom.mgmbroker.gateway.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import eu.greencom.mgmbroker.gateway.CommunicationLayerService;
import eu.greencom.mgmbroker.gateway.MGMBrokerGateway;
import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.api.Result;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;

public class CommunicationLayerImpl implements ManagedService, CommunicationLayerService, EventHandler {

	private static final Logger LOG = Logger.getLogger(CommunicationLayerImpl.class);

	private ConfigurationAdmin configurationAdmin;
	private ObjectMapper mapper;

	// the Id of the manager which is going to provide JsonRpcServer endpoints
	private String managerId;

	private Timer aliveTimer = new Timer();

	private ComponentContext context;
	private JsonRpcClientFactory jsonRpcClientFactory;

	private ServiceRegistration registration;

	public void activate(ComponentContext context) {
		this.context = context;

		Dictionary<String, Object> properties = new Hashtable<String, Object>();// NOSONAR squid:S1319 squid:S1149
																				// -
																				// JPU:
																				// Concrete
																				// type
																				// expected
																				// by
																				// method
																				// signature
		properties.put(Constants.SERVICE_PID, MGMBrokerGateway.getConfigurationPID());
		this.context.getBundleContext().registerService(ManagedService.class, this, properties);

		LOG.info("Activating " + context.getBundleContext().getBundle().getSymbolicName());

		mapper = new ObjectMapper();

		// properly serialize and deserialize Joda's DateTime
		mapper.registerModule(new JodaModule());
		String[] topics = new String[] { "event/jsonrpc/request/GET" };
		@SuppressWarnings("rawtypes")
		Dictionary evproperties = new Hashtable();// NOSONAR squid:S1319 squid:S1149 - JPU:
													// Concrete type expected by
													// method signature
		evproperties.put(EventConstants.EVENT_TOPIC, topics);
		evproperties.put(EventConstants.EVENT_FILTER, "(uri=gc:SolicitateAliveMessage)");

		context.getBundleContext().registerService(EventHandler.class, this, evproperties);

		LOG.debug("Started " + context.getBundleContext().getBundle().getSymbolicName());

	}

	public void bindJsonRpcClientFactory(JsonRpcClientFactory jsonRpcClientFactory) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		this.jsonRpcClientFactory = jsonRpcClientFactory;
	}

	public void unbindJsonRpcClientFactory(JsonRpcClientFactory jsonRpcClientFactory) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		this.jsonRpcClientFactory = null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		this.configurationAdmin = null;
	}

	public void deactivate(ComponentContext context2) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		stopTimer();
	}

	@Override
	public void updated(Dictionary<String, ?> properties) {
		if (properties == null) {
			Properties props = new Properties();
			props.put(MGMBrokerGateway.getManagerIDPropertyName(), "");
			try {
				configurationAdmin.getConfiguration(MGMBrokerGateway.getConfigurationPID()).update((Dictionary) props);
			} catch (IOException e) {
				LOG.error("Error setting defautl properties", e);
			}
			return;
		}
		if (managerId != null && managerId.equals((String) properties.get(MGMBrokerGateway.getManagerIDPropertyName()))) {
			// nothing changed, nothing to do
			return;
		}

		// update manager ID restart timer
		managerId = (String) properties.get(MGMBrokerGateway.getManagerIDPropertyName());
		if ("".equals(managerId)) {
			// not yet configured, do nothing
			return;
		}

		// restart timer
		stopTimer();

		unregisterService();

		registerService();

		startTimer();

	}

	private void registerService() {
		registration = this.context.getBundleContext().registerService(CommunicationLayerService.class, this, null);

	}

	private void unregisterService() {
		if (registration != null) {
			registration.unregister();
		}
	}

	private void stopTimer() {
		aliveTimer.cancel();
		aliveTimer.purge();
	}

	private void startTimer() {
		AliveTask task = new AliveTask();
		task.setJsonRpcClientFactory(jsonRpcClientFactory);
		task.setManagerID(managerId);
		aliveTimer = new Timer();
		// schedule an alive notification every 5 minutes
		aliveTimer.scheduleAtFixedRate(task, 1000, 10 * 1000);
	}

	@Override
	public String getManagerID() {
		return this.managerId;
	}

	@Override
	public void handleEvent(Event arg0) {

		JsonRpcRequest request = OSGiUtils.toRequest(arg0);
		JsonRpcResponse response = new JsonRpcResponse();
		response.setId(request.getId());

		LOG.info("Received a solicitation to send alive message from " + request.getParams().getSource());

		Result res = new Result();
		res.setCode(200);
		response.setResult(res);

		String result = jsonRpcClientFactory.createJsonRpcClient(request.getParams().getSource()).post(response);
		LOG.debug("Call result: " + result);

	}

}
