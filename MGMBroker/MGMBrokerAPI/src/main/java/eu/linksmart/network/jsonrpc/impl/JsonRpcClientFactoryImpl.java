package eu.linksmart.network.jsonrpc.impl;

import eu.linksmart.network.jsonrpc.api.JsonRpcClientFactory;
import eu.linksmart.network.jsonrpc.api.JsonRpcMessageBroker;

/**
 * Implementation of @JsonRpcClientFactory, Singleton pattern.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcClientFactoryImpl implements JsonRpcClientFactory {

	private JsonRpcServer jsonRpcServer;

	@Override
	public JsonRpcMessageBroker createJsonRpcClient(String destinationServerId) {
		return new JsonRpcClient(jsonRpcServer.getServerId(), destinationServerId, jsonRpcServer.getNetworkManager(),
				jsonRpcServer.getJsonRpcMessageManager());
	}

	public void bindJsonRpcServer(JsonRpcServer jsonRpcServer) {
		this.jsonRpcServer = jsonRpcServer;
	}

	public void unbindJsonRpcServer(JsonRpcServer jsonRpcServer) {//NOSONAR squid:S1172 - JPU: Required by SCR API
		this.jsonRpcServer = null;
	}

}
