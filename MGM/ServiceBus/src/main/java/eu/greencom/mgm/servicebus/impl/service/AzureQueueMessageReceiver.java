package eu.greencom.mgm.servicebus.impl.service;

import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;

import eu.greencom.mgm.servicebus.types.ICallback;

// Sonar was complaining about "_name", renamed to "localName", to avoid naming collision
public class AzureQueueMessageReceiver<T> implements Runnable {

	private static final Logger LOG = LoggerFactory
			.getLogger(AzureQueueMessageReceiver.class.getName());
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	ICallback<T> localReceiveHandler;
	String localQueueName;
	ReceiveMessageOptions localOpts;
	Class<T> localClazz;
	ServiceBusContract client;
	Gson gson = new Gson();

	public AzureQueueMessageReceiver(ServiceBusContract client,
			ICallback<T> receiveHandler, String queueName,
			ReceiveMessageOptions opts, Class<T> clazz) {
		localReceiveHandler = receiveHandler;
		localQueueName = queueName;
		localOpts = opts;
		localClazz = clazz;
		this.client = client;
	}

	@Override
	public void run() {// NOSONAR squid:MethodCyclomaticComplexity - JPU: most
						// of the "complexity" is exception handling

		ReceiveQueueMessageResult resultQM;
		BrokeredMessage message = null;

		while (!Thread.currentThread().isInterrupted()) {
			InputStreamReader reader = null;
			try {
				resultQM = client
						.receiveQueueMessage(localQueueName, localOpts);
				message = resultQM.getValue();
				if (message != null && message.getMessageId() != null) {
					LOG.debug("Message received");
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
				LOG.error("ServiceException encountered: ", e);
				try {
					Thread.sleep(2000);
				} catch (Exception exx) {
					LOG.error("Failed to handle ServiceException:", exx);
				}
			} catch (Exception e) {
				LOG.error("Generic exception encountered: ", e);
				try {
					Thread.sleep(2000);
				} catch (Exception exx) {
					LOG.error("Failed to handle generic exception: ", exx);
				}
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
					} catch (Exception e) {
						LOG.error("Execution failed", e);
					}
				}
			}
		}
	}
}
