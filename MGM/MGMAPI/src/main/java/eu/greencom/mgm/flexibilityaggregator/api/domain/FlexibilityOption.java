package eu.greencom.mgm.flexibilityaggregator.api.domain;

import org.joda.time.*;

public class FlexibilityOption {

	/**
	 * Id of the device
	 */
	private long guid;
	
	/**
	 * The time period from which load should be shifted (e.g. because peaks 
	 * should be reduced).
	 */
	private Interval shiftFrom;
	
	/**
	 * The priority of the device. If it is more preferable to shift the load 
	 * from a heat pump than from a washing machine, the heat pump would have a
	 * higher priority.
	 */
	private byte priority;
	
	/**
	 * The total load which can be shifted during the given period of time.
	 */
	private double totalLoad;

	/**
	 * The time needed for a storage device to be completely loaded from the 
	 * time of the request. 
	 */
	private Duration fullyLoadedIn;
	
	/**
	 * The maximum time for a storage device to be completely loaded (from any 
	 * point in time).
	 */
	private Duration maxTimeTillFullyLoaded;
	
	/**
	 * The earliest date/ time the device can be turned on again after the start
	 * of the shifting period. Relevant if the device must stay off for a minimum 
	 * amount of time once it is turned off. 
	 */
	private ReadableDateTime earliestStartDate;
	
	/**
	 * The latest date/ time the device must be turned on again after the start
	 * shifting period (e.g. to fulfill contractual obligations).
	 */
	private ReadableDateTime latestStartDate;
	
	
	public FlexibilityOption(long guid) {
		this.guid = guid;
	}
	
	public FlexibilityOption(long guid, Interval shiftFrom) {
		this.guid = guid;
		this. shiftFrom = shiftFrom;
	}
	
	public long getGuid() {
		return guid;
	}
	
	public void setGuid(long guid) {
		this.guid = guid;
	}
	
	public Interval getShiftFrom() {
		return shiftFrom;
	}
	
	public void setShiftFrom(Interval shiftFrom) {
		this.shiftFrom = shiftFrom;
	}
	
	public byte getPriority() {
		return priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public double getTotalLoad() {
		return totalLoad;
	}

	public void setTotalLoad(double load) {
		this.totalLoad = load;
	}

	public Duration getFullyLoadedIn() {
		return fullyLoadedIn;
	}

	public void setFullyLoadedIn(Duration fullyLoadedIn) {
		this.fullyLoadedIn = fullyLoadedIn;
	}

	public Duration getMaxTimeTillFullyLoaded() {
		return maxTimeTillFullyLoaded;
	}

	public void setMaxTimeTillFullyLoaded(Duration maxTimeTillFullyLoaded) {
		this.maxTimeTillFullyLoaded = maxTimeTillFullyLoaded;
	}

	public ReadableDateTime getEarliestStartDate() {
		return earliestStartDate;
	}

	public void setEarliestStartDate(ReadableDateTime earliestStartDate) {
		this.earliestStartDate = earliestStartDate;
	}

	public ReadableDateTime getLatestStartDate() {
		return latestStartDate;
	}

	public void setLatestStartDate(ReadableDateTime latestStartDate) {
		this.latestStartDate = latestStartDate;
	}
	
}
