package eu.linksmart.network.jsonrpc.api;

/**
 * Class representing the collection of parameters that can be inserted into a
 * JSON-RPC request or response
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class Params {

	private String data;
	private String uri;
	private String host;
	private String source;
	private long timestamp;
	private int priority;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
