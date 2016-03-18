package eu.greencom.mgmbroker.gateway.commandprovider;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.greencom.mgmbroker.gateway.MGMBrokerGateway;

/**
 * Class specifying some utility commands for the MGMBroker
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class MGMBrokerGWCommandProvider implements CommandProvider {

	private ConfigurationAdmin configurationAdmin;

	private  static final Logger LOG = Logger.getLogger(MGMBrokerGWCommandProvider.class);

	public void activate(ComponentContext context) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		LOG.info("Activating MGMBrokerGateway Command Provider");
	}

	@Override
	public String getHelp() {
		return "---MGMBroker Gateway commands---\n"
				+ "\tbrokergwConfig {managerId} - configures identifier of the manager's JsonRpc endpoint to be resolved\n";
	}

	/**
	 * Command line interface to configure the gateway
	 * 
	 * @param ci
	 */
	public Object _brokergwConfig(CommandInterpreter ci) {//NOSONAR squid:S00100 - JPU: Special name of a command method
		String managerId = ci.nextArgument();

		if (managerId == null || managerId.isEmpty()) {
			ci.println("Syntax error");
			return null;
		}

		Properties props = new Properties();

		props.put(MGMBrokerGateway.getManagerIDPropertyName(), managerId);
		try {
			configurationAdmin.getConfiguration(MGMBrokerGateway.getConfigurationPID()).update((Dictionary) props);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		}
		return null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172 - JPU: Parameter expected by SCR Runtime
		this.configurationAdmin = null;
	}

}
