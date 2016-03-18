package eu.greencom.mgmbroker.commandlayer;

public class CommandLayerUtils {

	private CommandLayerUtils() {

	}

	/**
	 * This is the prefix PID used by the MGM to store configurations for GW
	 * components
	 */
	public static final String MGM_CONFIGURATION_PID_PREFIX = "eu.greencom.mgmbroker.manager.commandlayer";

	/**
	 * Returns the prefix of the PID to be used when registering a
	 * managedservice receiving configuration updates from the manager
	 * 
	 * @return
	 */
	public static final String GW_CONFIGURATION_PID_PREFIX = "eu.greencom.mgmbroker.gateway.commandlayer";

	/**
	 * Converts a PID from MGM format to GW format
	 * 
	 * @param pid
	 * @return
	 */
	public static String toGatewayConfigurationPID(String pid) {
		return pid.replaceAll(MGM_CONFIGURATION_PID_PREFIX, GW_CONFIGURATION_PID_PREFIX);
	}

	/**
	 * Converts a pid from GW format to MGM format
	 * 
	 * @param pid
	 * @return
	 */
	public static String toManagerConfigurationPID(String pid) {
		return pid.replaceAll(GW_CONFIGURATION_PID_PREFIX, MGM_CONFIGURATION_PID_PREFIX);
	}
}
