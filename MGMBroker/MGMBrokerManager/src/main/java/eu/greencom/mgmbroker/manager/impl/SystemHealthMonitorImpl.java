package eu.greencom.mgmbroker.manager.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;

import eu.greencom.mgmbroker.manager.api.InstallationsChecker;
import eu.greencom.mgmbroker.manager.api.SystemHealthMonitor;

public class SystemHealthMonitorImpl implements ManagedService, SystemHealthMonitor {

	private ConfigurationAdmin configurationAdmin;
	private InstallationsChecker installationsChecker;

	private Map<String, String> gatewaysInstallations = new HashMap<String, String>();
	private String emailReceiverAddress;

	private static String INSTALLATION_IDS_PROPERTY = "INSTALLATIONS_IDS";
	private static String EMAIL_RECEIVER_ADDRESS_PROPERTY = "EMAIL_RECEIVER_ADDRESS";

	private Timer systemCheckTimer = new Timer();

	private static final Logger LOG = Logger.getLogger(SystemHealthMonitorImpl.class);

	public void activate(ComponentContext context) {
		LOG.info("Activating system health monitor");
		Dictionary props = new Hashtable<String, String>();//NOSONAR squid:S1149

		props.put(Constants.SERVICE_PID, getPID());
		context.getBundleContext().registerService(ManagedService.class, this, props);
	}

	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		if (properties == null) {
			Properties props = new Properties();
			props.put(INSTALLATION_IDS_PROPERTY, "");
			props.put(EMAIL_RECEIVER_ADDRESS_PROPERTY, "");
			try {
				configurationAdmin.getConfiguration(getPID()).update((Dictionary) props);
			} catch (IOException e) {
				LOG.error("Error updating configuration", e);
			}
			return;
		}

		// if properties are empty, the configuration have just been
		// initialized, so do nothing
		if (properties.get(INSTALLATION_IDS_PROPERTY) != null && "".equals(properties.get(INSTALLATION_IDS_PROPERTY))
				|| properties.get(EMAIL_RECEIVER_ADDRESS_PROPERTY) != null
				&& "".equals(properties.get(EMAIL_RECEIVER_ADDRESS_PROPERTY))) {
			return;
		}

		stopTimer();

		gatewaysInstallations.clear();
		StringTokenizer tokenizer = new StringTokenizer((String) (properties.get(INSTALLATION_IDS_PROPERTY)), "|");
		while (tokenizer.hasMoreTokens()) {
			String installation = (String) tokenizer.nextElement();
			StringTokenizer instTokenizer = new StringTokenizer(installation, ":");
			String installationID = (String) instTokenizer.nextElement();
			String gatewayID = (String) instTokenizer.nextElement();
			LOG.debug("Adding Installation-GatewayID mapping :" + installationID + " - " + gatewayID);
			gatewaysInstallations.put(installationID, gatewayID);
		}

		this.emailReceiverAddress = (String) properties.get(EMAIL_RECEIVER_ADDRESS_PROPERTY);

		startTimer();

	}

	private static String getPID() {
		return "eu.greencom.mgmbroker.manager.systemhealth";
	}

	private void stopTimer() {
		systemCheckTimer.cancel();
		systemCheckTimer.purge();
	}

	private void startTimer() {

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date now = new Date();

		// start from tomorrow if it's after 6 o'clock AM
		if (cal.getTime().compareTo(now) < 0) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}

		// schedule an installation checker every day
		systemCheckTimer = new Timer();
		systemCheckTimer.scheduleAtFixedRate(new MailSenderTask(installationsChecker, this.gatewaysInstallations,
				this.emailReceiverAddress), cal.getTime(), 24 * 60 * 60 * 1000);
	}

	public void deactivate(ComponentContext context) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR
		this.stopTimer();
	}

	// methods for components binding/unbinding
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {//NOSONAR squid:S1172
		this.configurationAdmin = null;
	}

	public void bindInstallationsChecker(InstallationsChecker installationsChecker) {//NOSONAR squid:S1172
		this.installationsChecker = installationsChecker;
	}

	public void unbindInstallationsChecker(InstallationsChecker installationsChecker) {//NOSONAR squid:S1172
		this.installationsChecker = null;
	}

	// methods from SystemHealthMonitor interface

	@Override
	public Map<String, String> getInstallations() {
		return this.gatewaysInstallations;
	}

	@Override
	public void sendReport() {
		installationsChecker.sendReport(this.emailReceiverAddress, this.gatewaysInstallations);

	}

	@Override
	public void run() {
		this.sendReport();

	}

}
