package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing a JSON-RPC response.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class JsonRpcResponse implements JsonRpcMessage {

	private String id;
	private Result result;
	private Error error;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Result getResult() {
		if (result == null) {
			result = new Result();
		}
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

}
