package eu.greencom.xgateway.localwebapi.fakeevent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Farmin Farzin - ISMB
 *
 *         This class includes a map which contains sensors data and properties
 * 
 */
public class SensorsMap {

	private Map<String, SensorParameters> sensor_map;
	private static SensorsMap instance = null;

	public SensorsMap() {
		sensor_map = new HashMap<String, SensorParameters>();
	}

	public static SensorsMap getInstance() {
		if (instance == null) {
			instance = new SensorsMap();
		}
		return instance;
	}

	public Map<String, SensorParameters> getSensor_map() {
		return sensor_map;
	}

	public void setSensor_map(Map<String, SensorParameters> sensor_map) {
		this.sensor_map = sensor_map;
	}

	public void put_sensor_map(String key, SensorParameters params) {
		sensor_map.put(key, params);
	}

	public SensorParameters get_one_sensor_params(String sensor_name) {
		return sensor_map.get(sensor_name);
	}
}
