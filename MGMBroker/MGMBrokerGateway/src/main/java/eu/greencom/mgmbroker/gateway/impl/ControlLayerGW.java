package eu.greencom.mgmbroker.gateway.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;

public class ControlLayerGW implements EventHandler{

	public void activate(ComponentContext context){
		String[] topics=new String[]{"event/jsonrpc/notification/POST"};
		@SuppressWarnings("rawtypes")
		Dictionary properties=new Hashtable();//NOSONAR squid:S1319 - JPU: Concrete type expected by method signature
		properties.put(EventConstants.EVENT_TOPIC, topics);
		properties.put(EventConstants.EVENT_FILTER, "(uri=gc:DeviceState)");
		context.getBundleContext().registerService(EventHandler.class, this, properties);
	}
	
	@Override
	public void handleEvent(Event arg0) {
		JsonRpcNotification notification=OSGiUtils.toNotification(arg0);//NOSONAR findbugs:DLS_DEAD_LOCAL_STORE 
		StringTokenizer tokenizer=new StringTokenizer(":");
		String deviceID=tokenizer.nextToken();//NOSONAR findbugs:DLS_DEAD_LOCAL_STORE 
		boolean state=Boolean.valueOf(tokenizer.nextToken());//NOSONAR findbugs:DLS_DEAD_LOCAL_STORE - JPU: To be extended
		//TODO: add here code that performs control on the heatpump through scheduler
	}

}
