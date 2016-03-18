package eu.greencom.xgateway.configurationbroker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;
import eu.greencom.xgateway.api.configurationbroker.ConfigurationListener;
import eu.greencom.xgateway.api.configurationbroker.LocalConfigurationBroker;
import eu.greencom.xgateway.configurationbroker.internals.ComponentRepository;
import eu.greencom.xgateway.configurationbroker.internals.XMLConfigurationWriterLoader;

/**
 * Class hosting the main implementation for the ConfigurationBroker service
 * 
 * @author riccardo.tomasi
 * */
public class XmlConfigurationBroker implements ConfigurationBroker,LocalConfigurationBroker{
	
	//TODO Note by Riccardo: shall I think about some mechanism to load configuration by default at startup ? Or will it managed somewhere else ?
	//TODO Note by Riccardo: we should also consider proper loading and dumping of configuration to file synchornized with OSGI component activate and deactivate.
	
	private static final String FILEPATH=System.getProperty("eu.greencom.config.location")+"/gc-config/greencom-config.xml";
	private final static Logger LOG = Logger.getLogger(XmlConfigurationBroker.class.getName());
	private Map<String,ComponentRepository> componentsMap=null;
	private Map<String,List<ConfigurationListener>> subscribers = null;
	
    public void activate(ComponentContext context){ //NOSONAR squid: S1172
    	LOG.info("Activating ConfigurationBrokerImpl...");
    	LOG.info("Loading XML configuration from file");
    	this.componentsMap = XMLConfigurationWriterLoader.load(FILEPATH);
    	LOG.info("ConfigurationBrokerImpl activation completed, comp is="+this.componentsMap);
    }
    
    public void deactivate(ComponentContext context){ //NOSONAR squid: S1172
    	LOG.info("Deactivating ConfigurationBrokerImpl...");
    	LOG.info("Saving XML configuration to file");
    	XMLConfigurationWriterLoader.dump(FILEPATH, componentsMap);
    	LOG.info("ConfigurationBrokerImpl deactivation completed");
    }
    
    public XmlConfigurationBroker(){
    	LOG.debug("ConfigurationBrokerImpl constructor");
    	this.componentsMap= new HashMap<String,ComponentRepository>();
    	this.subscribers = new HashMap<String,List<ConfigurationListener>>();
    }

	@Override
	public void subscribe(ConfigurationListener s, String componentName) {
		//I get the map for the component name
		if(!this.subscribers.containsKey(componentName)) {
			List<ConfigurationListener> l=new LinkedList<ConfigurationListener>();
			l.add(s);
			this.subscribers.put(componentName, l);
		}else{
			this.subscribers.get(componentName).add(s);
		}
	}
	
	@Override
	public void unsubscribe(ConfigurationListener listener, String componentName) {
		List<ConfigurationListener> ll=this.subscribers.get(componentName);
		ConfigurationListener toBeRemoved=null;
		if(ll!=null){
			for(ConfigurationListener l:ll)	{
				if(l.equals(listener)){
					toBeRemoved=l;
				}
			}
		}
		//RR == became !=
		if(toBeRemoved!=null){
			ll.remove(toBeRemoved);
		}
	}

	@Override
	public String[] getComponentsNames() {
		LOG.debug("GetComponentsNames called, comp="+this.componentsMap);
		String[] ret = new String[this.componentsMap.size()]; 
		LOG.debug("Names "+Arrays.toString(ret)); 
		this.componentsMap.keySet().toArray(ret);
		LOG.debug("Names "+Arrays.toString(ret));
		return ret;
	}

	@Override
	public void setConfigValue(String componentName, String key,
			String value) {
		
		LOG.debug("setting conf value for [component_name="+componentName+"] [key="+key+"] [value="+value+"]");
		
		if(!this.componentsMap.containsKey(componentName)) {
			ComponentRepository toadd = new ComponentRepository(componentName);
			this.componentsMap.put(componentName, toadd);
		}
		
		ComponentRepository rep = this.componentsMap.get(componentName);
		rep.setConfigValue(key, value);
		this.internalnotify(componentName, key, value);
	}

	
	/**
	 * Internal function for notifiying to subscribers
	 * */
	private void internalnotify(String componentName, String key, String value) {
		if(!this.subscribers.containsKey(componentName)) {
			return;
		}		
		
		List<ConfigurationListener> subs = this.subscribers.get(componentName);
		
		for (ConfigurationListener l: subs) {
			l.notify(componentName, key, value);
		}
	}

	@Override
	public String[] getAllConfigKeys(String componentName) {
		if(!this.componentsMap.containsKey(componentName)) {
			return null; //NOSONAR   squid:S1168 
		}
		
		ComponentRepository component = this.componentsMap.get(componentName); 
		return component.getAllConfigKeys();
	}

	@Override
	public String getConfigValue(String componentName, String key) {
		if(!this.componentsMap.containsKey(componentName)) {
			return null;  //NOSONAR   squid:S1168 
		}
		
		ComponentRepository component = this.componentsMap.get(componentName);
		return component.getConfigValue(key);
	}

	@Override
	public void unsetConfigKey(String componentName, String key) {
		if(!this.componentsMap.containsKey(componentName)) {
			return;
		}		
		ComponentRepository tmp = this.componentsMap.get(componentName);
		
		if(tmp!=null) {
			tmp.unsetConfigKey(key);
			this.internalnotify(componentName, key, null);
		}
	}

	@Override
	public void unsetConfigComponent(String componentName) {
		
		if(this.subscribers.containsKey(componentName)) {
			//I first get the full set of keys
			ComponentRepository configs = this.componentsMap.get(componentName);
			String[] keys = configs.getAllConfigKeys();
		
			for(String k: keys) {
				this.internalnotify(componentName, k, null);
			}
		}
		
		this.componentsMap.remove(componentName);
		
	}
}
