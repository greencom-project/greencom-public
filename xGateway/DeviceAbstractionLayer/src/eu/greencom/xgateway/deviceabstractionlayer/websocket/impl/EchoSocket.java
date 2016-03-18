package eu.greencom.xgateway.deviceabstractionlayer.websocket.impl;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.greencom.xgateway.deviceabstractionlayer.http.impl.JsonSampledValue;
import eu.greencom.xgateway.deviceabstractionlayer.http.impl.SensorId;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class EchoSocket {

	private final CountDownLatch closeLatch;
	private Session session;
	SensorId sensorId;
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private EventAdmin eventAdmin;
	private ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOG = Logger.getLogger(EchoSocket.class.getName());
	public Boolean isConnected = false;

	
	public EchoSocket(EventAdmin eventAdmin) {
		this.closeLatch = new CountDownLatch(1);
		this.eventAdmin = eventAdmin;
	}

	public boolean awaitClose(int duration, TimeUnit unit)
			throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		LOG.debug("Connection closed: " +  statusCode +  reason);
		isConnected = false;
		this.session = null;
		this.closeLatch.countDown();
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		LOG.debug("Got connected: "+ session);
		this.session = session;
		isConnected = true;
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		LOG.debug("Got msg: " + msg);

		// Get SensorId , Value out of the msg
		JSONObject response = new JSONObject(msg);
		JSONObject properties = new JSONObject();
		JSONObject value = new JSONObject();

		properties = response.getJSONObject("properties");
		String propertyName = properties
				.getString("dal.function.property.name");
		String functionUID = properties.getString("dal.function.UID");

		// getting sensorid out of the functionUID
		sensorId = new SensorId();
		String sensorID = sensorId.getSensorId(functionUID);
		LOG.debug("Sensor ID: " + sensorID);

		value = properties.getJSONObject("dal.function.property.value");

		double level = 0;
		Boolean valueBol;
		long timeStamp;
		Date date;
		Time time;

		if (propertyName.equals("current")) {
			level = value.getDouble("level");
			timeStamp = value.getLong("timestamp");
			date = new Date(timeStamp);
			time = new Time(timeStamp);

			LOG.debug("level is: " + level);

			// Making GreenCom msg:
			String gcMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{ \"gc:externalIdentifier\": \""
					+ sensorID + "\"  ,\"rdf:value\":\"" + level + "\"}}}";
			LOG.debug(gcMsg);
			JsonNode msgNode = null;
			try {
				msgNode = mapper.readTree(gcMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar currentTime = Calendar.getInstance(UTC);

			JsonSampledValue sample = new JsonSampledValue();
			sample.setSent(false);
			sample.setDeviceID(sensorID);
			// Not used:
			sample.setTimeSeriesID("");
			sample.setValue(level);
			sample.setTimestamp(currentTime.getTimeInMillis());

			// Send out event
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("sampledValue", sample);
			prop.put("sampledValueString", msgNode);
			if (prop.isEmpty()) {
				LOG.debug("sample is null");
			} else {
				eventAdmin.postEvent(new Event("eu/greencom/sensordata", prop));
			}
			
			
			
		} else if (propertyName.equals("total")) {
			level = value.getDouble("level");
			timeStamp = value.getLong("timestamp");
			date = new Date(timeStamp);
			time = new Time(timeStamp);

			LOG.debug("level is: " + level);
			sensorID = "T" + sensorID;
			// Making GreenCom msg:
			String gcMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{ \"gc:externalIdentifier\": \""
					+ sensorID + "\"  ,\"rdf:value\":\"" + level + "\"}}}";
			LOG.debug(gcMsg);
			JsonNode msgNode = null;
			try {
				msgNode = mapper.readTree(gcMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Calendar currentTime = Calendar.getInstance(UTC);

			JsonSampledValue sample = new JsonSampledValue();
			sample.setSent(false);
			sample.setDeviceID(sensorID);
			// Not used:
			sample.setTimeSeriesID("");
			sample.setValue(level);
			sample.setTimestamp(currentTime.getTimeInMillis());

			// Send out event
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("sampledValue", sample);
			prop.put("sampledValueString", msgNode);
			if (prop.isEmpty()) {
				LOG.debug("sample is null");
			} else {
				eventAdmin.postEvent(new Event("eu/greencom/sensordata", prop));
			}
		} else {
			valueBol = value.getBoolean("value");
			timeStamp = value.getLong("timestamp");
			date = new Date(timeStamp);
			time = new Time(timeStamp);
			LOG.debug("value is: " + valueBol);

		}

	}

	public void sendMessage(String msg) {
		try {
			session.getRemote().sendString(msg);
		} catch (IOException e) {
			LOG.debug("Error in Sending msg:" + e.toString());
		}
	}

	public void closeSession() {
		session.close(StatusCode.NORMAL, "Done");
		
	}

}
