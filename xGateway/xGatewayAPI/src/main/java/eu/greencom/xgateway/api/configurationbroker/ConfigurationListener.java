package eu.greencom.xgateway.api.configurationbroker;

/**
 * The ConfigurationListener interface must be implemented by all sub-components
 * willing to receive notifications upon configuration values changes
 * 
 * @author riccardo.tomasi
 **/
public interface ConfigurationListener {

	/**
	 * the notify method is called when a configuration change occurs. The
	 * notification will both occurr when new parameters are added or when an
	 * existing parameter is changed.
	 * 
	 * @param componentName
	 *            the component_name used for registration.
	 * @param key
	 *            the key of the changed value.
	 * @param value
	 *            the value of the
	 * 
	 * */
	public void notify(String componentName, String key, String value);
}
