package eu.greencom.mgm.MGMDataWarehouse.api.domain;//NOSONAR squid:S00120 - JPU: Package name reflects component naming and would become unreadable without camel-case style

/**
 * @deprecated
 */
@Deprecated
public class TimeSeries {
	public String sensorGuid;//NOSONAR squid:ClassVariableVisibilityCheck - Intended as serializable message, no need for private members
	public Measurement[] measurements;//NOSONAR squid:ClassVariableVisibilityCheck 
}
