package eu.greencom.mgm.MGMDataWarehouse.api.domain;//NOSONAR squid:S00120 - JPU: Package name reflects component naming and would become unreadable without camel-case style

import java.util.List;
import eu.greencom.api.domain.TimeSeries;

public class QueryResult {
	public List<TimeSeries> timeseries;//NOSONAR squid:ClassVariableVisibilityCheck - Intended as serializable message, no need for private members
}
