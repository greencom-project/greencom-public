package eu.greencom.aggregation.service.api;

import java.util.Map;

/**
 * Service component processing supplied configuration. It is instantiated by a
 * {@link ConfigurationProcessorFactory} matching the configuration. The
 * component has a minimal lifecycle inspired by SCR component (activate,
 * deactivate) and Apache Felix Dependency Manager Component class.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
@SuppressWarnings("rawtypes")
public interface ConfigurationProcessor<T extends Map> {

	T getConfiguration();

}
