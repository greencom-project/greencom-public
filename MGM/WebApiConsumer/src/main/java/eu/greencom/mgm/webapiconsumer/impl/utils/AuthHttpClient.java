package eu.greencom.mgm.webapiconsumer.impl.utils;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * An extension of a CloseableHttpClient transparently handling login to
 * DataWarehouse: every time a 401 (Unauthorized) status code is returned by a
 * call, the login procedure is invoked and the call is performed again
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
// @SuppressWarnings("deprecation")
public class AuthHttpClient extends CloseableHttpClient {
	private CookieStore store = new BasicCookieStore();
	private CloseableHttpClient httpClient;

	private String url;
	private String user;
	private String password;

	public AuthHttpClient(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public CloseableHttpResponse execute(HttpUriRequest arg0) throws IOException {
		CloseableHttpResponse response = getHttpClient().execute(arg0);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0);
		}
		return response;
	}

	@Override
	public CloseableHttpResponse execute(HttpUriRequest arg0, HttpContext arg1) throws IOException {
		CloseableHttpResponse response = getHttpClient().execute(arg0, arg1);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg1);
		}
		return response;
	}

	@Override
	public CloseableHttpResponse execute(HttpHost arg0, HttpRequest arg1) throws IOException {
		CloseableHttpResponse response = getHttpClient().execute(arg0, arg1);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg1);
		}
		return response;
	}

	@Override
	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1) throws IOException {
		HttpResponse response = getHttpClient().execute(arg0);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0);
		}
		return arg1.handleResponse(response);

	}

	@Override
	public CloseableHttpResponse execute(HttpHost arg0, HttpRequest arg1, HttpContext arg2) throws IOException {
		CloseableHttpResponse response = getHttpClient().execute(arg0, arg1);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg1);
		}
		return response;
	}

	@Override
	public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2) throws IOException {
		HttpResponse response = getHttpClient().execute(arg0, arg2);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg2);
		}
		return arg1.handleResponse(response);
	}

	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2) throws IOException {
		HttpResponse response = getHttpClient().execute(arg0, arg1);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg1);
		}
		return arg2.handleResponse(response);
	}

	@Override
	public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3)
			throws IOException {
		HttpResponse response = getHttpClient().execute(arg0, arg1, arg3);
		if (response.getStatusLine().getStatusCode() == 401) {
			authenticate();
			response = getHttpClient().execute(arg0, arg1, arg3);
		}
		return arg2.handleResponse(response);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ClientConnectionManager getConnectionManager() {
		return null;
	}

	protected void authenticate() {
		store.clear();
		AuthenticationClient authClient = new AuthenticationClient(url);
		List<Cookie> cookies = authClient.authenticateAPI(user, password);
		if (cookies != null) {
			for (Cookie c : cookies) {
				store.addCookie(c);
			}
		}
	}

	protected CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setDefaultCookieStore(store).build();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void close() throws IOException {
		// 
	}

	@Override
	protected CloseableHttpResponse doExecute(HttpHost arg0, HttpRequest arg1, HttpContext arg2) throws IOException {
		return this.execute(arg0, arg1, arg2);
	}

	@Override
	public HttpParams getParams() {
		return httpClient.getParams();// NOSONAR findbugs:NP_UNWRITTEN_FIELD -
										// JPU: Value gained from httpClient,
										// though deprecated
	}

}