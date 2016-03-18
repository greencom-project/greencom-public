package eu.greencom.xgateway.api.configurationbroker;

/**
 * The ConfigurationController interface enables controlling the stored
 * configuration either by downloading it from the ConfigurationBroker, or
 * dumping/loading to file.
 * 
 * @author riccardo.tomasi
 * */
public interface ConfigurationController {

	/**
	 * Dumps the full configuration to disk
	 * 
	 * @param filename
	 *            the file where configuration should be dumped (with full or
	 *            relative path)
	 */
	public void dumpFullConfigurationToXMLFile(String filename);

	/**
	 * Loads the full configuration from disk
	 * 
	 * @param filename
	 *            the file where configuration should be loaded from(with full
	 *            or relative path)
	 */
	public void loadFullConfigurationFromXMLFile(String filename);
}
