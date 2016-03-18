package eu.linksmart.network.jsonrpc.api;

/**
 * Enum collecting all admitted values for field Method in JSON-RPC messages
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public enum Method {
	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
	private String value;

	private Method(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
