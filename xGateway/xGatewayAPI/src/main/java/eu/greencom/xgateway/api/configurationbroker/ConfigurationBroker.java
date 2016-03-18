package eu.greencom.xgateway.api.configurationbroker;

/**
 * The ConfigurationBroker interface enables read/write access to configuration
 * parameters at runtime. In case a configuration parameter is changed, all
 * listening sub-components will be notified.
 * 
 * This interface has been designed to be exposed to management components e.g.
 * running in the MGM. Modules willing to use the local ConfigurationBroker
 * module to store their own configuration parameters (or be notified in case of
 * changes), should use the LocalConfigurationBroker interface instead.
 * 
 * @author riccardo.tomasi
 * 
 * */
public interface ConfigurationBroker {

	/**
	 * Return the full list of components_name for whom the ConfigurationBroker
	 * is storing configuration parameters In case no components are defined, a
	 * zero-size array is returned.
	 * 
	 * @returns an array including the list of defined component names
	 */
	public String[] getComponentsNames();

	/**
	 * changes the configuration parameter with specified key and component_name
	 * to the given value.
	 * 
	 * @param componentName
	 *            the name of the component for whom I want to set a config
	 *            value
	 * @param key
	 *            the key for whom I want to set the value
	 * @param value
	 *            the value to be associated
	 */
	public void setConfigValue(String componentName, String key, String value);

	/**
	 * Returns the full set of keys for the parameters registered for a specific
	 * component name.
	 * 
	 * @param componentName
	 *            the name of the component for whom I want to get all available
	 *            config keys
	 * @returns a String array with all keys or null, if component has not been
	 *          defined.
	 */
	public String[] getAllConfigKeys(String componentName);

	/**
	 * Returns the configuration value registered to a specific component_name
	 * with the specified key.
	 * 
	 * @param componentName
	 *            the name of the component for whom I want to get a config
	 *            value
	 * @param key
	 *            the key for whom I want to query the value
	 * @returns the value associated to the requested key or null if this is
	 *          missing.
	 */
	public String getConfigValue(String componentName, String key);

	/**
	 * Deletes the stored configuration value from the repository In case the
	 * parameter is not available, this method does not fail.
	 * 
	 * @param componentName
	 *            the name of the component for whom I want to unset a config
	 *            value
	 * @param key
	 *            the key for whom I want to unset the value
	 * 
	 */
	public void unsetConfigKey(String componentName, String key);

	/**
	 * Deletes all the stored configuration value associated to component with
	 * component_name In case the component_name is not available, this method
	 * does not fail.
	 * 
	 * @param componentName
	 *            the name of the component for whom I want to unset the full
	 *            set of keys
	 */
	public void unsetConfigComponent(String componentName);

}
