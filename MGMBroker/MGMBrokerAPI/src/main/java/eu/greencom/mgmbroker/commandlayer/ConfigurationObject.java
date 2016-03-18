package eu.greencom.mgmbroker.commandlayer;

import java.util.Map;

/**
 * Transport object for configuration to be exchanged by endpoints
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class ConfigurationObject {

	public ConfigurationObject() {
	}

	public ConfigurationObject(String pID, Map<String, Object> values) {
		super();
		pid = pID;
		this.values = values;
	}

	private String pid;
	private Map<String, Object> values;

	public String getPID() {
		return pid;
	}

	public Map<String, Object> getValues() {
		return values;
	}

}
