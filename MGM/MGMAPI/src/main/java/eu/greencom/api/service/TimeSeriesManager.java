package eu.greencom.api.service;

import java.util.List;
import java.util.Map;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.TimeSeries;

/**
 * Service for persistent storage and retrieval of time series data.
 * 
 * @see https://tempo-db.com/docs/api/series/
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface TimeSeriesManager {

	/**
	 * Creates a time series storage for given source and unit. 
	 * Attempts to initiate an existing storage result in an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param source - id of the time series
	 * @param unit - unit of measure
	 */
	void create(String deviceID, String timeSeriesID, String unit);

	/**
	 * Indicates whether a time series for given source exists.
	 * 
	 * @param source
	 *            URI of the data source.
	 * @return
	 */
	boolean exists(String deviceID, String timeSeriesID);

	/**
	 * Removes configuration and data for given time series.
	 * 
	 * @param source
	 *            URI of the data source.
	 */
	void removeTimeSeries(String deviceID, String timeSeriesID);

	/**
	 * Stores a sampled value for given source at timestamp supplied by the
	 * {@link SampledValue} eventually replacing an existing item.
	 * 
	 * @deprecated
	 * 
	 * @param source
	 *            URI of the data source.
	 * @param value
	 *            current value
	 * @return true if the value has been stored successfully, false otherwise.
	 */
	@Deprecated
	boolean put(String deviceID, String timeSeriesID, SampledValue value);
	
	/**
	 * Stores a sampled {@link SampledValue} eventually replacing an existing item.
	 * id will be auto-generated
	 * @param source
	 *            URI of the data source.
	 * @param value
	 *            current value
	 * @return true if the value has been stored successfully, false otherwise.
	 */
	boolean put(SampledValue value);	
	
	/**
	 * Stores a list of sampledvalues {@link SampledValue} eventually replacing an existing item.
	 * id will be auto-generated
	 * @param source
	 *            URI of the data source.
	 * @param value
	 *            current value
	 * @return true if the value has been stored successfully, false otherwise.
	 */
	boolean[] put(List<SampledValue> values);	
	
	/**
	 * Stores a list of sampled values for given source in one bulk request.
	 * 
	 * @deprecated
	 * 
	 * @param source
	 *            URI of the data source.
	 * @param value
	 *            value sampled at particular time.
	 * @return
	 */
	@Deprecated
	boolean put(String deviceID, String timeSeriesID, List<SampledValue> values);
	
	void removeSampledValues(Map<String, Object> query);
	
	/**
	 * Retrieves an URI list of existent time series.
	 * 
	 * @return
	 */
	List<String> listTimeSeriesID();

	/**
	 * Retrieves the first {@link SampledValue} of given time series.
	 * 
	 * @param source
	 *            URI of the data source.
	 * @return
	 */
	SampledValue firstValue(String source);

	/**
	 * Retrieves the last {@link SampledValue} of given time series.
	 * 
	 * @param source
	 *            URI of the data source.
	 * @return
	 */
	SampledValue lastValue(String source);

	/**
	 * Retrieves a {@link TimeSeries} for given source and interval. TODO:
	 * review support for folding functions etc.
	 * 
	 * @see https://tempo-db.com/docs/api/read/
	 * 
	 * @param source
	 * @param start
	 * @param end
	 * @return
	 */
	TimeSeries getTimeSeries(String deviceID, String source, long start, long end);
	
	/** 
	 * Retrieves a list of {@link TimeSeries} each one containing a list of unsent values.
	 *
	 * @param limit
	 * 
	 * @return
	 */
	List<SampledValue> getUnsentValues(int limit);


}