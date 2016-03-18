package eu.greencom.api.domain;


import java.io.Serializable;
import java.util.List;

/**
 * Series of typed {@link SampledValue}s. Further (statistical) data might be
 * added in future versions. Compare: https://tempo-db.com/docs/api/read/
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 * @param <T>
 */
public interface TimeSeries extends Serializable{

	/**
	 * Identifier of the timeSeries
	 */
	String getTimeSeriesID();
	
	/**
	 * Identifier of the measurement unit.
	 * 
	 * @return
	 */
	String getUnit();

	/**
	 * URI of the device (sensor).
	 * 
	 * @return
	 */
	String getDeviceID();

	/**
	 * First time stamp of the series.
	 * 
	 * @return
	 */
	long getStart();

	/**
	 * Last time stamp of the series.
	 * 
	 * @return
	 */
	long getEnd();

	/**
	 * Retrieves a {@link SampledValue} at given time stamp.
	 * 
	 * @param time
	 * @return
	 */
	SampledValue getValue(long time);

	/**
	 * Lists all {@link SampledValue}s within given time range.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	List<SampledValue> getValues(long start, long end);

	/**
	 * List all {@link SampledValue}s
	 * @return
	 */
	List<SampledValue> getValues();
	
	boolean isEquidistant();

	/**
	 * Number of entries.
	 * 
	 * @return
	 */
	int size();

}