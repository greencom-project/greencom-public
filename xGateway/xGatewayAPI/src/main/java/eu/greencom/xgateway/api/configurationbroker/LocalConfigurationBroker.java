package eu.greencom.xgateway.api.configurationbroker;

/**
 * The LocalConfigurationBroker interface can used by any local module running
 * inside xGateway to query Configuration parameters. It also provides the
 * possibility to subscribe to configuration changes.
 * 
 * This interface has been designed to support modules willing to use the local
 * ConfigurationBroker module to store their own configuration parameters (or be
 * notified in case of changes) Management modules, e.g. running in the MGM,
 * willing to control the ConfigurationBroker remotely, should use the
 * ConfigurationBroker interface instead.
 * 
 * @author riccardo.tomasi
 * */
public interface LocalConfigurationBroker extends ConfigurationBroker{

//	/**
//	 * Returns the full set of keys for the parameters registered for a specific component
//	 * name.
//	 * 
//	 * @param component_name
//	 *            the name of the component for whom I want to get all available
//	 *            config keys
//	 * @returns a String array with all keys or null, if component has not been
//	 *          defined.
//	 */
//	
//
//	/**
//	 * Returns the configuration value registered to a specific component_name
//	 * with the specified key.
//	 * 
//	 * @param component_name
//	 *            the name of the component for whom I want to get a config
//	 *            value
//	 * @param key
//	 *            the key for whom I want to query the value
//	 * @returns the value associated to the requested key or null if this is
//	 *          missing.
//	 * 
//	 */
//	public String getConfigValue(String component_name, String key); //NOSONAR RR

	/**
	 * Subscribe a ConfigurationListener to all changes for the given
	 * component_name.
	 * 
	 * If a ConfigurationListener for some reasons wants to register to
	 * different component_names (which is BTW not really common) it should
	 * spawn multiple delegates, each implementing a ConfigurationListener)
	 * 
	 * @param listener
	 *            the ConfigurationListener I am willing to subscribe
	 * @param componentName
	 *            the component_name of interest
	 */
	public void subscribe(ConfigurationListener listener, String componentName);

	public void unsubscribe(ConfigurationListener listener, String componentName);
}
