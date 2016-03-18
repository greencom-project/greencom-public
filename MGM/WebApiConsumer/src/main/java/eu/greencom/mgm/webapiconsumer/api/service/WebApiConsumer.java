package eu.greencom.mgm.webapiconsumer.api.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import eu.greencom.mgm.webapiconsumer.model.DriverAzureCredentials;
import eu.greencom.mgm.webapiconsumer.model.Sensor;

public interface WebApiConsumer {
	
	public boolean publishReadingData(String data, String driverID);

	public DriverAzureCredentials getServiceBusCredentials(String driverID);

	public InputStream getTimeSeries(String deviceID, String source, long start, long end);

	/**
	 * Retreives all sensors (both virtual and real) of the specified
	 * installation
	 * 
	 * @param installationID
	 * @return a List of sensors
	 */
	public List<Sensor> getSensors(String installationID);

	/**
	 * Retreives only the real sensors of a specified installation
	 * 
	 * @param installationID
	 * @return
	 */
	public List<Sensor> getRealSensors(String installationID);

	/**
	 * Retrieves the most recent value persisted for given device.
	 * 
	 * @return Map of a numeric timestamp mapped to the value.
	 */
	public Map<Long, Double> getLastValue(String deviceID);

	public String getSensorStatus(String sensorId);

	public void setSensorStatus(String sensorId, String status);

}
