package eu.greencom.mgm.flexibilityaggregator.api.domain;

import java.math.BigDecimal;
import java.util.List;

public class FlexibilityAggregation {

	/**
	 * List of all the devices which offer flexibility
	 */
	private List<FlexibilityOption> flexibilityOptions;
	
	/**
	 * The total load of all the devices which can be shifted. Note: some devices
	 * might need to be turned on again before the shifting period end or cannot
	 * shifted directly with the start of the period.
	 */
	private BigDecimal totalLoad;

	public List<FlexibilityOption> getFlexibilityOptions() {
		return flexibilityOptions;
	}

	public void setFlexibilityOptions(List<FlexibilityOption> flexibilityOptions) {
		this.flexibilityOptions = flexibilityOptions;
	}

	public BigDecimal getTotalLoad() {
		return totalLoad;
	}

	public void setTotalLoad(BigDecimal totalLoad) {
		this.totalLoad = totalLoad;
	}
	
	public Integer getNumberDevices() {
		return flexibilityOptions.size();
	}
}
