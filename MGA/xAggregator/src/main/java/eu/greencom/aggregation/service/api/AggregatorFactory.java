package eu.greencom.aggregation.service.api;

import eu.greencom.aggregation.api.Aggregation;

/**
 * Marker interface used to look-up {@link Aggregator} factories.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface AggregatorFactory extends
		ConfigurationProcessorFactory<Aggregator, Aggregation> {
}
