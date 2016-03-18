package eu.greencom.mgm.servicebus.types;

import com.microsoft.windowsazure.services.servicebus.implementation.BrokerProperties;

public interface ICallback<T>{

	public void callback(T message, BrokerProperties msgProps);

}