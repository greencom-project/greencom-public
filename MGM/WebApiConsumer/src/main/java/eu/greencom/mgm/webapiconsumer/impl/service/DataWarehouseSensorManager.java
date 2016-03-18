package eu.greencom.mgm.webapiconsumer.impl.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.SampledValueImpl;
import eu.greencom.mgm.webapiconsumer.api.service.SensorManager;
import eu.greencom.mgm.webapiconsumer.model.Data;
import eu.greencom.mgm.webapiconsumer.model.JsonDatapoints;
import eu.greencom.mgm.webapiconsumer.model.Sensor;

/**
 * Utility class extending SCS's WebApiConsumer functionality.
 * 
 * @author SCS
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class DataWarehouseSensorManager extends WebApiConsumerImpl implements SensorManager {

	// protected SimpleDateFormat dateFormatter = new
	// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	// Name pattern used to identify house's smart meter
	private static final Pattern NAME_PATTERN = Pattern.compile("Smart\\s+Meter", Pattern.CASE_INSENSITIVE);

	// Type pattern indicating the cumulative type of smart meter values
	private static final Pattern TYPE_PATTERN = Pattern.compile("Cumulative", Pattern.CASE_INSENSITIVE);

	private Logger log = LoggerFactory.getLogger(DataWarehouseSensorManager.class.getName());

	// Mapping of gateway ID to smart meter ID
	protected Map<String, String> smartMeterMap = new HashMap<String, String>();

	protected ObjectMapper mapper = new ObjectMapper();

	public void activate(ComponentContext context) {
		super.activate(context);		
		loadSmartMeterMap();
	}

	public void deactivate(ComponentContext context) {
		super.deactivate(context);		
	}

	@Override
	public List<String> getSensorsByType(String context, String type) {
		if (!TYPE_CUMULATIVE_SMART_METER.equals(type)) {
			throw new IllegalArgumentException("Currently only search for '" + TYPE_CUMULATIVE_SMART_METER
					+ "' type is supported!");
		}

		// Gateway ID normally contains a series of hyphens, normalize
		String smartMeter = smartMeterMap.get(context.replaceAll("-", ""));
		if (smartMeter != null) {
			List<String> s = new LinkedList<String>();
			s.add(smartMeter);
			return s;
		}
		return null;// NOSONAR squid:S1168 - JPU: Null by purpose
	}

	/*
	 * Loads a mapping of gateway ID (house) to Echelon Smart meter ID by
	 * comapring the sensor name and type.
	 */
	private void loadSmartMeterMap() {
		String url = getHost() + "/api/sensors";
		Matcher nameMatcher = NAME_PATTERN.matcher("");
		Matcher typeMatcher = TYPE_PATTERN.matcher("");
		try {
			JsonNode node = doGet(url);
			Iterator<JsonNode> i = node.get("Sensors").elements();
			while (i.hasNext()) {
				ObjectNode sensor = (ObjectNode) i.next();
				JsonNode sensorGw = sensor.get("GatewayId");
				// Sensor name or empty string ~ "Echelon Smart Meter"
				nameMatcher.reset(sensor.path("Name").asText());
				// ~ Cumulative
				typeMatcher.reset(sensor.path("Type").asText());

				if (sensorGw != null && nameMatcher.find() && typeMatcher.find()) {
					// GW ID does *not* contain hyphens
					smartMeterMap.put(sensorGw.asText(), sensor.get("Id").asText());
				}
			}
			log.info("Smart meter map loaded: {}", smartMeterMap);
		} catch (Exception e) {
			log.error("Failed to load smart meter map:", e);
		}
	}

	@Override
	public boolean store(String id, Sensor sensor) {
		String url = host + "/api/sensors";
		String json = "{}";
		try {
			json = "{\"Sensor\":" + mapper.writeValueAsString(sensor) + "}";
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize sensor {}: {}", id, e);
		}
		log.debug("Storing sensor {}", id);
		return doPost(url, json);

	}

	@Override
	public Sensor get(String uri) {
		return null;
	}

	@Override
	public boolean exists(String id) {
		String uri = host + "/api/sensors/" + id + "/details?format=json";
		JsonNode json = doGet(uri);
		/*
		 * SIC: Code is always 201, test for "Sensor" property, otherwise
		 * missing
		 */
		return json != null && json.has("Sensor");
	}

	@Override
	public boolean remove(String id) {
		if (id == null || "".equals(id)) {
			return false;
		}
		log.debug("Deleting sensor: " + id);
		return doDelete(host + "/api/sensors/" + id);
	}

	@Override
	public boolean addValue(String id, SampledValue... values) {
		String url = host + "/api/sensors/" + id + "/values";
		if (values != null && values.length > 0) {
			JsonDatapoints json = new JsonDatapoints();
			json.datapoints = new Data[values.length];
			for (int i = 0; i < values.length; i++) {
				SampledValue value = values[i];
				String date = dateFormatter.format(new Date(value.getTimestamp()));
				json.datapoints[i] = new Data(date, value.getValue() + "");
			}
			try {
				String msg = mapper.writeValueAsString(json);
				log.debug("Posting value(s) {} for sensor {}", msg, id);
				return doPost(url, msg);
			} catch (JsonProcessingException e) {
				log.error("Failed to serialize data points: {}", e);
			}
		}
		return false;
	}

	@Override
	public SampledValue getLatestValue(String sensorID) {
		Map<Long, Double> map = getLastValueMap(sensorID);
		if (map != null && !map.isEmpty()) {
			Long time = map.keySet().iterator().next();
			Double value = map.get(time);
			return new SampledValueImpl(sensorID, sensorID, time, value);
		}
		return null;
	}

	@Override
	public long getSamplingRate(String id) {

		long samplingRate = -1;

		SampledValue lastValue = getLatestValue(id);

		/*
		 * Retrieve probe values for this range (1h)
		 */
		long range = 1000 * 60 * 60 * 1;
		long end = lastValue.getTimestamp();
		long start = end - range;

		List<SampledValue> values = getValues(id, start, end);
		int i = 0;
		if (values != null && !values.isEmpty()) {
			for (i = 0; i < values.size(); i++) {
				if (i + 1 < values.size()) {
					long delta = values.get(i + 1).getTimestamp() - values.get(i).getTimestamp();
					samplingRate += delta;
				}
			}
			samplingRate = samplingRate / i;
		}
		// Interpret timestamp as milliseconds: 1000
		return Math.round(samplingRate / 1000.0) * 1000;
	}

	protected JsonNode doGet(String uri) {

		JsonNode json = null;
		HttpGet request = new HttpGet(uri + "?format=json");
		CloseableHttpClient client = getHttpClient();
		String response = null;
		try {
			CloseableHttpResponse resp = client.execute(request);
			response = EntityUtils.toString(resp.getEntity());
			log.info("The GET status for getLastValue is: " + resp.getStatusLine());
			log.debug("Entire response is: " + resp.toString());
			if (response != null && resp.getStatusLine().getStatusCode() == 200) {
				json = mapper.readTree(response);
			} else {
				log.debug("The call for getLastValue returned: " + resp.getStatusLine());
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to perform HTTP GET", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Execution failed: ", e);
			}
		}
		return json;
	}

	protected boolean doPost(String uri, String json) {
		log.info("POST to the CloudApp for data: " + json);
		boolean response = false;
		CloseableHttpClient httpclient = getHttpClient();
		log.info("POSTING to " + uri);
		try {
			// HOST+ "/api/sensors"
			HttpPost httppost = new HttpPost(uri);
			httppost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
			CloseableHttpResponse resp = httpclient.execute(httppost);

			if (resp.getStatusLine().getStatusCode() == 200) {
				response = true;
			}
			resp.close();
		} catch (Exception e) {
			log.error("Failed to perform HTTP POST to {}: {}", uri, e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Failed to close http client: ", e);
			}
		}

		return response;
	}

	protected boolean doDelete(String uri) {
		HttpDelete request = new HttpDelete(uri);
		CloseableHttpClient client = getHttpClient();
		try {
			CloseableHttpResponse resp = client.execute(request);
			if (resp.getStatusLine().getStatusCode() == 200) {
				log.debug("The call for doDelete succeeded with response code 200");
			} else {
				log.debug("The call for doDelete returned: " + resp.getStatusLine());
			}			
			resp.close();
		} catch (Exception e) {
			log.error("Failed to perform HTTP DELETE", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Execution failed: ", e);
			}
		}
		return true;
	}

	protected Map<Long, Double> getLastValueMap(String deviceID) {

		Map<Long, Double> result = null;
		// ... /api/sensors/2E15BC001700123D/lastvalue
		HttpGet request = new HttpGet(host + "/api/sensors/" + deviceID + "/lastvalue?format=json");
		CloseableHttpClient client = getHttpClient();
		String response = null;
		try {
			CloseableHttpResponse resp = client.execute(request);
			response = EntityUtils.toString(resp.getEntity());
			log.info("The GET status for getLastValue is: " + resp.getStatusLine());
			log.debug("Entire response is: " + resp.toString());

			if (response != null && resp.getStatusLine().getStatusCode() == 200) {

				mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(response);
				result = new HashMap<Long, Double>();
				String utc = json.get("LastUpdateDate").textValue();
				Date timestamp = dateFormatter.parse(utc);
				Double value = json.get("LastValue").doubleValue();
				result.put(timestamp.getTime(), value);

			} else {
				log.debug("The call for getLastValue returned: " + resp.getStatusLine());
			}			
			resp.close();
		} catch (Exception e) {
			log.error("Failed to retrieve last value", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to close HTTP client", e);
			}
		}
		return result;
	}

	@Override
	public List<SampledValue> getValues(String id, long start, long end) {
		
		List<SampledValue> sampledValues = null;

		CloseableHttpClient client = getHttpClient();

		URIBuilder uri = new URIBuilder()
				// .setScheme("http")
				// .setHost(HOST)
				.setPath(host + "/api/sensors/" + id + "/values")
				.addParameter("FromDate", dateFormatter.format(new Date(start)))
				.addParameter("ToDate", dateFormatter.format(new Date(end)));

		log.debug("Getting values from URI {}", uri);

		HttpGet request = null;
		try {
			request = new HttpGet(uri.build());
		} catch (URISyntaxException e) {
			log.error("One of the parameters was wrong!!!", e);
			return null;// NOSONAR squid:S1168 - JPU: Returns null by purpose
		}
		try {

			CloseableHttpResponse resp = client.execute(request);

			String response = EntityUtils.toString(resp.getEntity());
			log.info("The GET status for getLastValue is: " + resp.getStatusLine());
			log.debug("Entire response is: " + resp.toString());

			if (response != null && resp.getStatusLine().getStatusCode() == 200) {

				mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(response);
				/*
				 * There is always array keyed by "Values", empty on error.
				 */
				ArrayNode values = (ArrayNode) json.get("Values");
				if (values.size() > 0) {// NOSONAR squid:S1155 - JPU: Not a
										// collection, isEmpty() test does
										// not apply
					sampledValues = new LinkedList<SampledValue>();
					Iterator<JsonNode> i = values.elements();
					while (i.hasNext()) {
						JsonNode node = i.next();
						Double value = node.get("Value").asDouble();
						String dt = node.get("Date").asText();
						Long timeStamp = dateFormatter.parse(dt).getTime();
						SampledValue sampleValue = new SampledValueImpl(id, id, timeStamp, value);
						sampledValues.add(sampleValue);
					}
				}

			} else {
				log.debug("The call for getLastValue returned: " + resp.getStatusLine());
			}			
			resp.close();
		} catch (Exception e) {
			log.error("Failed to get values in range", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("Failed to close HTTP client: ", e);
			}
		}
		log.debug("Got values for sensor {}: {} in range from {} to {}", id, sampledValues, new Date(start), new Date(
				end));
		return sampledValues;

	}

}
