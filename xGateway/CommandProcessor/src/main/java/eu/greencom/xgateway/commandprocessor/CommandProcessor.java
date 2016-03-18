package eu.greencom.xgateway.commandprocessor;

import java.io.IOException;
import java.util.Dictionary;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.microsoft.windowsazure.services.servicebus.implementation.BrokerProperties;

import eu.greencom.mgm.servicebus.api.service.ServiceBus;
import eu.greencom.mgm.servicebus.types.ICallback;
import eu.greencom.mgm.webapiconsumer.api.service.WebApiConsumer;
import eu.greencom.xgateway.hpintegration.HPIntegrationService;
import eu.greencom.xgateway.integrationlayer.api.service.DeviceController;;

public class CommandProcessor {

	private static final Logger LOGGER = Logger.getLogger(CommandProcessor.class);

	public static final String PARAM_ACTION_CONTROL_SENSOR_STATUS = "ControlSensorStatus";
	public static final String PARAM_ACTION_HEAT_PUMP_STATUS = "HeatPumpStatus";

	public static final String PARAM_SENSOR_ID = "SensorId";
	public static final String PARAM_SENSOR_VALUE = "Value";
	public static final String PARAM_HEAT_PUMP_PROPERTY = "Property";
	public static final String PARAM_HEAT_PUMP_HEATCURVEOFFSET = "HeatCurveOffset";

	// HPIntegration
	private HPIntegrationService hpservice;

	private ServiceBus bus;
	private WebApiConsumer consumer;
	private ConfigurationAdmin configurationAdmin;
	private DeviceController deviceController;

	private String driverID = "";
	private Thread runner;

	ICallback<Command> icallback;

	// made a class to decrease complexity
	class Callbackff implements ICallback<Command> {

		@Override
		public void callback(Command cmd, BrokerProperties msgProps) {
			String id = cmd.getId();
			LOGGER.info("Received queue command: " + id);
			BrokerProperties props = new BrokerProperties();
			props.setSessionId(msgProps.getReplyToSessionId());
			props.setTimeToLive(4d);
			boolean commandSuccess = false;
			if (id.equalsIgnoreCase(PARAM_ACTION_CONTROL_SENSOR_STATUS)) {
				try {
					String sensorId = (String) cmd.getParameter(PARAM_SENSOR_ID);
					LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
							+ "Received queue command parameter ------- SensorId: " + sensorId);
					String value = (String) cmd.getParameter(PARAM_SENSOR_VALUE);
					LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
							+ "Received queue command parameter ------- Value:" + value);
					deviceController.put(sensorId, "rdf:value", value);
					commandSuccess = true;
				} catch (Exception e) {
					LOGGER.error(CommandProcessor.class.getSimpleName() + ": " + e, e);
				}
			} else if (id.equalsIgnoreCase(PARAM_ACTION_HEAT_PUMP_STATUS)) {
				try {
					String sensorId = (String) cmd.getParameter(PARAM_SENSOR_ID);
					LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
							+ "Received queue command parameter ------- SensorId: " + sensorId);
					String property = (String) cmd.getParameter(PARAM_HEAT_PUMP_PROPERTY);
					LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
							+ "Received queue command parameter ------- Property: " + property);
					String heatCurveOffset = (String) cmd.getParameter(PARAM_HEAT_PUMP_HEATCURVEOFFSET);
					LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
							+ "Received queue command parameter ------- HeatCurveOffset: " + heatCurveOffset);
					// deviceController.put(sensorId, property,
					// heatCurveOffset);
					// Implementation for Heat Pump
					int value = Integer.parseInt(heatCurveOffset);
					Boolean issuccess = hpservice.sendWrite(47011, value);
					if (issuccess) {
						LOGGER.debug("Has been Write Successfully");
						commandSuccess = true;
					} else {
						LOGGER.error("Has been Write Unsuccessfully");
					}

					// commandSuccess = true;
				} catch (Exception e) {
					LOGGER.error(CommandProcessor.class.getSimpleName() + ": " + e, e);
				}
			}
			try {
				LOGGER.info(CommandProcessor.class.getSimpleName() + ": "
						+ "Sent back to the API ------- commandSuccess: " + commandSuccess);
				CommandProcessor.this.bus.publishQueueMessage(commandSuccess, "mainrpcresponsequeue", props);
			} catch (Exception e) {
				LOGGER.error(CommandProcessor.class.getSimpleName() + ": " + e, e);
			}
		}

	}

	public void activate(BundleContext context) {
		LOGGER.info("activate " + context.toString());
		// new instance off icallback created and put in subscribe
		icallback = new Callbackff();
		runner = new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info(CommandProcessor.class.getSimpleName() + ": " + "------------------>RUN COMMAND PROCESSOR");
				while (isInvalid(driverID)) {
					try {
						Configuration c = configurationAdmin.getConfiguration("eu.greencom.xgateway");
						Dictionary<String, Object> d = c.getProperties();
						if (d != null) {
							driverID = (String) d.get("GATEWAY_ID");
							Thread.sleep(1000);
						}
					} catch (IOException e) {
						LOGGER.error(CommandProcessor.class.getSimpleName() + ": Error setting defautl properties " + e,
								e);
					} catch (InterruptedException e) {
						LOGGER.error(CommandProcessor.class.getSimpleName()
								+ ": Command Processor gateway id thread interrupted" + e, e);
					}
				}
				LOGGER.info(CommandProcessor.class.getSimpleName() + ": " + "------------------>GATEWAY ID: " + driverID
						+ "<----------- COMMANDPROCESSOR READY!");
				bus.setupServiceBus("greencom", "owner", "DxOfuOsXd4ZoohTfyaK3ny2e6hVNfOc8utroqRZBB9w=");
				bus.subscribeQueue(icallback, "rpcrequestqueue-" + driverID, 0, Command.class);
			}
		});
		runner.start();
	}

	public void deactivate(BundleContext context) {
		LOGGER.info("COMMAND PROCESSOR DEACTIVATED!");
		LOGGER.info("deactivate " + context.toString());

		bus.close();

		runner.interrupt();
		try {
			runner.join(20);
		} catch (InterruptedException e) {
			LOGGER.log(Level.ERROR, e.getMessage());

		}
	}

	private boolean isInvalid(String gwid) {
		if (gwid == null) {
			return true;
		}

		try {
			UUID.fromString(gwid);
			return false;
		} catch (Exception e) {
			LOGGER.error(CommandProcessor.class.getSimpleName() + ": " + e, e);
			return true;
		}
	}

	public void bindServiceBus(ServiceBus bus) {
		this.bus = bus;
	}

	public void unbindServiceBus(ServiceBus bus) {
		LOGGER.info("UnbindServiceBus " + bus.toString());
		this.bus = null;
	}

	public void bindDeviceController(DeviceController deviceController) {
		this.deviceController = deviceController;
	}

	public void unbindDeviceController(DeviceController deviceController) {
		this.deviceController = deviceController;
	}

	public void bindWebApiConsumer(WebApiConsumer consumer) {
		this.consumer = consumer;
		LOGGER.info("bindWebApiConsumer " + this.consumer.toString());
	}

	public void unbindWebApiConsumer(WebApiConsumer consumer) {
		LOGGER.info("UnbindWebApiConsumer " + consumer.toString());
		this.consumer = null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		LOGGER.info("UnbindConfiguration...");
		this.configurationAdmin = null;
	}

	protected void bindHPintegration(HPIntegrationService hpService) {
		this.hpservice = hpService;
		LOGGER.info("bindHPIntegration...");
	}

	protected void unbindHPintegration(HPIntegrationService hpService) {
		this.hpservice = null;
		LOGGER.info("UnbindHPIntegration...");
	}

}
