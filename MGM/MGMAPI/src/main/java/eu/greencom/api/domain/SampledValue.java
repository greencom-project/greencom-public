package eu.greencom.api.domain;

import java.io.Serializable;

/**
 * Singular sensor reading. The source of the data is implicit.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 * @param <T>
 *            Data type.
 */
public interface SampledValue extends Serializable {

	String get_id();// NOSONAR squid:S00100 - JPU: Method/field name selected by
					// purpose (probably to match MongoDB _id property?).

	void set_id(String id);// NOSONAR squid:S00100 

	String getTimeSeriesID();

	String getDeviceID();

	long getTimestamp();

	double getValue();

	boolean isSent();

	void setSent(boolean sent);

}