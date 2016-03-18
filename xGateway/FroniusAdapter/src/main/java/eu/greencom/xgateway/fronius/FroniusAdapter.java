package eu.greencom.xgateway.fronius;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import eu.greencom.mgm.webapiconsumer.api.service.SensorManager;
import eu.greencom.mgm.webapiconsumer.model.Sensor;
import eu.greencom.xgateway.integrationlayer.JsonRpcSampledValue;

/**
 * Every "sample period" retrieves Fronius Solar API values and pushes them via
 * EventAdmin to GreenCom. The values are extracted from GET <server> endpoint
 * according to configuration: <service>/<datapath>#targetSensorId
 */
public class FroniusAdapter {

	protected static final Logger LOG = LoggerFactory.getLogger(FroniusAdapter.class.getName());

	// Used to forward the "sampled values"
	private EventAdmin eventAdmin;

	private String gateway;

	// Server endpoint
	private String endpoint;

	// Maps ServiceID to SensorID
	private Map<String, Set<String>> sensorMap = new HashMap<String, Set<String>>();

	// Maps SensorID to retrieval path
	private Map<String, String> pathMap = new HashMap<String, String>();

	private long samplingRate = 0;

	private String separator = "#";

	private Timer scheduler;

	// Based on client request
	protected boolean started = true;

	protected SensorManager sensorManager;

	protected void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	protected void removeSensorManager(SensorManager sensorManager) {
		this.sensorManager = null;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	protected void activate(Map<String, Object> properties) {
		configure(properties);
		start();
		LOG.debug("{} activated", this.getClass().getName());
	}

	protected void modified(Map<String, Object> properties) {
		LOG.debug("{} modified", this.getClass().getName());
		stop();
		configure(properties);
		start();
	}

	protected void deactivate(Map<String, String> properties) {
		stop();
		LOG.debug("{} deactivated", this.getClass().getName());
	}

	protected void configure(Map<String, Object> properties) {

		LOG.debug("Updating configuration{}: {}", this.getClass().getName(), properties);

		// House ID
		gateway = (String) properties.get("gateway");

		endpoint = (String) properties.get("endpoint");

		if (properties.containsKey("samplingRate")) {
			samplingRate = (Long) properties.get("samplingRate") * 1000;
		}

		if (properties.containsKey("separator")) {
			separator = (String) properties.get("separator");
		}

		String[] params = (String[]) properties.get("params");
		if (params != null && params.length > 0) {
			// Clear old settings
			pathMap.clear();
			sensorMap.clear();
			// Iterate single entries
			// 5/0#$.Body.Data.Power_P_Grid.value#House20_P_Grid
			for (String entry : params) {
				// Split mandatory fields
				String[] mapping = entry.split(separator);
				String service = mapping[0];
				String path = mapping[1];
				String sensorId = mapping[2];
				String type = null;
				if (mapping.length > 3) {
					type = mapping[3];
				}
				String desc = null;
				if (mapping.length > 4) {
					desc = mapping[4];
				}
				Set<String> sensors = sensorMap.get(service);
				if (sensors == null) {
					sensors = new HashSet<String>();
					sensorMap.put(service, sensors);
				}
				sensors.add(sensorId);
				pathMap.put(sensorId, path);

				if (!sensorManager.exists(sensorId)) {
					// Check params!
					sensorManager.store(sensorId, instantiateSensor(sensorId, gateway, sensorId, type, desc));
				}
			}

		}

	}

	protected double retrieve(String url, String path) {
		String json = getRemoteJson(url);
		Object object = Configuration.defaultConfiguration().jsonProvider().parse(json);
		Double value = JsonPath.read(object, path);
		return value;
	}

	public String getRemoteJson(String url) {

		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

			StringBuffer b = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				b.append(line);
			}
			result = b.toString();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void start() {
		if (samplingRate != 0) {
			// Hard reset in order to reflect configuration changes
			scheduler = new Timer();
			scheduler.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					// For every service retrieve and parse underlying Json data
					for (String service : sensorMap.keySet()) {
						String url = endpoint + "/" + service;
						DocumentContext ctx = null;
						try {
							String json = getRemoteJson(url);
							Object object = Configuration.defaultConfiguration().jsonProvider().parse(json);
							ctx = JsonPath.using(Configuration.defaultConfiguration()).parse(json);
						} catch (Exception e) {
							LOG.error("Failed retrieving data from the Fronius API at {}", url);
						}
						if (ctx != null) {
							// For every service's sensor retrieve/store the
							// value
							for (String sensor : sensorMap.get(service)) {
								String path = pathMap.get(sensor);
								/*
								 * Check for null values, API delivers null
								 * instead of numeric 0.0 ! "Power_P_Generate" :
								 * { "value" : null, "unit" :"W" }
								 */
								try {
									// Request explicit casting
									double value = ctx.read(path, Double.class);
									long sampleTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
											.getTimeInMillis();
									publishData(sensor, value, sampleTime);
								} catch (Exception e) {
									LOG.error("Failed extracting Fronius data at path  {}", path);
								}
							}
						}
					}
				}
			}, 0, samplingRate);
			LOG.debug("{} scheduling started.", getClass().getName());
			started = true;
		} else {
			LOG.debug("{} remains inactive due to zero sampling rate!", getClass().getName());
		}
	}

	public void stop() {
		if (isStarted()) {
			try {
				scheduler.cancel();
			} catch (Exception e) {
				LOG.error("Failed stopping scheduler");
			}
			LOG.debug("Scheduler stopped");
			started = false;
		}
	}

	public boolean isStarted() {
		return started;
	}

	protected void publishData(String sensor, double value, long timestamp) {
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		JsonRpcSampledValue sample = new JsonRpcSampledValue();
		sample.setSent(false);
		sample.setDeviceID(sensor);
		// Not used:
		sample.setTimeSeriesID("");
		sample.setValue(value);
		sample.setTimestamp(timestamp);
		// Send out event
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("sampledValue", sample);
		// prop.put("sampledValueString", msg);
		prop.put("value", value);
		prop.put("sensor", sensor);
		eventAdmin.postEvent(new Event("eu/greencom/sensordata", prop));
	}

	protected static Sensor instantiateSensor(String id, String gateway, String name, String type, String desc) {
		Sensor s = new Sensor();
		if (id == null || gateway == null)
			throw new IllegalArgumentException("The sensor ID may not be empty!");

		s.setId(id);
		s.setGatewayId(gateway);
		if (name != null) {
			s.setName(name);
		}
		if (type != null) {
			s.setType(type);
		} else {
			// Use id prefix to guess type
			if (id.startsWith("2E") || id.contains("Power")) {
				s.setType("Instant");
			} else if (id.startsWith("2F") || id.contains("Energy")) {
				s.setType("Cumulative");
			}
		}
		if (desc != null) {
			s.setRemarks(desc);
		} else {
			s.setRemarks("Fronius Solar API Sensor");
		}
		return s;
	}
	
	protected List<String> resolveAvailableEndpoints(){
		
		String command ="sudo arp-scan --interface=eth0 --localnet -t 1000";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    CommandLine commandline = CommandLine.parse(command);
	    DefaultExecutor exec = new DefaultExecutor();
	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
	    exec.setStreamHandler(streamHandler);
	    try {
			exec.execute(commandline);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    //outputStream.toString())
		
		return null;
	}
	

}
