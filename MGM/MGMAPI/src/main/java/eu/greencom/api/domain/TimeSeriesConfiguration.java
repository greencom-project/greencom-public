package eu.greencom.api.domain;

/**
 * Base configuration of a time series storage.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface TimeSeriesConfiguration {

	/**
	 * Identifier of the effective measurement unit.
	 * 
	 * @return
	 */
	String getUnit();
}