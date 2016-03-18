package eu.greencom.mgm.webapiconsumer.impl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import eu.greencom.mgm.webapiconsumer.model.DriverCredentialsResponse;

public class AuthenticationClient {
	private static final Logger LOG = Logger
			.getLogger(AuthenticationClient.class);
	
	private static final String DEFAULT_ENCODING = "UTF-8";

	private String baseurl;
	private Gson gson;

	public AuthenticationClient(String url) {
		baseurl = url;
		gson = new Gson();
	}

	public List<Cookie> authenticateAPI(String name, String password) {
		LOG.info("Going to authenticate http client");
		BasicCookieStore store = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieStore(store).build();

		HttpPost request = new HttpPost(baseurl + "/api/auth/");

		Map<String, String> content = new HashMap<String, String>();

		content.put("username", name);
		content.put("password", password);

		request.setEntity(new StringEntity(gson.toJson(content),
				ContentType.APPLICATION_JSON));

		List<Cookie> responseCookieStore = null;
		try {
			// CloseableHttpResponse resp=
			httpclient.execute(request);
			responseCookieStore = store.getCookies();
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
		LOG.debug("Authentication cookies: ");
		for (Cookie c : responseCookieStore) {
			LOG.debug("   " + c.getName() + " : " + c.getValue());
		}
		return responseCookieStore;
	}

	public DriverCredentialsResponse authenticateServiceBus(
			BasicCookieStore store, String driverID) {
		DefaultHttpClient client = new DefaultHttpClient();

		HttpEntityEnclosingRequestBase request = new HttpPost(baseurl
				+ "/api/auth/driver/");

		client.setCookieStore(store);

		Map<String, String> content = new HashMap<String, String>();
		content.put("DriverId", driverID);

		request.setEntity(new StringEntity(gson.toJson(content),
				ContentType.APPLICATION_JSON));

		HttpResponse response = null;
		DriverCredentialsResponse credentials = null;
		BufferedReader br = null;
		try {
			response = client.execute(request);

			br = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent(),DEFAULT_ENCODING));

			String aux = null;
			String entity = "";
			while ((aux = br.readLine()) != null) {
				entity += aux;
			}

			credentials = gson
					.fromJson(entity, DriverCredentialsResponse.class);

		} catch (ClientProtocolException e) {
			LOG.error("An error occurred: ", e);
		} catch (IOException e) {
			LOG.error("An error occurred: ", e);
		} catch (Exception e) {
			LOG.error("An error occurred: ", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOG.error("An error occurred: ", e);
				}
			}
			request.releaseConnection();
			client.getConnectionManager().shutdown();
		}

		return credentials;
	}
}