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

import java.util.Date;

public class SampledValueImpl implements SampledValue,
		Comparable<SampledValueImpl> {

	private static final long serialVersionUID = 8056003914216708609L;
	private String _id; // NOSONAR squid:S0017 - JPU: Member is named by
						// intention, "_id" introduced in sampled value for
						// unknown (probably persistence) reason.
	private double value;
	private long timestamp;
	private boolean sent = false;
	private String timeSeriesID;
	private String deviceID;

	public SampledValueImpl() {
	}

	public SampledValueImpl(String deviceID, String timeSeriesID,
			long timestamp, double value) {
		this.deviceID = deviceID;
		this.setTimeSeriesID(timeSeriesID);
		this.timestamp = timestamp;
		this.value = value;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result
				+ ((deviceID == null) ? 0 : deviceID.hashCode());
		result = prime * result + (sent ? 1231 : 1237);
		result = prime * result
				+ ((timeSeriesID == null) ? 0 : timeSeriesID.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {// NOSONAR squid:MethodCyclomaticComplexity - JPU:
										// generated code comparing all of the
										// members (can't reduce)
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SampledValueImpl other = (SampledValueImpl) obj;
		if (_id == null) {
			if (other._id != null) {
				return false;
			}
		} else if (!_id.equals(other._id)) {
			return false;
		}
		if (deviceID == null) {
			if (other.deviceID != null) {
				return false;
			}
		} else if (!deviceID.equals(other.deviceID)) {
			return false;
		}
		if (sent != other.sent) {
			return false;
		}
		if (timeSeriesID == null) {
			if (other.timeSeriesID != null) {
				return false;
			}
		} else if (!timeSeriesID.equals(other.timeSeriesID)) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(SampledValueImpl o) {
		if (this.timestamp < o.getTimestamp()) {
			return -1;
		} else if (this.timestamp > o.getTimestamp()) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean isSent() {
		return sent;
	}

	@Override
	public void setSent(boolean sent) {
		this.sent = sent;
	}

	@Override
	public String getTimeSeriesID() {
		return timeSeriesID;
	}

	public void setTimeSeriesID(String timeSeriesID) {
		this.timeSeriesID = timeSeriesID;
	}

	@Override
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public String get_id() {
		return _id;
	}

	@Override
	public void set_id(String _id) {// NOSONAR squid:S0017 - JPU: Parameter is
									// named by intention
		this._id = _id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SampledValue '").append(value).append("' of device '")
				.append(deviceID).append("' at '").append(new Date(timestamp));
		return sb.toString();
	}
}
