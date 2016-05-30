package eu.greencom.xgateway.localwebapi.fakeevent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;

/**
 * @author Farmin Farzin - ISMB
 *
 *         This class will implement a component to publish fake sensor data to
 *         show on the GUI Also it reads the CSV files including real data for 1
 *         day of 5 sensors and import it to mongo.
 */

public class FakeEvent {
	protected static final Logger LOG = LoggerFactory.getLogger(FakeEvent.class.getName());
	private EventAdmin eventAdmin;
	private boolean enable = true;
	private Timer scheduler;
	protected boolean started = true;
	SensorsMap sensor_map = new SensorsMap();
	int soc_sample = 0;
	int mains_sample = 0;
	int pv_sample = 0;
	int grid_sample = 0;
	int akku_sample = 0;
	Random r = new Random();

	BufferedReader bufferReader = null;
	ArrayList<String> grid_array;
	ArrayList<String> pv_array;
	ArrayList<String> soc_array;
	ArrayList<String> load_array;
	ArrayList<String> akku_array;
	int counter = 0;

	static boolean checkAlreadyStarted = false;
	private LocalDataStorage store;

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	protected void activate(Map<String, Object> props) {
		LOG.info("fake events activate");
		if (checkAlreadyStarted) {
			LOG.debug("already started");
		} else {
			start();
		}
	}

	protected void deactivate(Map<String, Object> props) {
		LOG.info("fake events deactivate");
		stop();
	}

	protected void modified(Map<String, Object> props) {
		LOG.info("fake events modified");
		// stop();
		// configure(props);
		// start();
	}

	private void configure(Map<String, Object> properties) {
		enable = (boolean) properties.get("enabled");
	}

	private int generateRandom() {
		int Low = 1;
		int High = 1000;
		int Result = r.nextInt(High - Low) + Low;
		return Result;
	}

	private double generateRandomIndoorTemp() {
		double rangeMin = 22.00;
		double rangeMax = 24.00;
		double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		return randomValue;
	}

	// Reading CSV and fill the array
	public static ArrayList<String> crunchifyCSVtoArrayList(String crunchifyCSV) {
		ArrayList<String> crunchifyResult = new ArrayList<String>();

		if (crunchifyCSV != null) {
			String[] splitData = crunchifyCSV.split("\\s*,\\s*");
			for (int i = 0; i < splitData.length; i++) {
				if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
					crunchifyResult.add(splitData[i].trim());
				}
			}
		}

		return crunchifyResult;
	}

	public void start() {

		// drop the collection
		store.removeDB("SampledValue");
		// read the CSV files and keep the values in the related array
		try {
			String crunchifyLine;

			bufferReader = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("/p_grid.csv")));
			while ((crunchifyLine = bufferReader.readLine()) != null) {
				grid_array = crunchifyCSVtoArrayList(crunchifyLine);
			}
			bufferReader = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("/p_load.csv")));
			while ((crunchifyLine = bufferReader.readLine()) != null) {
				load_array = crunchifyCSVtoArrayList(crunchifyLine);
			}
			bufferReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/p_pv.csv")));
			while ((crunchifyLine = bufferReader.readLine()) != null) {
				pv_array = crunchifyCSVtoArrayList(crunchifyLine);
			}
			bufferReader = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("/p_akku.csv")));
			while ((crunchifyLine = bufferReader.readLine()) != null) {
				akku_array = crunchifyCSVtoArrayList(crunchifyLine);
			}
			bufferReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/p_soc.csv")));
			while ((crunchifyLine = bufferReader.readLine()) != null) {
				soc_array = crunchifyCSVtoArrayList(crunchifyLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException crunchifyException) {
				crunchifyException.printStackTrace();
			}
		}

		// Filling up DB from yesterday with the array data

		long one_day = 86400000;
		long sampleTime_yesterday = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - one_day;
		// saving data from yesterday to now (every minute), by sending events
		// with different timestamps
		for (int k = 0; k < grid_array.size(); k++) {
			sampleTime_yesterday = sampleTime_yesterday + 60000;
			publishData("sensor_meter_mains", Double.parseDouble(load_array.get(k)), sampleTime_yesterday);
			publishData("sensor_meter_pv", Double.parseDouble(pv_array.get(k)), sampleTime_yesterday);
			publishData("sensor_battery_soc", Double.parseDouble(soc_array.get(k)), sampleTime_yesterday);
			publishData("sensor_meter_akku", Double.parseDouble(akku_array.get(k)), sampleTime_yesterday);
			publishData("sensor_meter_grid", Double.parseDouble(grid_array.get(k)), sampleTime_yesterday);

		}
		LOG.info("Data for last day imported successfully with size of " + grid_array.size());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Getting instance of sensor map and fill the map
		// This map keeps the last values for each sensor
		sensor_map = SensorsMap.getInstance();

		if (enable)

		{
			// Scheduler for sending the fake events
			
			scheduler = new Timer();
			scheduler.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					long sampleTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

					double consumption = Double.parseDouble(load_array.get(counter));
					double production = Double.parseDouble(pv_array.get(counter));
					double grid = Double.parseDouble(grid_array.get(counter));
					double soc = Double.parseDouble(soc_array.get(counter));
					double akku = Double.parseDouble(akku_array.get(counter));
					counter++;
					// sending value with some sensors name every 61 seconds
					// 2 different events to be sent because one is for saving to Mongo, and one is to use it on websocket client
					publishData("sensor_meter_mains", consumption, sampleTime);
					sensor_map.put_sensor_map("sensor_meter_mains",
							new SensorParameters("sensor_meter_mains", consumption, sampleTime));

					publishData("sensor_meter_pv", production, sampleTime);
					sensor_map.put_sensor_map("sensor_meter_pv",
							new SensorParameters("sensor_meter_pv", production, sampleTime));

					publishData("sensor_battery_soc", soc, sampleTime);
					sensor_map.put_sensor_map("sensor_battery_soc",
							new SensorParameters("sensor_battery_soc", soc, sampleTime));

					publishData("sensor_meter_akku", akku, sampleTime);
					sensor_map.put_sensor_map("sensor_meter_akku",
							new SensorParameters("sensor_meter_akku", akku, sampleTime));

					publishData("sensor_meter_grid", grid, sampleTime);
					sensor_map.put_sensor_map("sensor_meter_grid",
							new SensorParameters("sensor_meter_grid", grid, sampleTime));

					publishData("sensor_temp_indoor", generateRandomIndoorTemp(), sampleTime);
					sensor_map.put_sensor_map("sensor_temp_indoor",
							new SensorParameters("sensor_temp_indoor", generateRandomIndoorTemp(), sampleTime));

					publishData("sensor_temp_outdoor", generateRandom(), sampleTime);
					sensor_map.put_sensor_map("sensor_temp_outdoor",
							new SensorParameters("sensor_temp_outdoor", generateRandom(), sampleTime));

					publishData("sensor_washing_machine", generateRandom(), sampleTime);
					sensor_map.put_sensor_map("sensor_washing_machine",
							new SensorParameters("sensor_washing_machine", generateRandom(), sampleTime));

					publishData("sensor_tv", generateRandom(), sampleTime);
					sensor_map.put_sensor_map("sensor_tv",
							new SensorParameters("sensor_tv", generateRandom(), sampleTime));

					publishData("sensor_light", generateRandom(), sampleTime);
					sensor_map.put_sensor_map("sensor_light",
							new SensorParameters("sensor_light", generateRandom(), sampleTime));

				}
			}, 5000, 61000);
			LOG.debug("{} scheduling started.", getClass().getName());
			started = true;
		} else {
			LOG.debug("{} remains inactive due to disability!", getClass().getName());
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
		eventAdmin.postEvent(new Event("eu/greencom/sensordata/" + sensor, prop));

	}

	public void bindLocalDataStorage(LocalDataStorage store) {
		LOG.info("Binding LocalDataStorage");
		this.store = store;
	}

	public void unbindLocalDataStorage(LocalDataStorage store) {
		LOG.info("Unbinding LocalDataStorage" + store);
		this.store = null;
	}

}
