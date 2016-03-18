/** 
 * Coded By Giorgio Dal To� on 16/mag/2013 
 *
 * Internet of Things Service Management Unit 
 * Pervasive Technologies Area
 * Istituto Superiore Mario Boella
 * Tel. (+39) 011 2276614
 * Email: daltoe@ismb.it
 * Email: giorgio.daltoe@gmail.com 
 * 
 * '||'  .|'''.|  '||    ||' '||''|.   
 *  ||   ||..  '   |||  |||   ||   ||  
 *  ||    ''|||.   |'|..'||   ||'''|.  
 *  ||  .     '||  | '|' ||   ||    || 
 * .||. |'....|'  .|. | .||. .||...|'
 *
 * Via Pier Carlo Boggio 61 
 * 10138 Torino, Italy
 * T 011/2276201; F 011/2276299
 * info@ismb.it
 */
package eu.greencom.api.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Must be istantiated with an ordered list of values.
 */
public class TimeSeriesImpl implements TimeSeries {

	private static final long serialVersionUID = 4182527246548886601L;
	private String timeSeriesID;
	private String deviceID;
	private List<SampledValue> values;// NOSONAR squid:S1948 - JPU. Required for
										// serialization, may not become
										// transient. List is not serializable
										// but a serializable implemenation is
										// expected to be provided via
										// constructor argument.
	private String unit;

	public TimeSeriesImpl() {

	}

	public TimeSeriesImpl(String deviceID, String timeSeriesID, String unit,
			List<SampledValue> val) {
		this.deviceID = deviceID;
		this.timeSeriesID = timeSeriesID;
		this.values = val;
		this.unit = unit;
	}

	@Override
	public long getStart() {
		if (!getValues().isEmpty()) {
			return getValues().get(0).getTimestamp();
		}
		return 0;
	}

	@Override
	public long getEnd() {
		if (!getValues().isEmpty()) {
			return getValues().get(getValues().size() - 1).getTimestamp();
		}
		return 0;
	}

	@Override
	public SampledValue getValue(long time) {
		for (SampledValue v : getValues()) {
			if (v.getTimestamp() == time){
				return v;
			}
		}
		return null;
	}

	// only useful if start >= timeseries start and end <= timeseries end
	@Override
	public List<SampledValue> getValues(long start, long end) {

		List<SampledValue> res = new LinkedList<SampledValue>();
		for (SampledValue sv : getValues()) {
			if (sv.getTimestamp() >= start && sv.getTimestamp() <= end) {
				res.add(sv);
			}
		}
		return res;
	}

	// to be added
	// utils for the complete time series
	@Override
	public List<SampledValue> getValues() {
		if (values == null){
			values = new LinkedList<SampledValue>();
		}
		return values;
	}

	public void setValues(List<SampledValue> values) {
		this.values = values;
	}

	@Override
	public boolean isEquidistant() {
		if (this.getValues().size() < 3){
			return true;
		}
		long l1 = values.get(0).getTimestamp();
		long l2 = values.get(1).getTimestamp();
		long distance = l2 - l1;
		long cur = l2;
		for (SampledValue s : this.getValues()) {
			if (s.getTimestamp() == l1 || s.getTimestamp() == l2){
				continue;
			}
			if (s.getTimestamp() - cur != distance){
				return false;
			}
			cur = s.getTimestamp();
		}
		return true;
	}

	@Override
	public int size() {
		return getValues().size();
	}

	@Override
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public String getTimeSeriesID() {
		return timeSeriesID;
	}

	public void setTimeSeriesID(String timeSeriesID) {
		this.timeSeriesID = timeSeriesID;
	}

}
