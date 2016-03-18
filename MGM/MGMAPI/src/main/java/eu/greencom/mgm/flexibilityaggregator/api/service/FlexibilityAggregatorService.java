package eu.greencom.mgm.flexibilityaggregator.api.service;

import org.joda.time.Interval;

import eu.greencom.mgm.flexibilityaggregator.api.domain.FlexibilityAggregation;

public interface FlexibilityAggregatorService {

	/**
	 * Returns information about all options to shift consumption of different 
	 * devices.
	 * @param shiftFrom period of time from which to shift the load
	 * @return FlexibilityAggregation containing information about the options
	 */
	FlexibilityAggregation getFlexibilityOptions(Interval shiftFrom) ;
}
