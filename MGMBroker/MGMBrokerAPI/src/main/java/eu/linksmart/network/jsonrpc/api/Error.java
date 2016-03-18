package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing errors that can be returnet into a @JsonRpcResponse
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class Error {

	private final int code;
	private final String data;

	public Error(int code, String data) {
		super();
		this.code = code;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public String getData() {
		return data;
	}

}
