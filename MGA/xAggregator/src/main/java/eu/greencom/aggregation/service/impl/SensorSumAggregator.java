package eu.greencom.aggregation.service.impl;

import java.util.List;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.SampledValueImpl;

/*
 * Periodically sums-up multiple time-synchronized sensor values into an aggregated value.  
 */
public class SensorSumAggregator extends AbstractSensorAggregator {

	private String target;

	/**
	 * Computes the sum of configured source values closest to given time stamp;
	 * 
	 * @param sensor
	 * @param time
	 * @return
	 */
	protected double getSensorsSum(long time) {
		double sum = 0.0;		
		// Loop by collecting values closest to the aggregation time:
		for (String sensor : configuration.getAggregationSource()) {
			List<SampledValue> values = retrieveCloseValues(sensor, time, time);
			sum += getClosestValue(time, values);
		}
		return sum;
	}

	@Override
	protected void touchAggregationTarget() {

		target = generateTargetId(null);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!sensorManager.exists(target)) {
					log.debug("Target {} does not exist, creating", target);
					sensorManager.store(target, instantiateSensor(target));
				} else {
					log.debug("Target {} already exists", target);
				}
				ready = true;
			}
		}).start();
	}

	@Override
	protected void aggregate() {
		if (ready && started) {
			long time = getAggregationTime();
			double sum = getSensorsSum(time);
			sensorManager.addValue(target, new SampledValueImpl(target, target,
					time, sum));
		}
	}

	@Override
	protected String generateTargetId(String ignored) {
		// Same as configured
		return configuration.getAggregationTarget();
	}

}
