package eu.linksmart.network.jsonrpc.impl;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessage;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageHandler;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageManager;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.utils.OSGiUtils;
import eu.linksmart.network.networkmanager.NetworkManager;

/**
 * Implementation of @JsonRpcMessageManager, using a FIFO queue to manage
 * response handlers
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcMessageManagerImpl implements JsonRpcMessageManager {

	Map<String, JsonRpcMessageHandler> handlers = Collections
			.synchronizedMap(new CustomLinkedHashMap<String, JsonRpcMessageHandler>());

	private ObjectMapper mapper = new ObjectMapper();

	private static final Logger LOG = Logger.getLogger(JsonRpcMessageManagerImpl.class);

	public void activate(ComponentContext context) {// NOSONAR squid:S1172 -
													// JPU: Required by API
		LOG.debug("Activating JsonRpcMessageManager service");
	}

	@Override
	public String sendMessage(VirtualAddress address, NetworkManager networkManager, JsonRpcMessage message,JsonRpcMessageHandler handler) {//NOSONAR squid:MethodCyclomaticComplexity
		String reqId = null;
		byte[] payload = null;

		try {
			if (message instanceof JsonRpcRequest) {
				JsonRpcRequest req = (JsonRpcRequest) message;
				((JsonRpcRequest) message).getParams().setTimestamp(new Date().getTime());

				if (handler != null) {
					// generate unique request Id
					while (handlers.containsKey(req.getId()) || req.getId() == null) {
						req.setId(UUID.randomUUID().toString());
						reqId = req.getId();
					}

					handlers.put(req.getId(), handler);

					LOG.debug("Handler addded for request with id: " + req.getId());
				}

				payload = mapper.writeValueAsBytes(message);
				LOG.info("Sending JsonRpcRequest to " + address + ", id:" + ((JsonRpcRequest) message).getId());
			}
			if (message instanceof JsonRpcNotification) {
				((JsonRpcNotification) message).getParams().setTimestamp(new Date().getTime());
				payload = mapper.writeValueAsBytes(message);
				LOG.info("Sending JsonRpcNotification to " + address);
			}

			if (message instanceof JsonRpcResponse) {
				payload = mapper.writeValueAsBytes(message);
				LOG.info("Sending JsonRpcResponse to " + address + ", id:" + ((JsonRpcResponse) message).getId());
			}

			NMResponse response = networkManager.sendData(networkManager.getVirtualAddress(), address, payload, false);
			switch (response.getStatus()) {
			case NMResponse.STATUS_ERROR:
				LOG.error("Error sending message: " + response.getMessage());
				break;
			case NMResponse.STATUS_TIMEOUT:
				LOG.error("Timeout sending message: " + response.getMessage());
				break;
			default:
				LOG.info("NMResponse: " + response.getMessage());
				break;

			}

		} catch (JsonProcessingException e) {
			LOG.error("Unable to serialize message", e);
		} catch (RemoteException e) {
			LOG.error("Error sending data", e);
		}

		return reqId;
	}

	@Override
	public void receiveMessage(JsonRpcMessage message, EventAdmin eventAdmin) {
		LOG.debug("Received a message");

		Event event = null;
		if (message instanceof JsonRpcResponse) {
			LOG.debug("Message is a response");
			String key = ((JsonRpcResponse) message).getId();

			if (handlers.containsKey(key)) {
				JsonRpcMessageHandler handler = handlers.get(key);
				if (handler != null) {
					LOG.debug("Invoking registered handler for the Response");
					handler.handle(message);

					handlers.remove(((JsonRpcResponse) message).getId());
					return;
				} else {
					handlers.remove(((JsonRpcResponse) message).getId());
				}
			}
			event = OSGiUtils.toEvent((JsonRpcResponse) message);
		}

		if (message instanceof JsonRpcNotification) {
			LOG.info("Message is a notification");
			event = OSGiUtils.toEvent((JsonRpcNotification) message);
		}
		if (message instanceof JsonRpcRequest) {
			LOG.info("Message is a request");
			event = OSGiUtils.toEvent((JsonRpcRequest) message);
		}
		if (event != null) {
			LOG.debug("Posting event:");
			for (int i = 0; i < event.getPropertyNames().length; i++) {
				LOG.debug("\t " + event.getPropertyNames()[i] + ":" + event.getProperty(event.getPropertyNames()[i]));
			}
			eventAdmin.postEvent(event);
		} else {
			LOG.error("Received a message that cannot be handled by MessageBroker");
		}
	}

}
