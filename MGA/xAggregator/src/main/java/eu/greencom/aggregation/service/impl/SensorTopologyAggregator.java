package eu.greencom.aggregation.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import eu.greencom.aggregation.api.Aggregation;
import eu.greencom.aggregation.service.api.AggregationSourceResolver;
import eu.greencom.api.domain.SampledValueImpl;

/*
 * Periodically sums-up consumption of configured radials.  
 */
public class SensorTopologyAggregator extends SensorDeltaAggregator {

	private List<String> radials = new LinkedList<String>();

	private Map<AggregationSourceResolver, String> resolvers = new HashMap<AggregationSourceResolver, String>();

	private class Worker implements Runnable {
		@Override
		public void run() {
			
			Calendar calendar = Calendar
					.getInstance(Aggregation.DEFAULT_TIME_ZONE);
			long normalizedNow = getAggregationTime();
			long aggregationInterval = configuration.getAggregationInterval();
			// Retrieve values for the past aggregation period
			long time = normalizedNow - aggregationInterval;

			for (String radial : radials) {
				log.debug("Retrieving sum for radial {}", radial);
				for (AggregationSourceResolver resolver : resolvers.keySet()) {
					List<String> sensors = resolver.getSources(radial);
					log.debug(
							"Radial {} resolved to sensor list {} via resolver {}",
							radial, sensors, resolver);
					if (sensors != null && sensors.size() > 0) {
						double radialSum = getRadialSum(sensors, time);
						// Should equally consider 0 delta
						// if (radialSum != 0) {
						String prefix = resolvers.get(resolver);
						String target = prefix + "_" + generateTargetId(radial);
						log.debug("Storing radial sum at {}: {}", target,
								radialSum);
						// Store aggregated value
						sensorManager.addValue(target, new SampledValueImpl(
								target, target, time, radialSum));
					} else {
						log.warn("No sensors to process for radial {}", radial);
					}
				}
			}

		}
	}

	protected void addAggregationSourceResolver(
			AggregationSourceResolver resolver, Map properties) {
		String prefix = (String) properties
				.get(Aggregation.PROPERTY_AGGREGATION_TARGET_PREFIX);
		resolvers.put(resolver, prefix);
		log.debug("Added AggregationSourceResolver {}", resolver);
	}

	protected void removeAggregationSourceResolver(
			AggregationSourceResolver resolver, Map properties) {
		resolvers.values().remove(resolver);
		log.debug("Removed AggregationSourceResolver {}", resolver);
	}

	@Override
	protected void configure(Map<String, Object> properties) {
		super.configure(properties);
		// Configure monitored radials
		radials = parseConfigValue(getProperty(Aggregation.PROPERTY_AGGREGATION_KEYS));
	}

	// Only a partial id: {radial}_{period}
	@Override
	protected String generateTargetId(String radial) {
		String period = getConfiguration().getAggregationPeriod().toString();
		return radial + "_" + period;
	}

	@Override
	protected void touchAggregationTarget() {
		TimerTask touchTargets = new TimerTask() {
			@Override
			public void run() {
				for (String radial : radials) {
					for (String targetPrefix : resolvers.values()) {
						String target = targetPrefix + "_"
								+ generateTargetId(radial);
						if (!sensorManager.exists(target)) {
							log.debug("Target {} does not exist, creating",
									target);
							// Create a target entry in DW
							sensorManager.store(target,
									instantiateSensor(target));
						} else {
							log.debug("Target {} already exists", target);
						}
					}
				}
				// Enable processing
				ready = true;
			}
		};
		// Wait till resolvers are set up
		new Timer().schedule(touchTargets, 2000);
	}

	@Override
	protected void aggregate() {
		if (ready && started) {
			new Thread(new Worker()).start();
		}
	}

	private double getRadialSum(List<String> sensors, long time) {
		double sum = 0.0;
		for (String sensor : sensors) {
			double delta = getSensorDelta(sensor, time);
			if (delta > 0) {
				sum += delta;
			} else {
				log.debug("No value delta for sensor {}: {}", sensor, delta);
			}
		}
		return sum;
	}
}
