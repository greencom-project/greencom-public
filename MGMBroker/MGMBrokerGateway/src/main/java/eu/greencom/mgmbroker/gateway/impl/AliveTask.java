package eu.greencom.mgmbroker.gateway.impl;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageBroker;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageManager;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.Method;
import eu.linksmart.network.jsonrpc.exception.InvalidClientException;

public class AliveTask extends TimerTask {
	
	private JsonRpcClientFactory jsonRpcClientFactory;
	private String managerID;

	private final static Logger LOG=Logger.getLogger(AliveTask.class);
	
	@Override
	public void run() {
		
		LOG.info("Trying to send alive notification to "+managerID);
		
		JsonRpcMessageBroker client= this.jsonRpcClientFactory.createJsonRpcClient(managerID);
		JsonRpcNotification notification=new JsonRpcNotification();
		notification.setMethod(Method.POST);
		notification.getParams().setUri("gc:AliveMessage");
		
		
		String result=client.post(notification);
		LOG.info("Alive notification result: "+result);
		
	}

	public void setJsonRpcClientFactory(
			JsonRpcClientFactory jsonRpcClientFactory) {
		this.jsonRpcClientFactory=jsonRpcClientFactory;
	}

	public void setManagerID(String managerID) {
		this.managerID=managerID;
	}

}
