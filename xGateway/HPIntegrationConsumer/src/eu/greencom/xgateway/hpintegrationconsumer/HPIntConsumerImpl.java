package eu.greencom.xgateway.hpintegrationconsumer;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import eu.greencom.xgateway.hpintegration.HPIntegrationService;
import eu.greencom.xgateway.hpintegrationconsumer.constant.Const;

/**
 * @author Farmin Farzin Implementation class of a bundle for passing parameter
 *         to Heat Pump Integration Service.
 */
public class HPIntConsumerImpl implements ManagedService {

	private static final Logger LOG = Logger.getLogger(HPIntConsumerImpl.class.getName());
	private ReadRunnerThread readRunnerThread;
	private Thread readthread;
	private WriteRunnerThread writeRunnerThread;
	private Thread writethread;
	ComponentContext context;
	public ConfigurationAdmin configAdmin;
	public static final String PID = "HPIntegrationConsumer";
	private ServiceRegistration<ManagedService> configService;
	private EventAdmin eventAdmin;
	HPIntegrationService hpservice;
	String readActivateDefault = "false";
	Boolean readStatus = false;
	Boolean writeStatus = false;
	public int parameterToRead;
	public int parameterToWrite;
	public int writeValue = 0;
	public String preStringName = "NoHouseName";
	public long timeUpdatePeriod = 60000;

	public void activator(ComponentContext context) {
		this.context = context;
		LOG.debug("HP integraion Consumer Starting....");

		parameterToRead = Const.HeatcurveS1;
		parameterToWrite = Const.Language;
		// configuring the bundle
		Hashtable<String, String> d = new Hashtable<String, String>();
		d.put(Constants.SERVICE_PID, PID);
		configService = context.getBundleContext().registerService(ManagedService.class, this, d);

		// restart();

	}

	// this function read the properties from configuration admin and start
	// reading
	private void restart() {
		// initial Configuration
		Dictionary<String, ?> prop;
		prop = null;
		try {
			prop = configAdmin.getConfiguration(PID).getProperties();

		} catch (IOException e) {
			LOG.error("IOException" + e);
		}
		// reading the pre string factor which we use it for uploading GC as a
		// unique factor for each house
		preStringName = (String) prop.get("prestring");

		// ************Reading***************
		if (prop.get("readactive") == null) {
			LOG.warn("Configuration is empty");
			return;
		}

		// check the activate status of reading and also update the parameters
		// from Configuration

		String readActivate = (String) prop.get("readactive");
		readStatus = (Boolean) prop.get("readstatus");
		timeUpdatePeriod = (Long) prop.get("timeupdateperiod");
		LOG.debug("readactive is : " + readActivate);

		if (prop.get("readparameter") != null) {
			parameterToRead = (Integer) prop.get("readparameter");
		} else {
			readActivate = "false";
		}

		if (readStatus) {
			// if reading should be stopped
			if (readActivate.contains("stop")) {
				readRunnerThread.stop();
			} else {
				LOG.debug("thread starts to read");

				if (readRunnerThread == null) {

					// Creating Thread for Reading...
					readRunnerThread = new ReadRunnerThread(eventAdmin, hpservice, timeUpdatePeriod, preStringName);
					readthread = new Thread(readRunnerThread);
					// start reading from modbus
					readthread.start();
				}
			}
		}
		// ************* Writting ***********************
		// check the activate Status of writting

		writeStatus = (Boolean) prop.get("writestatus");
		writeValue = (Integer) prop.get("writevalue");
		if (prop.get("writeparameter") != null) {
			parameterToWrite = (Integer) prop.get("writeparameter");
		} else {
			System.out.println("write parameter is empty so putting default value");
		}

		if (writeStatus) {
			LOG.debug("thread start to writting this parameter: " + parameterToWrite);

			// Creating Thread for Writting
			writeRunnerThread = new WriteRunnerThread(hpservice, parameterToWrite, writeValue);
			writethread = new Thread(writeRunnerThread);

			// write to modbus
			writethread.start();
		}

	}

	public void deactivate(ComponentContext context) {
		this.context = null;
		if (readRunnerThread != null) {
			if(readthread != null){
				readthread.destroy();
			}
			readRunnerThread.stop();
			//readthread.destroy();
			readRunnerThread = null;
			readthread = null;
		}
		configService.unregister();
		LOG.debug("HP integraion Consumer Stopping....");
	}

	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		LOG.info("ManagedService updating");
		LOG.debug("ManagedService updating");
		// checking the properties
		if (properties == null) {
			// use default configuration
			Properties p = new Properties();
			p.put("readactive", readActivateDefault);
			p.put("readparameter", parameterToRead);
			p.put("writeparameter", parameterToWrite);
			p.put("readstatus", readStatus);
			p.put("writestatus", writeStatus);
			p.put("writevalue", writeValue);
			p.put("prestring", preStringName);
			p.put("timeupdateperiod", timeUpdatePeriod);

			try {
				configAdmin.getConfiguration(PID).update((Dictionary) p);
				;
			} catch (IOException e) {
				LOG.warn("IOException" + e);
			}
			return;
		}

		this.restart();
	}

	protected void bindHPintegration(HPIntegrationService hpService) {
		this.hpservice = hpService;
	}

	protected void unbindHPintegration(HPIntegrationService hpService) {
		this.hpservice = null;
	}

	protected void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	protected void removeConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = null;
		LOG.info("removeConfigurationAdmin removed");
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}
}
