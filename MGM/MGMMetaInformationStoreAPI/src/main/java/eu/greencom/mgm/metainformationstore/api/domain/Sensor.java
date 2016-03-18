package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.Map;

public class Sensor {
	public String sensorGuid;//NOSONAR squid:ClassVariableVisibilityCheck - JPU: Serializable message properties 
	public Map<String,String> attributes;//NOSONAR squid:ClassVariableVisibilityCheck 
}
