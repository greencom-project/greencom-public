package eu.greencom.mgm.webapiconsumer.impl.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.greencom.mgm.webapiconsumer.api.service.WebApiConsumer;
import eu.greencom.mgm.webapiconsumer.impl.utils.AuthHttpClientFactory;
import eu.greencom.mgm.webapiconsumer.model.DriverAzureCredentials;
import eu.greencom.mgm.webapiconsumer.model.InstallationsResponse;
import eu.greencom.mgm.webapiconsumer.model.Sensor;

/**
 * DataWareHouse client implementing WebApiConsumer API.
 * 
 * @author SCS
 * @author pullmann
 *
 */
public class WebApiConsumerImpl implements WebApiConsumer {

	private Logger log = LoggerFactory.getLogger(WebApiConsumer.class.getName());

	protected AuthHttpClientFactory httpClientFactory = new AuthHttpClientFactory();

	protected SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");

	protected String host = null;

	protected String user = null;

	protected String password = null;

	protected WebApiConsumerImpl() {

	};

	public WebApiConsumerImpl(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void activate(ComponentContext context) {
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		configure(context.getProperties());
		log.debug("{} activated", this.getClass().getName());
	}

	public void modify(ComponentContext context) {
		configure(context.getProperties());
		log.debug("{} modified", this.getClass().getName());
	}

	public void deactivate(ComponentContext context) {
		log.debug("{} deactivated", this.getClass().getName());
	}

	private void configure(Dictionary<String, Object> properties) {
		if (properties != null) {
			host = (String) properties.get("host");
			user = (String) properties.get("user");
			password = (String) properties.get("password");
			log.debug("{} configured", this.getClass().getName());
		}
	}

	@Override
	public boolean publishReadingData(String data, String sensorID) {
		log.info("POST to the CloudApp for data: {}", data);
		boolean response = false;
		CloseableHttpClient httpclient = getHttpClient();
		try {
			HttpPost httppost = new HttpPost(getHost() + "/api/sensors/" + sensorID + "/values");
			httppost.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
			CloseableHttpResponse resp = httpclient.execute(httppost);

			log.info("The POST status is: {}", resp.getStatusLine());
			log.debug("Entire response is: {}", resp.toString());

			if (resp.getStatusLine().getStatusCode() == 200) {
				response = true;
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to publish data readings {}", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Failed to close httpclient: {}", e);
			}
		}
		return response;
	}

	@Override
	public InputStream getTimeSeries(String deviceID, String source, long start, long end) {

		CloseableHttpClient client = getHttpClient();

		URIBuilder uri = new URIBuilder().setScheme("http").setHost(getHost())
				.setPath("/api/sensors/" + deviceID + "/values")
				.addParameter("FromDate", dateFormatter.format(new Date(start)))
				.addParameter("ToDate", dateFormatter.format(new Date(end)));

		HttpGet request = null;
		try {
			request = new HttpGet(uri.build());
		} catch (URISyntaxException e) {
			log.error("One of the parameters was wrong!!! {}", e);
			return null;
		}

		InputStream responseStream = null;
		try {
			CloseableHttpResponse resp = client.execute(request);
			responseStream = resp.getEntity().getContent();
			resp.close();
		} catch (Exception e) {
			log.error("Failed to retrieve the time series {}", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to close client: {}", e);
			}
		}
		return responseStream;

	}

	public void modified(ComponentContext contect) {
		configure(contect.getProperties());
	}

	public static String getPID() {
		return "eu.greencom.mgm.webapiconsumer";
	}

	@Override
	public List<Sensor> getSensors(String installationID) {
		CloseableHttpClient client = getHttpClient();
		InstallationsResponse result = null;
		HttpGet request = new HttpGet(getHost() + "/api/installations/" + installationID + "/sensors");
		try {
			CloseableHttpResponse resp = client.execute(request);
			String response = EntityUtils.toString(resp.getEntity());
			if (resp.getStatusLine().getStatusCode() == 200 && response != null) {

				ObjectMapper mapper = new ObjectMapper();
				result = mapper.readValue(response, InstallationsResponse.class);

			} else {
				log.error("The web apis returned {} :{}", resp.getStatusLine().getStatusCode(), resp.getStatusLine()
						.getReasonPhrase());
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to retrieve sensors {}", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to close client: {}", e);
			}
		}

		return result.getSensors();
	}

	@Override
	public Map<Long, Double> getLastValue(String deviceID) {

		Map<Long, Double> result = null;
		// http://<host>/api/sensors/2E15BC001700123D/lastvalue
		HttpGet request = new HttpGet(getHost() + "/api/sensors/" + deviceID + "/lastvalue?format=json");
		CloseableHttpClient client = getHttpClient();
		String response = null;
		try {
			CloseableHttpResponse resp = client.execute(request);
			response = EntityUtils.toString(resp.getEntity());
			log.info("The GET status for getLastValue is: {}", resp.getStatusLine());
			log.debug("Entire response is: {}", resp.toString());
			if (response != null && resp.getStatusLine().getStatusCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(response);
				result = new HashMap<Long, Double>();
				String utc = json.get("LastUpdateDate").textValue();
				Date timestamp = dateFormatter.parse(utc);
				Double value = json.get("LastValue").doubleValue();
				result.put(timestamp.getTime(), value);
			} else {
				log.debug("The call for getLastValue returned: {}", resp.getStatusLine());
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to retrieve last value: {}", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to finalize the cleint: {}", e);
			}
		}
		return result;
	}

	@Override
	public String getSensorStatus(String sensorId) {
		log.info("Get sensor status requested for sensor: {}", sensorId);

		HttpGet request = new HttpGet(getHost() + "/api/sensors/" + sensorId + "/details?format=json");
		CloseableHttpClient client = getHttpClient();
		String response = null;
		String result = null;
		try {
			CloseableHttpResponse resp = client.execute(request);
			response = EntityUtils.toString(resp.getEntity());
			log.info("The GET status for getSensorStatus is: {}", resp.getStatusLine());
			log.debug("Entire response is: {}", resp.toString());
			if (response != null && resp.getStatusLine().getStatusCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(response);
				result = json.get("Sensor").get("Status").textValue();
			} else {
				log.debug("The call for getSensorStatus returned: {}", resp.getStatusLine());
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to retrieve last value: {}", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to finalize the cleint: {}", e);
			}
		}
		return result;
	}

	@Override
	public void setSensorStatus(String sensorId, String status) {
		log.info("Set sensor status to {} requested for sensor: {}", status, sensorId);
		CloseableHttpClient httpclient = getHttpClient();
		try {
			HttpPost httppost = new HttpPost(getHost() + "/api/sensors/" + sensorId + "/statuses");
			httppost.setEntity(new StringEntity("{\"statuspoints\":[{\"date\":\"" + (dateFormatter.format(new Date()))
					+ "\",\"status\":\"" + status + "\",\"trigger\":\"\"}]}", ContentType.APPLICATION_JSON));
			CloseableHttpResponse resp = httpclient.execute(httppost);
			log.info("The POST status is: {}", resp.getStatusLine());
			log.debug("Entire response is: {}", resp.toString());
			resp.close();
		} catch (Exception e) {
			log.error("Failed to process request: {}", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Failed to close client: {}", e);
			}
		}
	}

	@Override
	public DriverAzureCredentials getServiceBusCredentials(String driverID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Sensor> getRealSensors(String installationID) {
		// get all sensors
		List<Sensor> sensors = this.getSensors(installationID);
		// remove virtual sensors
		Iterator<Sensor> it = sensors.iterator();
		while (it.hasNext()) {
			Sensor s = it.next();
			if (s.getRemarks() != null && "virtual".equals(s.getRemarks())) {
				it.remove();
			}
		}
		return sensors;
	}

	protected CloseableHttpClient getHttpClient() {
		return httpClientFactory.getHttpClient(getHost(), getUser(), getPassword());
	}

}
