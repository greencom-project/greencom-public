package eu.greencom.mgmbroker.manager.impl;



import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import eu.greencom.mgmbroker.manager.api.ControlLayerMGR;
import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.Method;

public class ControlLayerMGRImpl implements ControlLayerMGR {

	private static final Logger LOG = Logger.getLogger(ControlLayerMGR.class.getName());
	
	private JsonRpcClientFactory clientFactory;	
	private ObjectMapper mapper;

	public void activate(ComponentContext context) {		
		mapper = new ObjectMapper();
		// properly serialize and deserialize Joda's DateTime
		mapper.registerModule(new JodaModule());
		LOG.debug(context.getProperties().get(ComponentConstants.COMPONENT_NAME)+" activated");
	}
	
	public void deactivate(ComponentContext context) {
		LOG.debug(context.getProperties().get(ComponentConstants.COMPONENT_NAME)+" deactivated");
	}

	@Override
	public void changeDeviceState(String gatewayID, String deviceID, boolean state) {

		JsonRpcNotification notification = new JsonRpcNotification();
		notification.setMethod(Method.POST);
		notification.getParams().setUri("gc:DeviceState");
		notification.getParams().setData(deviceID + ":" + state);

		clientFactory.createJsonRpcClient(gatewayID).post(notification);
	}

	public void bindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void unbindJsonRpcClientFactory(JsonRpcClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	

}
