package eu.greencom.mgm.servicebus.impl.service;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.implementation.BrokerProperties;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.CreateTopicResult;
import com.microsoft.windowsazure.services.servicebus.models.GetTopicResult;
import com.microsoft.windowsazure.services.servicebus.models.ListSubscriptionsResult;
import com.microsoft.windowsazure.services.servicebus.models.ListTopicsResult;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.RuleInfo;
import com.microsoft.windowsazure.services.servicebus.models.SubscriptionInfo;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;

import eu.greencom.mgm.servicebus.api.service.ServiceBus;
import eu.greencom.mgm.servicebus.types.ICallback;

// Sonar was complaining about "_names, renamed to "localName", to avoid naming collision
public class ServiceBusImpl implements ServiceBus {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceBus.class
			.getName());

	private String localNamespace;
	private String localIssuer;
	private String localIssuerKey;

	private ServiceBusContract client;

	private String localServiceRootUri;
	private String localWrapRootUri;

	private Map<String, Thread> localSubscribers;
	private Map<String, Thread> localQueueReceivers;

	private Gson gson = new Gson();

	public ServiceBusImpl() {
		localSubscribers = new HashMap<String, Thread>();
		localQueueReceivers = new HashMap<String, Thread>();
	}

	@Override
	public ServiceBusImpl setupServiceBus() {
		localNamespace = "nassistSB";
		localIssuer = "owner";
		localIssuerKey = "Ldo/kSeZbAJyZwFovKnznFMZT4wVyvNW12qlVUNVI7E=";
		localServiceRootUri = ".servicebus.windows.net";
		localWrapRootUri = "-sb.accesscontrol.windows.net/WRAPv0.9";

		client = createClient();

		return this;
	}

	@Override
	public ServiceBusImpl setupServiceBus(String namespace, String issuer,
			String issuerKey) {
		try {
			localNamespace = namespace;
			localIssuer = issuer;
			localIssuerKey = issuerKey;
			localServiceRootUri = ".servicebus.windows.net";
			localWrapRootUri = "-sb.accesscontrol.windows.net/WRAPv0.9";

			client = createClient();
		} catch (Exception e) {
			LOG.error("Setup of the service bus failed", e);
		}
		return this;
	}

	private ServiceBusContract createClient() {
		// TODO this is a workaround for ServiceLoader (doesn't get on well with
		// OSGi environment)
		ClassLoader aux = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				ServiceBusImpl.class.getClassLoader());
		Configuration config = ServiceBusConfiguration
				.configureWithWrapAuthentication(localNamespace, localIssuer,
						localIssuerKey, localServiceRootUri, localWrapRootUri);

		config.setProperty(Configuration.PROPERTY_CONNECT_TIMEOUT, new Integer(
				180000));
		config.setProperty(Configuration.PROPERTY_READ_TIMEOUT, new Integer(
				180000));

		ServiceBusContract contract = ServiceBusService.create(config);

		Thread.currentThread().setContextClassLoader(aux);

		return contract;
	}

	@Override
	public ServiceBusImpl close() {
		for (Entry<String, Thread> entry : localSubscribers.entrySet()) {
			entry.getValue().interrupt();
		}

		for (Entry<String, Thread> entry : localQueueReceivers.entrySet()) {
			entry.getValue().interrupt();
		}

		try {
			for (Entry<String, Thread> entry : localSubscribers.entrySet()) {
				entry.getValue().join(200);
			}

			for (Entry<String, Thread> entry : localQueueReceivers.entrySet()) {
				entry.getValue().join(200);
			}
		} catch (Exception e) {
			LOG.error("Failed to close", e);
		}

		return this;
	}

	// Topic
	@Override
	public <T> ServiceBusImpl subscribeTopic(ICallback<T> receiveHandler,
			String topicName, String subscriptionName, int receiveMode,
			Class<T> clazz) {
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;

		opts.setReceiveMode(receiveMode == 0 ? ReceiveMode.RECEIVE_AND_DELETE
				: ReceiveMode.PEEK_LOCK);

		// Check for topic and subscription, if do not exist create them

		try {

			if (existTopic(topicName)) {
				GetTopicResult resultGetTopic = client.getTopic(topicName);

				if (existSubscription(topicName, subscriptionName)) {
					client.getSubscription(resultGetTopic.getValue().getPath(),
							subscriptionName);
				} else {
					client.createSubscription(resultGetTopic.getValue()
							.getPath(), new SubscriptionInfo(subscriptionName));
				}

			} else {
				CreateTopicResult topicResult = client
						.createTopic(new TopicInfo(topicName));
				client.createSubscription(topicResult.getValue().getPath(),
						new SubscriptionInfo(subscriptionName));
			}

			// Here we have topic and subscription
			// start receiving

			Thread t = new Thread(new AzureTopicMessageReceiver<T>(client,
					receiveHandler, topicName, subscriptionName, opts, clazz));

			localSubscribers.put(topicName, t);

			t.start();
		} catch (Exception e) {
			LOG.error("Failed to subscribe to topic {}: {}", topicName, e);
		}

		return this;
	}

	// Subscribe to any kind of existing subscriptions
	@Override
	public <T> ServiceBusImpl subscribeTopicWithoutChecking(
			ICallback<T> receiveHandler, String topicName,
			String subscriptionName, int receiveMode, Class<T> clazz) {
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;

		opts.setReceiveMode(receiveMode == 0 ? ReceiveMode.RECEIVE_AND_DELETE
				: ReceiveMode.PEEK_LOCK);

		// Do not check for topic / subscription existance

		try {
			Thread t = new Thread(new AzureTopicMessageReceiver<T>(client,
					receiveHandler, topicName, subscriptionName, opts, clazz));

			localSubscribers.put(topicName, t);

			t.start();
		} catch (Exception e) {
			LOG.error("Failed to subscribe to topic {}: {}", topicName, e);
		}

		return this;
	}

	@Override
	public <T> ServiceBusImpl subscribeTopicWithFilter(
			ICallback<T> receiveHandler, String topicName,
			String subscriptionName, int receiveMode, Class<T> clazz,
			String filterName, String filterStr) {
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;

		opts.setReceiveMode(receiveMode == 0 ? ReceiveMode.RECEIVE_AND_DELETE
				: ReceiveMode.PEEK_LOCK);

		// Check for topic and subscription, if do not exist create them

		try {

			if (existTopic(topicName)) {
				GetTopicResult resultGetTopic = client.getTopic(topicName);

				if (existSubscription(topicName, subscriptionName)) {
					client.getSubscription(resultGetTopic.getValue().getPath(),
							subscriptionName);
				} else {
					client.createSubscription(resultGetTopic.getValue()
							.getPath(), new SubscriptionInfo(subscriptionName));
					RuleInfo ruleInfo = new RuleInfo(filterName);
					ruleInfo = ruleInfo.withSqlExpressionFilter(filterStr);
					client.createRule(resultGetTopic.getValue().getPath(),
							subscriptionName, ruleInfo);
					client.deleteRule(resultGetTopic.getValue().getPath(),
							subscriptionName, "$Default");
				}
			} else {
				CreateTopicResult topicResult = client
						.createTopic(new TopicInfo(topicName));

				client.createSubscription(topicResult.getValue().getPath(),
						new SubscriptionInfo(subscriptionName));
				RuleInfo ruleInfo = new RuleInfo(filterName);
				ruleInfo = ruleInfo.withSqlExpressionFilter(filterStr);
				client.createRule(topicResult.getValue().getPath(),
						subscriptionName, ruleInfo);
				client.deleteRule(topicResult.getValue().getPath(),
						subscriptionName, "$Default");
			}

			// Here we have topic and subscription
			// start receiving

			Thread t = new Thread(new AzureTopicMessageReceiver<T>(client,
					receiveHandler, topicName, subscriptionName, opts, clazz));

			localSubscribers.put(topicName, t);

			t.start();
		} catch (Exception e) {
			LOG.error("Failed to subscribe to topic {}: {}", topicName, e);
		}

		return this;
	}

	@Override
	public ServiceBusImpl publishTopicMessage(Object message, String topicName)
			throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(gson.toJson(message));
			client.sendTopicMessage(topicName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}

	@Override
	public ServiceBusImpl publishTopicMessageWithProperties(Object message,
			String topicName, Map<String, Object> prop) throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(gson.toJson(message));

			for (Entry<String, Object> e : prop.entrySet()) {
				msg.setProperty(e.getKey(), e.getValue());
			}

			client.sendTopicMessage(topicName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}

	@Override
	public ServiceBusImpl clearTopics() {
		try {
			for (String entry : localSubscribers.keySet()) {
				client.deleteTopic(entry);
			}
		} catch (ServiceException e) {
			LOG.error("Failed to clear topics", e);
		}

		return this;
	}
	@Override
	public ServiceBusImpl deleteTopic(String path) {
		try {
			client.deleteTopic(path);
		} catch (ServiceException e) {
			LOG.error("Failed to delete topic {}: {}", path, e);

		}

		return this;
	}
	@Override
	public ServiceBusImpl deleteTopicSubscription(String topicPath, String name) {
		try {
			client.deleteSubscription(topicPath, name);
		} catch (ServiceException e) {
			LOG.error("Failed to delete topic subscription {}: {}", name, e);
		}

		return this;
	}

	// Queue methods
	@Override
	public ServiceBusImpl publishQueueMessage(Object message, String queueName)
			throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(gson.toJson(message));
			client.sendQueueMessage(queueName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}
	@Override
	public ServiceBusImpl publishQueueMessage(Object message, String queueName,
			BrokerProperties msgProps) throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(msgProps);
			msg.setBody(new ByteArrayInputStream(gson.toJson(message).getBytes(
					"UTF-8")));
			client.sendQueueMessage(queueName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}
	@Override
	public ServiceBusImpl publishQueueMessageWithProperties(Object message,
			String queueName, Map<String, Object> prop) throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(gson.toJson(message));

			for (Entry<String, Object> e : prop.entrySet()) {
				msg.setProperty(e.getKey(), e.getValue());
			}

			client.sendQueueMessage(queueName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}
	@Override
	public ServiceBusImpl publishQueueMessageWithProperties(Object message,
			String queueName, Map<String, Object> prop,
			BrokerProperties msgProps) throws Exception {
		try {
			BrokeredMessage msg = new BrokeredMessage(msgProps);
			msg.setBody(new ByteArrayInputStream(gson.toJson(message).getBytes(
					"UTF-8")));

			for (Entry<String, Object> e : prop.entrySet()) {
				msg.setProperty(e.getKey(), e.getValue());
			}

			client.sendQueueMessage(queueName, msg);
		} catch (Exception x) {
			throw x;
		}

		return this;
	}
	@Override
	public <T> ServiceBusImpl subscribeQueue(ICallback<T> receiveHandler,
			String queueName, int receiveMode, Class<T> clazz) {
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;

		opts.setReceiveMode(receiveMode == 0 ? ReceiveMode.RECEIVE_AND_DELETE
				: ReceiveMode.PEEK_LOCK);

		try {
			Thread t = new Thread(new AzureQueueMessageReceiver<T>(client,
					receiveHandler, queueName, opts, clazz));

			localQueueReceivers.put(queueName, t);

			t.start();
		} catch (Exception e) {
			LOG.error("Failed to subscribe to queue {}: {}", queueName, e);
		}

		return this;
	}

	// Helper methods

	private boolean existSubscription(String topicName, String subscriptionName) {
		try {
			ListSubscriptionsResult result = client
					.listSubscriptions(topicName);
			for (SubscriptionInfo info : result.getItems()) {
				if (info.getName().equals(subscriptionName)) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			LOG.error("Failed to test whether subscription {} exists: {}",
					subscriptionName, e);
			return false;
		}
	}

	private boolean existTopic(String topicName) {
		try {
			ListTopicsResult result = client.listTopics();
			for (TopicInfo info : result.getItems()) {
				if (info.getPath().equals(topicName)) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			LOG.error("Failed to test whether topic {} exists: {}", topicName,
					e);
			return false;
		}
	}

}