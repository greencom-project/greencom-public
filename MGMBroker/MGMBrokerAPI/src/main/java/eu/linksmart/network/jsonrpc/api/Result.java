package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing the result that can be specified within a JSON-RPC
 * response
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class Result {
	private int code;
	private String data;
	private String uri;
	private String host;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
