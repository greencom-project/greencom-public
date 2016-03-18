package eu.greencom.mgmbroker.manager.impl;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import eu.greencom.mgmbroker.manager.api.CommunicationLayerMGR;
import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageBroker;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.api.Method;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;

/**
 * Service receiving events containing "alive" messages from MGMBroker. Only
 * process messages having uri=gc:AliveMessage and maintains a list of available
 * gateways
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 * 
 */
public class CommunicationLayerMGRImpl implements EventHandler, CommunicationLayerMGR {

	private ObjectMapper mapper;
	private JsonRpcClientFactory jsonRpcClientFactory;

	Map<String, Date> gateways = new HashMap<String, Date>();

	private static final  Logger LOG = Logger.getLogger(CommunicationLayerMGRImpl.class);

	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext context) {
		LOG.info("Activating " + context.getBundleContext().getBundle().getSymbolicName());

		mapper = new ObjectMapper();

		// properly serialize and deserialize Joda's DateTime
		mapper.registerModule(new JodaModule());
		
		String[] topics = new String[] { "event/jsonrpc/notification/POST" };
		@SuppressWarnings("rawtypes")
		Dictionary properties = new Hashtable();//NOSONAR squid:S1149
		properties.put(EventConstants.EVENT_TOPIC, topics);
		properties.put(EventConstants.EVENT_FILTER, "(uri=gc:AliveMessage)");

		context.getBundleContext().registerService(EventHandler.class, this, properties);

		LOG.debug("Started " + context.getBundleContext().getBundle().getSymbolicName());
	}

	@Override
	public void handleEvent(Event event) {

		LOG.debug("Received an event, topic:\"" + event.getTopic() + "\"");
		JsonRpcNotification notification = OSGiUtils.toNotification(event);
		LOG.info("Received an alive notification from: " + notification.getParams().getSource());
		gateways.put(notification.getParams().getSource(), new Date());


	}

	@Override
	public void cleanOldGateways() {
		Iterator<Map.Entry<String, Date>> iterator = gateways.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Date> e = iterator.next();
			Date d = e.getValue();
			// if the alive message arrived more than 5 minutes ago, remove from
			// list
			if (new Date().getTime() - d.getTime() > 10 * 60 * 1000) {
				iterator.remove();
			}
		}
	}

	@Override
	public Date solicitateAliveMessage(String gatewayId) {
		JsonRpcMessageBroker client = this.jsonRpcClientFactory.createJsonRpcClient(gatewayId);
		JsonRpcRequest request = new JsonRpcRequest();
		request.setMethod(Method.GET);
		request.getParams().setUri("gc:SolicitateAliveMessage");
		Date d = new Date();
		JsonRpcResponse response = client.post(request);
		if (response != null && response.getResult().getCode() == 200) {
			gateways.put(gatewayId, d);
			return d;
		}

		return null;

	}

	@Override
	public Map<String, Date> getGateways() {
		return gateways;
	}

	public void bindJsonRpcClientFactory(JsonRpcClientFactory jsonRpcClientFactory) {//NOSONAR squid:S1172
		this.jsonRpcClientFactory = jsonRpcClientFactory;
	}

	public void unbindJsonRpcClientFactory(JsonRpcClientFactory jsonRpcClientFactory) {//NOSONAR squid:S1172
		this.jsonRpcClientFactory = null;
	}

}
