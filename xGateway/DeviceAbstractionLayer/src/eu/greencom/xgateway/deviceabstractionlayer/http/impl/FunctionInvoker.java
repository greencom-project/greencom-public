package eu.greencom.xgateway.deviceabstractionlayer.http.impl;


import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Farmin Farzin
 *	This class provide the timertask for invoking opertaions on each device's function. After sending the request to http service and receiving the answer, depends on the operation, it 
 *	tries to save it on the cloud with sending the OSGi event.
 *	gets the list of devices from deviceValue
 */
public class FunctionInvoker extends TimerTask {

	private String dalAddress;
	private String[] devices;
	HttpRestGetPost hc;
	private EventAdmin eventAdmin;
	SensorId sensorId;
	private ObjectMapper mapper = new ObjectMapper();
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	DeviceValue devicevalue;

	public FunctionInvoker(HttpRestGetPost hc, EventAdmin ea, DeviceValue dv) {
		this.hc = hc;
		this.eventAdmin = ea;
		this.devicevalue = dv;
	}

	@Override
	public void run() {
		devices = devicevalue.getDevices();
		if (!(devices == null)) {
			for (int j = 0; j < devices.length; j++) {
				System.out.println(" of " + devices[j]);
				if (devices[j] == null) {
					break;
				}

				// in case there is space in the name we change it to %20
				String link = "http://" + dalAddress + "/api/devices/"
						+ devices[j] + "/functions";
				link = link.replaceAll(" ", "%20");
				// get Function
				String functionList = hc.getHTTP(link);

				if (!functionList.contains("unsuccessful")) {

					JSONObject wholeRespond = new JSONObject(functionList);
					int code = wholeRespond.getInt("code");
					if (code == 200) {
						
						JSONArray functionsJArray = new JSONArray();
						functionsJArray = wholeRespond.getJSONArray("result");

						// send post request for each function property
						for (int k = 0; k < functionsJArray.length(); k++) {
							JSONObject functionUIDobj = new JSONObject();
							functionUIDobj = functionsJArray.getJSONObject(k);
							// Name of functionUID
							String functionUIDname = functionUIDobj
									.getString("dal.function.UID");

							JSONArray propertiesJArray = new JSONArray();
							propertiesJArray = functionUIDobj
									.getJSONArray("dal.function.property.names");

							for (int l = 0; l < propertiesJArray.length(); l++) {
								// Name of Property
								String propertyName = propertiesJArray
										.getString(l);
								String p = propertyName.substring(0, 1)
										.toUpperCase()
										+ propertyName.substring(1);
								String propertyToSend = "get" + p;
								System.out.println(propertyToSend);
								if (propertyToSend.equals("getCurrent")) {

									// --------------------------------
									// getting sensorid out of the functionUID
									sensorId = new SensorId();
									String sensorID = sensorId
											.getSensorId(functionUIDname);
									System.out
											.println("Sensor ID: " + sensorID);
									sendToGC(functionUIDname, propertyToSend,
											sensorID);
								} else if (propertyToSend.equals("getTotal")) {
									// getting sensorid out of the functionUID
									sensorId = new SensorId();
									String sensorID = sensorId
											.getSensorId(functionUIDname);
									sensorID = "T" + sensorID;
									System.out
											.println("Sensor ID: " + sensorID);
									sendToGC(functionUIDname, propertyToSend,
											sensorID);
								}

							}
						}
					}
				}
			}
		}
		System.out.println("no Connection or no device");
	}

	// public void setDeviceArray (String[] devices){
	// this.devices = devices;
	// }

	public void setDalIPAddress(String ip) {
		dalAddress = ip;

	}

	public void sendToGC(String functionUIDname, String propertyToSend,
			String sensorID) {
		functionUIDname = functionUIDname.replaceAll(" ", "%20");
		String respondFromPost = hc.postHTTP("http://" + dalAddress
				+ "/api/functions/", functionUIDname, propertyToSend);
		if (!respondFromPost.contains("unsuccessful")) {
			// Get SensorId , Value out of the msg
			JSONObject response = new JSONObject(respondFromPost);
			JSONObject Jresult = new JSONObject();
			Jresult = response.getJSONObject("result");

			double level = 0;
			level = Jresult.getDouble("level");
			System.out.println("level is: " + level);

			// Making GreenCom msg:
			String gcMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{ \"gc:externalIdentifier\": \""
					+ sensorID + "\"  ,\"rdf:value\":\"" + level + "\"}}}";
			System.out.println(gcMsg);
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
				System.out.println("sample is null");
			} else {
				eventAdmin.postEvent(new Event("eu/greencom/sensordata", prop));
			}
		}
	}
}
