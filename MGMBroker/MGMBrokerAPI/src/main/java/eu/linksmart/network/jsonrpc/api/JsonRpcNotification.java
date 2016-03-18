package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing a JSON-RPC notification.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcNotification implements JsonRpcMessage {

	private Method method;
	private Params params;

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Params getParams() {
		if (params == null) {
			params = new Params();
		}
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

}
