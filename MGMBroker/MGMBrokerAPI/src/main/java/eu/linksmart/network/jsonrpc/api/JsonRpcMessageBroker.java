package eu.linksmart.network.jsonrpc.api;

import eu.linksmart.network.jsonrpc.exception.InvalidClientException;

/**
 * This interface defines the JSON-RCP client apis
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public interface JsonRpcMessageBroker {

	/**
	 * Asynchronously sends the @JsonRpcMessage to an endpoint. This is either
	 * implicit ( hard-wired) or explicitly defined by the message it self. Any
	 * response to this message is broad-casted as OSGi Event within the
	 * sender’s runtime. It returns the auto-generated request identifier in
	 * case the message is a @JsonRpcRequest, null otherwise
	 * 
	 * @throws InvalidClientException
	 */
	public String post(JsonRpcMessage message);

	/**
	 * Asynchronously sends the @JsonRpcRequest to an endpoint. The provided @JsonRpcMessageHandler
	 * is invoked when the response is received. It returns the auto-generated @JsonRpcRequest
	 * identifier.
	 */
	public String post(JsonRpcRequest request, JsonRpcMessageHandler handler);

	public JsonRpcResponse post(JsonRpcRequest request);

}
