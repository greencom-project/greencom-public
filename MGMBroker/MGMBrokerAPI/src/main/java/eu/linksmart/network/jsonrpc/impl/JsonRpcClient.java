package eu.linksmart.network.jsonrpc.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessage;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageBroker;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageHandler;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageManager;
import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.networkmanager.NetworkManager;

/**
 * The Implementation of @JsonRpcMessageBroker interface handling LinkSmart
 * services resolution
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcClient implements JsonRpcMessageBroker {

	private String destinationServerId;

	private String sourceServerId;

	private NetworkManager networkManager;

	private JsonRpcMessageManager messageManager;

	private static final Logger LOG = Logger.getLogger(JsonRpcClient.class);

	public JsonRpcClient(String sourceServerId, String destinationServerId, NetworkManager networkManager,
			JsonRpcMessageManager messageManager) {
		this.sourceServerId = sourceServerId;
		this.destinationServerId = destinationServerId;
		this.networkManager = networkManager;
		this.messageManager = messageManager;
	}

	/**
	 * Internal generic method to send @JsonRpcMessage, specifying the correct
	 * endpoint according to specific type of message ( @JsonRpcNotification , @JsonRpcRequest
	 * or @JsonRpcResponse ) and invoking @JsonRpcMessageManager to send the
	 * message
	 * 
	 * @param message
	 */
	private String sendMessage(JsonRpcMessage message) {
		VirtualAddress addr = null;

		String serviceDescription = null;

		if (message instanceof JsonRpcRequest) {
			serviceDescription = "RequestProxy_" + destinationServerId;
			((JsonRpcRequest) message).getParams().setSource(sourceServerId);
		} else if (message instanceof JsonRpcNotification) {
			serviceDescription = "NotificationProxy_" + destinationServerId;
			((JsonRpcNotification) message).getParams().setSource(sourceServerId);
		} else if (message instanceof JsonRpcResponse) {
			serviceDescription = "ResponseProxy_" + destinationServerId;
		}

		addr = resolveService(serviceDescription);

		if (addr != null) {
			LOG.debug("Address resolved for description " + serviceDescription + ": " + addr.toString());
			LOG.debug("Sending message to " + addr.toString());
			return messageManager.sendMessage(addr, networkManager, message, null);
		}

		LOG.error("Unable to send message, could not determine VirtualAddress for the specified message, not a recognized type");
		return null;

	}

	private VirtualAddress resolveService(String serviceDescription) {
		VirtualAddress addr;
		LOG.debug("Resolving service with description: " + serviceDescription);
		LinkSmartServiceResolver resolver = new LinkSmartServiceResolver(networkManager);
		addr = resolver.resolveServiceByDescription(serviceDescription);
		return addr;
	}

	// Methods of JsonRpcMessageBroker interfaces

	@Override
	public String post(JsonRpcMessage message) {
		return this.sendMessage(message);
	}

	@Override
	public String post(JsonRpcRequest request, JsonRpcMessageHandler handler) {
		request.getParams().setSource(sourceServerId);
		VirtualAddress addr = null;
		// Todo
		addr = resolveService("RequestProxy_" + destinationServerId);
		if (addr != null) {
			return messageManager.sendMessage(addr, networkManager, request, handler);
		}

		return null;

	}

	@Override
	public JsonRpcResponse post(JsonRpcRequest request) {
		request.getParams().setSource(sourceServerId);
		final CountDownLatch doneSignal = new CountDownLatch(1);
		JsonRpcMessageHandler handler = new JsonRpcMessageHandler() {

			JsonRpcResponse response = null;

			@Override
			public void handle(JsonRpcMessage message) {
				this.response = (JsonRpcResponse) message;
				doneSignal.countDown();
			}

			@Override
			public JsonRpcResponse getResponse() {
				return this.response;
			}
		};
		VirtualAddress addr = null;
		addr = resolveService("RequestProxy_" + destinationServerId);
		if (addr != null) {
			messageManager.sendMessage(addr, networkManager, request, handler);
		} else {
			return null;
		}

		try {
			if (doneSignal.await(30, TimeUnit.SECONDS)) {
				LOG.error("Timeout sending request to " + addr.toString());
			}

		} catch (InterruptedException e) {
			LOG.error("Interrupted while waiting for response", e);
		}

		return handler.getResponse();
	}

}
