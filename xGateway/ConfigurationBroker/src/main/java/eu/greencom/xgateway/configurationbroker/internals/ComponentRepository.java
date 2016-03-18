package eu.greencom.xgateway.configurationbroker.internals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;

public class ComponentRepository{
	
	private final static Logger LOG = Logger.getLogger(ComponentRepository.class.getName());
	private String componentName; //NOSONAR squid: S1134 //FIXME this is actually not checked at the moment: should I keep it ?
	private Map<String, String> conf;
	
	public ComponentRepository(String componentName){
		LOG.debug("ComponentsRepository constructor for component ["+componentName+"]");
		this.componentName=componentName;
		
		this.conf = new HashMap<String,String>();
	}

	public String getComponentName() {
		return this.componentName;
	}

	public void setConfigValue(String key, String value) {
		//TODO unused component_name: I could double-check the component name here, but at the moment it seems a little overkill
		LOG.debug("ComponentsRepository  [component="+componentName+"] setting [key="+key+"] [value="+value+"]");
		this.conf.put(key, value);
	}

	public String[] getAllConfigKeys() {
		//TODO unused component_name: I could double-check the component name here, but at the moment it seems a little overkill
		String [] ret = new String[this.conf.size()];
		return this.conf.keySet().toArray(ret);
	}

	public String getConfigValue(String key) {
		//TODO unused component_name: I could double-check the component name here, but at the moment it seems a little overkill
		return this.conf.get(key);
	}

	public void unsetConfigKey(String key) {
		//TODO unused component_name: I could double-check the component name here, but at the moment it seems a little overkill
		this.conf.remove(key);
	}
}
