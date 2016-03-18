package eu.greencom.aggregation.service.api;

import eu.greencom.aggregation.api.Aggregation;

/**
 * Services processing a particular aggregation configuration. It has a managed
 * life-cycle depending on availability of this configuration. Once instantiated
 * with this configuration the execution may be stopped or resumed without
 * deleting the configuration.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface Aggregator extends ConfigurationProcessor<Aggregation> {

	/**
	 * Initializes or resumes aggregation processing.
	 */
	void start();

	/**
	 * Temporarily stops aggregation processing (without the need to remove it).
	 */
	void stop();

	/**
	 * Indicates whether the aggregation is active.
	 * 
	 * @return
	 */
	boolean isStarted();
	
	/**
	 * Retrieves the configuration underlying this instance.
	 * 
	 * @return
	 */	
	Aggregation getConfiguration();

}
