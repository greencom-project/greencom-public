package eu.greencom.xgateway.hpintegrationconsumer;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.greencom.xgateway.hpintegration.HPIntegrationService;
import eu.greencom.xgateway.hpintegrationconsumer.constant.Const;
import eu.greencom.xgateway.hpintegrationconsumer.constant.JsonSampledValue;

/**
 * @author Farmin Farzin
 *
 *         This class implements Runnable for reading values from HP using
 *         HPIntegration service and uploading them to GC cloud.
 * 
 *
 */
public class ReadRunnerThread implements Runnable {

	private static final Logger LOG = Logger.getLogger(ReadRunnerThread.class);
	JsonSampledValue sample = new JsonSampledValue();
	private EventAdmin eventAdmin;
	HPIntegrationService hpservice;
	private ObjectMapper mapper = new ObjectMapper();
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private Boolean active = false;
	private int parameterToRead;
	private String preSensorID;
	private Const constant = new Const();
	private static String paramName;
	private static int factor;
	private long updateTimePeriod;

	public ReadRunnerThread(EventAdmin ea, HPIntegrationService hp, long updatetimeperiod, String preSensorID) {
		this.eventAdmin = ea;
		this.hpservice = hp;
		this.preSensorID = preSensorID;
		this.updateTimePeriod = updatetimeperiod;
		constant.makeMap();
	}

	@Override
	public void run() {

		// just for test:
		this.active = true;
		// String[] tags = new String[] { "offsetS1", "outdoortemp","indoortemp"
		// ,"compressorstatus", "hotwatermode", "minsupply",
		// "calculatedsupply", "supplytemp", "degreeminutes", "heatcurve",
		// "additivepower", "operationalmode",
		// "compressorstartcount", "compressorstate", "boilertemp",
		// "allowadditiveheat", "allowheat" };

		String[] tags = new String[] { "offsetS1", "outdoortemp", "indoortemp", "compressorstatus", "hotwatermode",
				"minsupply", "calculatedsupply", "supplytemp", "degreeminutes", "heatcurve", "additivepower",
				"operationalmode" };

		while (this.active) {

			for (int j = 0; j < constant.commands.size(); j++) {
				// constant.commands.keySet().iterator();
				paramName = tags[j];
				parameterToRead = constant.commands.get(tags[j])[0];
				factor = constant.commands.get(tags[j])[1];
				// reading from modbus
				int result = 121212;

				result = hpservice.sendRead(parameterToRead);
				// For outdoor Temperature we do something weird...
				// because of negative value of temperature
				if (parameterToRead == Const.BT1Outdoortemp && result > 6520) {

					// if it is greater that X it means it is negative and we
					// need to perform smthing
					result = result ^ 0xFFFF;
					result = -result;
					System.out.println("we are getting negative value for offset " + result);
				}
				// Offset S1 in case of having negative value
				if (parameterToRead == Const.OffsetS1 && result > 200) {

					// if it is greater that X it means it is negative and we
					// need to perform smthing
					System.out.println("we are getting negative value for offset " + result);
					LOG.debug("we are getting negative value for offset " + result);
					result = result ^ 0x00FF;
					result++;
					result = -result;
					// result = -result;
					System.out.println("we are getting negative value for offset " + result);

				}

				LOG.debug(parameterToRead + " has been read with result : " + result);

				if (result != 121212) {
					double resultD = (double) result;
					resultD = resultD / factor;
					System.out.println("The parameter " + paramName + " with registery: " + parameterToRead
							+ " and factor: " + factor + " , has result: " + resultD);
					LOG.debug("The parameter " + paramName + " with registery: " + parameterToRead + " and factor: "
							+ factor + " , has result: " + resultD);

					String sensorID = preSensorID + paramName;
					// upload to GC
					uploadToGC(sensorID, resultD);
				}
			}

			try {
				Thread.sleep(updateTimePeriod);
			} catch (InterruptedException e) {
				LOG.warn("InterruptedException", e);
			}
		}

	}

	public void stop() {
		this.active = false;

	}

	public Boolean getActive() {
		return this.active;
	}

	private void uploadToGC(String sensorID, Double level) {
		String gcMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{ \"gc:externalIdentifier\": \""
				+ sensorID + "\"  ,\"rdf:value\":\"" + level + "\"}}}";
		LOG.debug(gcMsg);
		JsonNode msgNode = null;

		try {
			msgNode = mapper.readTree(gcMsg);
		} catch (JsonProcessingException e) {
			LOG.error("JsonProcessingException" + e);
		} catch (IOException e) {
			LOG.error("IOException" + e);
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
