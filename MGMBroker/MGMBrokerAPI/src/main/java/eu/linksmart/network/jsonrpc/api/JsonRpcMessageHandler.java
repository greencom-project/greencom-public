package eu.linksmart.network.jsonrpc.api;

/**
 * Generic interface to be implemented by handlers to asynchronously get the @JsonRpcResponse
 * associated to a @JsonRpcRequest
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public interface JsonRpcMessageHandler {

	public void handle(JsonRpcMessage message);

	public JsonRpcResponse getResponse();

}
