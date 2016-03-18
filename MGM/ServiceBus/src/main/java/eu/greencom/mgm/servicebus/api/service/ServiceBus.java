package eu.greencom.mgm.servicebus.api.service;

import java.util.Map;

import com.microsoft.windowsazure.services.servicebus.implementation.BrokerProperties;

import eu.greencom.mgm.servicebus.types.ICallback;

@SuppressWarnings("all")
// JPU: suppress "squid.S00112", the API does not define own exception classes
public interface ServiceBus {

	public ServiceBus setupServiceBus();

	public ServiceBus setupServiceBus(String namespace, String issuer,
			String issuerKey);

	// Topic Methods
	public <T> ServiceBus subscribeTopic(ICallback<T> receiveHandler,
			String topicName, String subscriptionName, int receiveMode,
			Class<T> clazz);

	public <T> ServiceBus subscribeTopicWithoutChecking(
			ICallback<T> receiveHandler, String topicName,
			String subscriptionName, int receiveMode, Class<T> clazz);

	public <T> ServiceBus subscribeTopicWithFilter(ICallback<T> receiveHandler,
			String topicName, String subscriptionName, int receiveMode,
			Class<T> clazz, String filterName, String filterStr);

	public ServiceBus publishTopicMessage(Object message, String topicName)
			throws Exception;

	public ServiceBus publishTopicMessageWithProperties(Object message,
			String topicName, Map<String, Object> prop) throws Exception;

	public ServiceBus deleteTopic(String path);

	public ServiceBus deleteTopicSubscription(String topicPath, String name);

	// Queue Methods
	public ServiceBus publishQueueMessage(Object message, String queueName)
			throws Exception;

	@SuppressWarnings("squid:S00112")
	public ServiceBus publishQueueMessage(Object message, String queueName,
			BrokerProperties msgProps) throws Exception;

	@SuppressWarnings("squid:S00112")
	public ServiceBus publishQueueMessageWithProperties(Object message,
			String queueName, Map<String, Object> prop) throws Exception;

	public ServiceBus publishQueueMessageWithProperties(Object message,
			String queueName, Map<String, Object> prop,
			BrokerProperties msgProps) throws Exception;

	public <T> ServiceBus subscribeQueue(ICallback<T> receiveHandler,
			String queueName, int receiveMode, Class<T> clazz);

	public ServiceBus clearTopics();

	public ServiceBus close();

}
