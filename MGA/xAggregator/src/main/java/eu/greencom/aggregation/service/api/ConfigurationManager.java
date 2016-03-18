package eu.greencom.aggregation.service.api;

import java.util.Map;
import java.util.Set;

import org.osgi.service.cm.Configuration;

import eu.greencom.aggregation.api.Aggregation;

/**
 * Service for management of configuration data compatible with
 * {@link Configuration}. The API design is aligned with {@link Map}.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public interface ConfigurationManager<T extends Map> {

	/**
	 * Configuration type identifier used internally by
	 * {@link ConfigurationManager} for filtering purposes.
	 */
	public static final String PROPERTY_CONFIGURATION_TYPE = "configuration.type";

	T put(String id, T configuration);

	T get(String id);

	boolean exists(String id);

	T remove(String id);

	void setValue(String id, Object key, Object value);

	Set<String> keySet();

}
