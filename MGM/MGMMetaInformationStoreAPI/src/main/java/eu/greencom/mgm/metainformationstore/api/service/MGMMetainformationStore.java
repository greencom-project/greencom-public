package eu.greencom.mgm.metainformationstore.api.service;

import java.util.List;
import java.util.Map;

import eu.greencom.mgm.metainformationstore.api.domain.Sensor;

public interface MGMMetainformationStore {

	List<Sensor> getSensorByAttributeValues(Map<String,String> attr);
	
	/**
	 * Adds a new {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} 
	 * @param sensor The {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} to be added
	 * @return true if the operation was successful, false otherwise
	 */
	boolean addSensor(Sensor sensor);
	
	/**
	 * Updates an existing {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} along with its properties (they will be overwritten)
	 * @param senseor the {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} to be modified
	 * @return the modified {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} or null if the sensor does not exist
	 */
	Sensor updateSensor(Sensor sensor);
	
	/**
	 * Removes a {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} and its properties
	 * @param sensor the {@link eu.greencom.mgm.metainformationstore.api.domain.Sensor} to be removed
	 * @return true if the operation was successful, false otherwise
	 */
	boolean removeSensor(Sensor sensor);
	
}
