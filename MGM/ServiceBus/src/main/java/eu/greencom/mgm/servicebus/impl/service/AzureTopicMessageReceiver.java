package eu.greencom.mgm.servicebus.impl.service;

import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;

import eu.greencom.mgm.servicebus.types.ICallback;

// Sonar was complaining about "_name", renamed to "localName", to avoid naming collision
public class AzureTopicMessageReceiver<T> implements Runnable {

	private static final Logger LOG = LoggerFactory
			.getLogger(AzureTopicMessageReceiver.class.getName());
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	ICallback<T> localReceiveHandler;
	String localTopicName;
	String localSubscriptionName;
	ReceiveMessageOptions localOpts;

	Class<T> localClazz;
	ServiceBusContract client;

	Gson gson = new Gson();

	public AzureTopicMessageReceiver(ServiceBusContract client,
			ICallback<T> receiveHandler, String topicName,
			String subscriptionName, ReceiveMessageOptions opts, Class<T> clazz) {
		localReceiveHandler = receiveHandler;
		localTopicName = topicName;
		localSubscriptionName = subscriptionName;
		localOpts = opts;
		localClazz = clazz;
		this.client = client;
	}

	@Override
	public void run() {// NOSONAR squid:MethodCyclomaticComplexity - JPU: most
						// of the "complexity" is exception handling

		ReceiveSubscriptionMessageResult resultQM;
		BrokeredMessage message = null;

		while (!Thread.currentThread().isInterrupted()) {
			InputStreamReader reader = null;
			try {
				resultQM = client.receiveSubscriptionMessage(localTopicName,
						localSubscriptionName, localOpts);
				message = resultQM.getValue();
				if (message != null && message.getMessageId() != null) {
					reader = new InputStreamReader(message.getBody(),DEFAULT_CHARSET);
					String result = "";
					while (reader.ready()) {
						result += (char) reader.read();
					}
					localReceiveHandler.callback(
							gson.fromJson(result, localClazz),
							message.getBrokerProperties());
				}
			} catch (ServiceException e) {
				LOG.error("ServiceException encountered:", e);
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
					LOG.error("Execution failed", ex);
				}
			} catch (Exception ex) {
				LOG.error("generic exception encountered:", ex);
				try {

					Thread.sleep(2000);
				} catch (Exception exx) {
					LOG.error("Execution failed", exx);
				}
				// Indicate a problem, unlock message in topic
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (Exception e) {
					LOG.error("Failed to close the input stream", e);
				}

				if (localOpts.isPeekLock() && message != null) {
					try {
						client.unlockMessage(message);
					} catch (ServiceException e) {
						LOG.error("Execution failed, message unlocked", e);
					}
				}
			}
		}
	}
}
