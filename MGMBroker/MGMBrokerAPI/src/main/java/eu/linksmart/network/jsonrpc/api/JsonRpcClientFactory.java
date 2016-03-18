package eu.linksmart.network.jsonrpc.api;

public interface JsonRpcClientFactory {

	public JsonRpcMessageBroker createJsonRpcClient(String serverId);

}
