package eu.greencom.mgm.MGMDataWarehouse.api.service;//NOSONAR squid:S00120 - JPU: Package name reflects component naming and would become unreadable without camel-case style 

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;

import eu.greencom.api.domain.TimeSeries;
import eu.greencom.api.service.TimeSeriesManager;
import eu.greencom.mgm.MGMDataWarehouse.api.domain.QueryResult;

public interface MGMDataWareHouseService extends TimeSeriesManager {

	QueryResult getData(List<String> sensorIds, DateTime profilingStart,
			DateTime profilingEnd, Period frequency);

	/**
	 * Adds a {@link java.util.List} of
	 * {@link eu.greencom.api.domain.TimeSeries} to data warehouse
	 * 
	 * @deprecated
	 * 
	 * @param timeseries
	 *            a {@link java.util.List} of
	 *            {@link eu.greencom.api.domain.TimeSeries} to be added
	 * @return true if the operation was successful, false otherwise
	 */
	@Deprecated
	boolean addTimeSeries(List<TimeSeries> timeseries);

}
