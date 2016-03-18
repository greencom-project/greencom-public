package eu.greencom.aggregation.service.impl;

import java.util.Date;
import java.util.List;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.SampledValueImpl;

/**
 * Periodically computes a delta of cumulative sensor values (e.g. consumption
 * or production within last calendar hour) storing them separately at a
 * normalized time stamp (10:00:00, 11:00:00 etc.).
 */
public class SensorDeltaAggregator extends AbstractSensorAggregator {

	// TODO: Optionally replace by querying _nassist sensor value
	/**
	 * Retrieves sensor delta at particular time considering the configured
	 * aggregation rate.
	 * 
	 * @param sensor
	 * @param time
	 * @return
	 */
	protected double getSensorDelta(String sensor, long time) {
		long aggregationInterval = configuration.getAggregationInterval();
		long startTime = time - aggregationInterval;
		long endTime = time;
		List<SampledValue> values = retrieveCloseValues(sensor, startTime,
				endTime);
		double startValue = getClosestValue(startTime, values);
		double endValue = getClosestValue(endTime, values);
		double delta = endValue - startValue;
		log.debug("Retrieved delta {} for sensor {} at {} for period {}",
				delta, sensor, new Date(time),
				configuration.getAggregationPeriod());
		return delta;
	}

	@Override
	protected void touchAggregationTarget() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Create a target for every source
				for (String id : configuration.getAggregationSource()) {
					String targetId = generateTargetId(id);
					if (!sensorManager.exists(targetId)) {
						log.debug("Target {} does not exist, creating",
								targetId);
						sensorManager.store(targetId,
								instantiateSensor(targetId));
					} else {
						log.debug("Target {} already exists", targetId);
					}
				}
				ready = true;
			}
		}).start();
	}

	@Override
	protected String generateTargetId(String id) {
		StringBuffer sb = new StringBuffer(id);
		return sb.append(NAME_SEPARATOR).append("delta").append(NAME_SEPARATOR)
				.append(configuration.getAggregationPeriod()).toString();
	}

	@Override
	protected void aggregate() {
		if (ready && started) {
			long time = getAggregationTime();
			// Loop by collecting values closest to the aggregation time
			for (String sensor : configuration.getAggregationSource()) {
				double delta = getSensorDelta(sensor, time);
				// Consider only increasing values
				String targetId = generateTargetId(sensor);
				sensorManager.addValue(targetId, new SampledValueImpl(targetId,
						targetId, time, delta));
				log.debug("Uploaded delta {} for aggregation {}", delta,
						configuration.getId());
			}
		}
	}

}
