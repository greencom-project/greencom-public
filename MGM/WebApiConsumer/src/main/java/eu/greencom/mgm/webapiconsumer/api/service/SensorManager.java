package eu.greencom.mgm.webapiconsumer.api.service;

import java.util.List;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.mgm.webapiconsumer.model.Sensor;

/**
 * Copy, consolidation and extension of WebConsumer API code.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SensorManager {

	// CRUD sensor management
	boolean store(String id, Sensor sensor);

	Sensor get(String id);

	boolean exists(String id);

	boolean remove(String id);

	// Value and further management
	boolean addValue(String id, SampledValue... value);

	/**
	 * Retrieves a time-sorted list of measurements.
	 * 
	 * @param from
	 *            start time-stamp, inclusive
	 * @param to
	 *            final time-stamp, inclusive
	 * @return
	 */
	List<SampledValue> getValues(String id, long from, long to);

	SampledValue getLatestValue(String id);

	long getSamplingRate(String id);

	/**
	 * Retrieves identifiers of particular sensor type.
	 * 
	 * @param context
	 *            Identifier of the context (installation, group etc.) used to
	 *            resolve sensors of particular type, or null to consider any
	 *            context. The only currently supported type is
	 *            {@link #TYPE_CUMULATIVE_SMART_METER}.
	 * 
	 * @param type
	 *            Identifier of the sensor type.
	 * @return
	 */
	List<String> getSensorsByType(String context, String type);
	
	static final String TYPE_CUMULATIVE_SMART_METER = "CumulativeSmartMeter";

}
