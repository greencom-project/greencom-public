package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing a JSON-RPC request.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcRequest extends JsonRpcNotification {

	/**
	 * Mandatory in requests
	 */
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
