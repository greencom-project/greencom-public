package eu.greencom.xgateway.configurationbroker;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class ManagedServiceImpl implements ManagedService{
	
	ConfigAdminConfigurationBroker parent;
	String compName;
	
	public ManagedServiceImpl(String compName, ConfigAdminConfigurationBroker parent){
		this.compName=compName;
		this.parent=parent;
	}

	@Override
	public void updated(Dictionary<String, ?> arg0)
			throws ConfigurationException {
		parent.updated(this.compName,arg0);
	}

}
