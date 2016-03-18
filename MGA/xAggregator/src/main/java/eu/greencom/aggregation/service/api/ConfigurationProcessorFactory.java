package eu.greencom.aggregation.service.api;

import java.util.Map;

/**
 * Inspired by OSGi ComponentFactory and ServiceFactory this factory service
 * creates service instances capable of handling an appropriate configuration.
 * The factory is not simply referenced via an ID but based on its capabilities
 * to handle a matching configuration.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * @param <T>
 * @param <D>
 */
@SuppressWarnings("rawtypes")
public interface ConfigurationProcessorFactory<T extends ConfigurationProcessor<D>, D extends Map> {

	boolean matches(D properties);

	T newInstance(D properties);

}
