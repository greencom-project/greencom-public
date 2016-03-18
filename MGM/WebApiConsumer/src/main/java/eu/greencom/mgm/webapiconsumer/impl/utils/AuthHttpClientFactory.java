package eu.greencom.mgm.webapiconsumer.impl.utils;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Singleton HttpClient factory. The url is passed as a parameter since it can
 * be changed in component's configuration
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class AuthHttpClientFactory {

	private AuthHttpClient httpClient;

	public CloseableHttpClient getHttpClient(String url, String user, String password) {
		if (httpClient == null) {
			httpClient = new AuthHttpClient(url, user, password);
		} else {
			httpClient.setUrl(url);
		}
		return httpClient;
	}
}
