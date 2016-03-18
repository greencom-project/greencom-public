package eu.greencom.aggregation.api;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.Period;
import org.osgi.service.cm.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.aggregation.service.api.AggregationSourceResolver;
import eu.greencom.aggregation.service.api.Aggregator;

/**
 * Generic configuration (definition) of an aggregation rule compatible with
 * {@link Configuration#getProperties()}.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 *         TODO: Add type checks and conversions!
 * 
 */
public class Aggregation extends Properties {

	private static final long serialVersionUID = 4376017494556598497L;

	private static final Logger LOG = LoggerFactory.getLogger(Aggregation.class
			.getName());

	/**
	 * All date-time expressions are in UTC.
	 */
	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone
			.getTimeZone("UTC");

	/**
	 * Separator delimiting multiple values within a string value
	 */
	public static final String VALUE_SEPARATOR_REGEXP = "[,;|]";

	public static final String PROPERTY_ID = "aggregation.id";

	/**
	 * Update interval of the input source time series.
	 */
	public static final String PROPERTY_SAMPLING_PERIOD = "aggregation.samplingperiod";

	/**
	 * Update period of the aggregation.
	 * 
	 */
	public static final String PROPERTY_AGGREGATION_PERIOD = "aggregation.period";

	/**
	 * Delimited list of input source identifiers (e.g. sensor IDs or URIs)
	 * compatible with {@link #VALUE_SEPARATOR_REGEXP}.
	 */
	public static final String PROPERTY_AGGREGATION_SOURCES = "aggregation.sources";

	/**
	 * Delimited list of high-level keys to be resolved by available
	 * {@link AggregationSourceResolver}s into terminal source identifiers.
	 */
	public static final String PROPERTY_AGGREGATION_KEYS = "aggregation.keys";

	/**
	 * Unique identifier (name) of a {@link Aggregator} component intended to
	 * interpret/implement this configuration.
	 */
	public static final String PROPERTY_AGGREGATION_PROVIDER = "aggregation.provider";

	/**
	 * Complete identifier of the aggregation target (e.g. URI) used to store
	 * the aggregated value.
	 */
	public static final String PROPERTY_AGGREGATION_TARGET = "aggregation.target";

	/**
	 * Prefix prepended to multiple internally generated target identifiers.
	 */
	public static final String PROPERTY_AGGREGATION_TARGET_PREFIX = "aggregation.target_prefix";
	
	/**
	 * Type of the target value.
	 */
	public static final String PROPERTY_AGGREGATION_TARGET_TYPE = "aggregation.target_type";

	/**
	 * Readable name of a single aggregation target.
	 */
	public static final String PROPERTY_AGGREGATION_TARGET_NAME = "aggregation.target_name";

	/**
	 * Identifier of a context a target relates to (gateway, home etc.)
	 */
	public static final String PROPERTY_AGGREGATION_TARGET_CONTEXT = "aggregation.target_context";

	public Aggregation() {
		super();
	}

	/**
	 * Constructor compatible with {@link Configuration#getProperties()}.
	 * 
	 * @param configuration
	 */
	public Aggregation(Dictionary<String, Object> configuration) {
		this();
		Enumeration<String> e = configuration.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			put(key, configuration.get(key));
		}
	}

	/**
	 * Constructor compatible with SCR component's activate(Map) method.
	 * 
	 * @param configuration
	 */
	public Aggregation(Map<String, Object> configuration) {
		this();
		for (String key : configuration.keySet()) {
			put(key, configuration.get(key));
		}
	}

	public String getId() {
		return (String) get(PROPERTY_ID);
	}

	public Period getSamplingPeriod() {
		return asPeriod((String) get(PROPERTY_SAMPLING_PERIOD));
	}

	public long getSamplingInterval() {
		return getSamplingPeriod().toStandardDuration().getMillis();
	}

	public Period getAggregationPeriod() {
		return asPeriod((String) get(PROPERTY_AGGREGATION_PERIOD));
	}

	public long getAggregationInterval() {
		return getAggregationPeriod().toStandardDuration().getMillis();
	}

	public Set<String> getAggregationSource() {
		return asSet(PROPERTY_AGGREGATION_SOURCES);
	}

	public String getAggregationTarget() {
		return (String) get(PROPERTY_AGGREGATION_TARGET);
	}

	public String getAggregationType() {
		return (String) get(PROPERTY_AGGREGATION_PROVIDER);
	}

	public Set<String> asSet(String property) {
		return parseSetValue((String) get(property));
	}

	public double asDouble(String property, double fallback) {
		return parseDoubleValue((String) (get(property)), fallback);
	}

	public long getAggregationTime(long time) {
		return getAggregationTime(time, false);
	};

	/**
	 * Retrieves the closest previous time-stamp with relevant fields cleared
	 * according to the {@link #PROPERTY_AGGREGATION_PERIOD} resolution. Given
	 * the period "PT5M" the seconds and milliseconds fields are reset to 0. If
	 * the "normalize" flag is set and the field value is a proper divisor of
	 * the the enclosing time unit (any divisor of 60 for minutes field) the
	 * resultant time is normalized to the closest multiple of the aggregation
	 * period (instead of 17:03,17:08,17:13 etc. to 17:00,17:05,17:10 etc.)
	 * 
	 * 
	 * @param time
	 * @return
	 */
	public long getAggregationTime(long time, boolean normalize) {
		Calendar c = Calendar.getInstance(DEFAULT_TIME_ZONE);
		c.setTimeInMillis(time);
		Period p = getAggregationPeriod();
		boolean hasContext = p.getYears() != 0;

		if (hasContext && p.getMonths() == 0)
			c.set(Calendar.MONTH, 0);

		hasContext |= p.getMonths() != 0;

		if (hasContext && p.getDays() == 0)
			c.set(Calendar.DAY_OF_MONTH, 1);

		hasContext |= p.getDays() != 0;

		if (hasContext && p.getHours() == 0)
			c.set(Calendar.HOUR_OF_DAY, 0);
		else if (normalize && p.getHours() > 0 && 24 % p.getHours() == 0) {
			int hours = c.get(Calendar.HOUR_OF_DAY) / p.getHours()
					* p.getHours();
			c.set(Calendar.HOUR_OF_DAY, hours);
		}

		hasContext |= p.getHours() != 0;

		if (hasContext && getAggregationPeriod().getMinutes() == 0)
			c.set(Calendar.MINUTE, 0);
		// e.g. round minutes to next lower hour fragment
		else if (normalize && p.getMinutes() > 0 && (60 % p.getMinutes()) == 0) {
			int minutes = c.get(Calendar.MINUTE) / p.getMinutes()
					* p.getMinutes();
			c.set(Calendar.MINUTE, minutes);
		}

		hasContext |= p.getMinutes() != 0;

		if (hasContext && p.getSeconds() == 0)
			c.set(Calendar.SECOND, 0);
		else if (normalize && p.getSeconds() > 0
				&& (1000 % p.getSeconds()) == 0) {
			int seconds = c.get(Calendar.SECOND) / p.getSeconds()
					* p.getSeconds();
			c.set(Calendar.SECOND, seconds);
		}

		// Milliseconds are below the aggregation resolution
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	Set<String> parseSetValue(String value) {
		Set<String> result = null;
		if (value != null) {
			String[] tokens = value.split(VALUE_SEPARATOR_REGEXP);
			result = new HashSet<String>(Arrays.asList(tokens));
		}
		return result;
	}

	double parseDoubleValue(String value, double fallback) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return fallback;
		}
	}

	Period asPeriod(String period) {
		Period result = null;
		try {
			result = Period.parse(period);
		} catch (Exception e) {
			LOG.error("Failed to parse period {}: {}", period, e.getMessage());
		}
		return result;
	}

	long getAggregationTime() {
		Calendar c = Calendar.getInstance(DEFAULT_TIME_ZONE);
		return getAggregationTime(c.getTimeInMillis());
	}

}
