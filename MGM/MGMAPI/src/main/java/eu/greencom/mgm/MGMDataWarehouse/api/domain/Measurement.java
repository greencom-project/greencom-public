package eu.greencom.mgm.MGMDataWarehouse.api.domain;//NOSONAR squid:S00120 - JPU: Package name reflects component naming and would become unreadable without camel-case style

import org.joda.time.DateTime;

/**
 * @deprecated
 */
@Deprecated
public class Measurement {
	public DateTime time;//NOSONAR squid:ClassVariableVisibilityCheck - Intended as serializable message, no need for private members
	public double Value;//NOSONAR findbugs:NM_FIELD_NAMING_CONVENTION  - JPU: Named by purpose as message property
}
