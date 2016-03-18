package eu.greencom.xgateway.deviceabstractionlayer.http.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPImpl implements ManagedService {

	private static final Logger LOG = Logger.getLogger(HTTPImpl.class.getName());
	HttpRestGetPost hc;
	ComponentContext context;
	private ConfigurationAdmin configAdmin;
	public static final String SERVICE_PID = "DeviceAbstractionLayerHTTP";
	private String dalIPAddress = "130.192.86.173";
	private ServiceRegistration<ManagedService> configService;
	private EventAdmin eventAdmin;
	SensorId sensorId;
	private ObjectMapper mapper = new ObjectMapper();
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private int callFunctionPeriod = 20; // 20 seconds for running function
	private int callDeviceRefresher = 300; // 5 minutes for refreshing device
	public String[] devices;
	private DeviceUpdater deviceupdater;
	private FunctionInvoker functioninvoker;
	private Timer du;
	private Timer fi;
	DeviceValue devicevalue;

	public void activate(ComponentContext context) {
		this.context = context;

		// For configuring the bundle
		Hashtable<String, String> d = new Hashtable<String, String>();
		d.put(Constants.SERVICE_PID, SERVICE_PID);
		configService = context.getBundleContext().registerService(ManagedService.class, this, d);
		
		hc = new HttpRestGetPost();
		devicevalue = new DeviceValue();

		deviceupdater = new DeviceUpdater(hc, devicevalue);
		deviceupdater.setDalIPAddress(dalIPAddress);
		du = new Timer();
		du.schedule(deviceupdater, 0, callDeviceRefresher * 1000);

		functioninvoker = new FunctionInvoker(hc, eventAdmin, devicevalue);
		functioninvoker.setDalIPAddress(dalIPAddress);
		fi = new Timer();
		fi.schedule(functioninvoker, 5000, callFunctionPeriod * 1000);

	}

	public void deactivate(ComponentContext context) {
		this.context = null;
		configService.unregister();
		System.out.println("Dal Http client Stopping....");

	}

	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		LOG.info("New configuration received");
		// this.configure(arg0);
		if (properties == null) {
			LOG.info("Configuration received is empty, set the default values");
			Properties p = new Properties();
			p.put("JemmaAddress", dalIPAddress);
			p.put("FunctionPeriodCall", callFunctionPeriod);
			p.put("DevicePeriodCall", callDeviceRefresher);
			try {
				configAdmin.getConfiguration(SERVICE_PID).update((Dictionary) p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		// if it is not empty, stop the scheduler and start it again with new
		// setting received by configuration setting
		String updatedDalAddress = (String) properties.get("JemmaAddress");
		int updatedDalFunctionPeriod = (Integer) properties.get("FunctionPeriodCall");
		int updatedDalDevicePeriod = (Integer) properties.get("DevicePeriodCall");
		// if(!updatedDalAddress.equals(dalIPAddress)){
		LOG.info("Changing DAL Ip address to " + updatedDalAddress);
		dalIPAddress = updatedDalAddress;
		callFunctionPeriod = updatedDalFunctionPeriod;
		callDeviceRefresher = updatedDalDevicePeriod;

		hc = new HttpRestGetPost();

		du.cancel();
		deviceupdater = new DeviceUpdater(hc, devicevalue);
		deviceupdater.setDalIPAddress(dalIPAddress);
		du = new Timer();
		du.schedule(deviceupdater, 0, callDeviceRefresher * 1000);

		fi.cancel();
		functioninvoker = new FunctionInvoker(hc, eventAdmin, devicevalue);
		functioninvoker.setDalIPAddress(dalIPAddress);
		fi = new Timer();
		fi.schedule(functioninvoker, 5000, callFunctionPeriod * 1000);
	}

	protected void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	protected void removeConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = null;
		LOG.info("removeConfigurationAdmin in serial CommunicationAdapter" + configAdmin.toString());
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

}
