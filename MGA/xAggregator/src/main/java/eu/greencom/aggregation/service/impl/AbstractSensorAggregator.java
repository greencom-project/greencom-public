package eu.greencom.aggregation.service.impl;

import java.util.List;
import java.util.Map;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.mgm.webapiconsumer.api.service.SensorManager;

/*
 * Aggregator adding SensorManager interaction (backed by GreenCom DataWarehouse).
 */
public abstract class AbstractSensorAggregator extends AbstractAggregator {

	protected SensorManager sensorManager;

	protected void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	protected void removeSensorManager(SensorManager sensorManager) {
		this.sensorManager = null;
	}

	protected void activate(Map<String, Object> properties) {
		configure(properties);
		// Ensure the aggregation output ("target") is set up
		touchAggregationTarget();
		// Start aggregation
		start();
		log.debug("{} activated", this.getClass().getName());
	}

	protected void deactivate(Map<String, String> properties) {
		stop();
		log.debug("{} deactivated", this.getClass().getName());
	}

	/**
	 * Generates identifier for given target based on supplied configuration.
	 * This target identifier is used in all subsequent calls (creation,
	 * update). Should be overridden by subclasses, when needed.
	 * 
	 * @param id
	 * @return
	 */
	protected abstract String generateTargetId(String id);

	/**
	 * Ensure aggregation target(s)/sensors exist or are created.
	 */
	protected abstract void touchAggregationTarget();

	/**
	 * Retrieves a list of values included in or close to given interval.
	 * 
	 * @param sensor
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	protected List<SampledValue> retrieveCloseValues(String sensor,
			long startTime, long endTime) {
		long samplingInterval = configuration.getSamplingInterval();
		return sensorManager.getValues(sensor,
				startTime - 2 * samplingInterval, endTime + 2
						* samplingInterval);
	}

}
