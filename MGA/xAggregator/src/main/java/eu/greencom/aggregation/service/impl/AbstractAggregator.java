package eu.greencom.aggregation.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.aggregation.api.Aggregation;
import static eu.greencom.aggregation.api.Aggregation.*;
import eu.greencom.aggregation.service.api.Aggregator;
import eu.greencom.api.domain.SampledValue;
import eu.greencom.mgm.webapiconsumer.model.Sensor;

/*
 * Generic functionality.
 */
public abstract class AbstractAggregator implements Aggregator {

	protected static final String NAME_SEPARATOR = "_";

	protected Aggregation configuration;

	private Timer scheduler;

	// Based on client request
	protected boolean started = true;

	// Based on runtime conditions
	protected boolean ready = false;

	protected Logger log = LoggerFactory.getLogger(Aggregator.class.getName());
	
	protected abstract void aggregate();

	@Override
	public void start() {
		// Hard reset in order to reflect configuration changes
		scheduler = new Timer();
		scheduler.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Real aggregation code
				aggregate();
			}
		}, 0, configuration.getAggregationInterval());

		log.debug("Aggregation {} started at {} rate", configuration.getId(),
				configuration.getAggregationPeriod());

		started = true;
	}

	

	@Override
	public void stop() {
		if (isStarted()) {
			try {
				scheduler.cancel();
			} catch (Exception e) {
				log.error("Failed stopping scheduler for aggregation {}",
						configuration.getId());
			}
			log.debug("Aggregation {} stopped", configuration.getId());
			started = false;
		}
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	protected void configure(Map<String, Object> properties) {
		configuration = new Aggregation(properties);
	}

	@Override
	public Aggregation getConfiguration() {
		return configuration;
	}

	protected String getProperty(String name) {
		return configuration.getProperty(name);
	}

	protected long getAggregationTime() {
		Calendar calendar = Calendar.getInstance(Aggregation.DEFAULT_TIME_ZONE);
		return configuration.getAggregationTime(calendar.getTimeInMillis(),
				true)
		/*
		 * Workaround: delay by 1 minute (dispatcher upload period)
		 */
		- 1000 * 60;
	}

	// Parses mapping property:
	// House01:85f4d922-d876-47cf-bdc2-547924640bb5|House02 ...
	protected static Map<String, String> parseConfigValues(String propertyValue) {
		Map<String, String> map = new HashMap<String, String>();
		if (propertyValue != null) {
			for (String value : propertyValue
					.split(Aggregation.VALUE_SEPARATOR_REGEXP)) {
				String[] entry = value.split(":");
				if (entry.length == 2) {
					map.put(entry[0], entry[1]);
				}
			}
		}
		return map;
	}

	// radial1, radial2
	protected static List<String> parseConfigValue(String propertyValue) {
		List<String> list = new LinkedList<String>();
		if (propertyValue != null) {
			for (String value : propertyValue
					.split(Aggregation.VALUE_SEPARATOR_REGEXP)) {
				list.add(value);
			}
		}
		return list;
	}

	/**
	 * Retrieves a {@link SampledValue} closest to the specified time-stamp.
	 * 
	 * @param time
	 * @param values
	 * @return
	 */
	protected static double getClosestValue(long time, List<SampledValue> values) {
		double result = 0.0;
		if (values != null) {
			int i = 0;
			while (i < values.size()) {
				result = values.get(i).getValue();
				if (i + 1 < values.size()) {
					long delta = Math.abs(time - values.get(i).getTimestamp());
					long nextDelta = Math.abs(time
							- values.get(i + 1).getTimestamp());
					if (nextDelta > delta) {
						// terminate keeping current value
						i = values.size();
					}
				}
				i++;
			}
		}
		return result;
	}

	protected Sensor instantiateSensor() {
		return instantiateSensor(configuration
				.getProperty(PROPERTY_AGGREGATION_TARGET));
	}

	// Use supplied/generated ID
	protected Sensor instantiateSensor(String id) {
		return instantiateSensor(id,
				configuration.getProperty(PROPERTY_AGGREGATION_TARGET_CONTEXT),
				configuration.getProperty(PROPERTY_AGGREGATION_TARGET_NAME),
				configuration.getProperty(PROPERTY_AGGREGATION_TARGET_TYPE));
	}

	protected static Sensor instantiateSensor(String id, String context,
			String name, String type) {
		Sensor s = new Sensor();
		if (id == null || id.length() == 0)
			throw new IllegalArgumentException(
					"The target ID may not be empty!");
		s.setId(id);
		if (context != null) {
			s.setGatewayId(context);
		}
		if (name != null) {
			s.setName(name);
		}
		if (type != null) {
			s.setType(type);
		}
		return s;
	}

}
