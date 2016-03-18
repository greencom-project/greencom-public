package eu.greencom.xgateway.configurationbroker;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;
import eu.greencom.xgateway.api.configurationbroker.ConfigurationListener;
import eu.greencom.xgateway.api.configurationbroker.LocalConfigurationBroker;

public class ConfigAdminConfigurationBroker implements ConfigurationBroker, LocalConfigurationBroker {
	
	private ConfigurationAdmin config;
	private final static Logger LOG = Logger.getLogger(ConfigAdminConfigurationBroker.class.getName());
	private Map<String,List<ConfigurationListener>> subscribers = new HashMap<String, List<ConfigurationListener>>();
	private Map<String,ManagedServiceImpl> configAdminListeners=new HashMap<>();
	private List<ServiceRegistration<?>> services=new LinkedList<>();
	private BundleContext ctx;
	
	public void bindConfigAdmin(ConfigurationAdmin ca){ //NOSONAR /squid: S1172
		LOG.info("Binding Config Admin");
		this.config=ca;
	}
	
	public void unbindConfigAdmin(ConfigurationAdmin ca){ //NOSONAR /squid: S1172
		LOG.info("Unbinding Config Admin");
		this.config=null;
	}
	//component_nameRR
	@Override
	public void subscribe(ConfigurationListener listener, String componentName) {
		if(!this.subscribers.containsKey(componentName)) {
		    
			List<ConfigurationListener> l=new LinkedList<ConfigurationListener>();
			l.add(listener);
			this.subscribers.put(componentName, l);
		}else{
			this.subscribers.get(componentName).add(listener);
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
		//RR equal is changed in not equal in the following if 
		if(toBeRemoved!=null){
			ll.remove(toBeRemoved);
		}
	}
	
	public void activate(BundleContext ctx)	{
		LOG.info("Activating ConfigBroker");
		this.ctx=ctx;
	    LOG.info("Config Broker activation completed");
	}
	
	public void deactivate(BundleContext ctx){	//NOSONAR /squid: S1172
		LOG.info("Deactivating ConfigBroker");
	    for(ServiceRegistration<?> service:services)  {
			if (service != null) {
		        service.unregister();
		        service = null;
		    }
	    }
    }

	@Override
	public String[] getComponentsNames() {
		Configuration[] confList=null;
		try {
			confList = config.listConfigurations(null);
		} catch (IOException | InvalidSyntaxException e) {
			/* TODO Accurate check RRe.printStackTrace()*/
			LOG.error(ConfigAdminConfigurationBroker.class.getSimpleName() + ": " + e, e );
		}
		if(confList==null){
			return new String[0];
		}
		String[] res=new String[confList.length];
		for(int i=0; i<confList.length; i++){
			/*RRnew String*/
			res[i]= (confList[i].getPid());
		}
		return res;
	}

	@Override
	public void setConfigValue(String componentName, String key, String value) {
		Configuration conf=getConfiguration(componentName);
		if(conf==null){
			return;
		}
		Dictionary<String, Object> props = conf.getProperties();

		if (props == null) {
		    props = new Hashtable<String, Object>();
		}
		
		props.put(key, value);
		try {
			conf.update(props);
		} catch (IOException e) {
			// TODO Accurate check
			LOG.error(ConfigAdminConfigurationBroker.class.getSimpleName() + ": " + e, e );
		}
	}

	@Override
	public String[] getAllConfigKeys(String componentName) {
		Configuration conf=getConfiguration(componentName);

		Dictionary<String, Object> props=conf.getProperties();
		if(props==null){
			return new String[0];
		}
		String[] resp=new String[props.size()];
		int i=0;
		Enumeration<String> en=props.keys();
		while(en.hasMoreElements()){
			resp[i]=en.nextElement();
			i++;
		}
		return resp;
	}

	@Override
	public String getConfigValue(String componentName, String key) {
		Configuration conf=getConfiguration(componentName);

		Dictionary<String, Object> props=conf.getProperties();
		if(props==null){
			return null;
		}
		return (String) props.get(key);
	}

	@Override
	public void unsetConfigKey(String componentName, String key) {
		Configuration conf=getConfiguration(componentName);

		Dictionary<String, Object> props=conf.getProperties();
		if(props==null){
			return;
		}
		props.remove(key);
		
		try {
			conf.update(props);
		} catch (IOException e) {
			// TODO Accurate check
			LOG.error(ConfigAdminConfigurationBroker.class.getSimpleName() + ": " + e, e );
		}
	}

	@Override
	public void unsetConfigComponent(String componentName) {
		Configuration conf=getConfiguration(componentName);

		Dictionary<String, Object> props=conf.getProperties();
		if(props==null){
			return;
		}
		props=new Hashtable<String, Object>();
		try {
			conf.update(props);
		} catch (IOException e) {
			// TODO Accurate check
			LOG.error(ConfigAdminConfigurationBroker.class.getSimpleName() + ": " + e, e );
		}
	}

	public void updated(String compName, Dictionary<String, ?> props) throws ConfigurationException {
		LOG.info("Received notification for component "+compName);
		
		List<ConfigurationListener> s=subscribers.get(compName);
		if(s==null){
			return;
		}

		if(props==null)	{
			for(ConfigurationListener l:s){
				l.notify(compName, null, null);
			}
			return;
		}
			
		Enumeration<String> keys=props.keys();
		while(keys.hasMoreElements()){
			String key=keys.nextElement();
			for(ConfigurationListener l:s){
				l.notify(compName, key, (String) props.get(key));
			}
		}
	}
	private Configuration getConfiguration(String componentName){
		Configuration conf=null;
		try {
			conf=config.getConfiguration(componentName);
		} catch (IOException e) {
			// TODO Accurate check
			LOG.error(ConfigAdminConfigurationBroker.class.getSimpleName() + ": " + e, e );
			return null;
		}
		
		if(configAdminListeners.get(conf.getPid())==null){
			LOG.info("Attempt to create a new service config for component "+componentName);
			ManagedServiceImpl msi=new ManagedServiceImpl(componentName, this);
			Dictionary<String, Object> props = new Hashtable<String,Object>(); //NOSONAR squid: S1149 to discuss
		    props.put("service.pid", componentName);
		    services.add(ctx.registerService(ManagedService.class, msi, props));
		    configAdminListeners.put(componentName, msi);
		}else{
			LOG.info("component already exist");
		}
		return conf;
	}

}
