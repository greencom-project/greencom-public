package eu.greencom.xgateway.dispatcher.impl.service;

import java.util.Dictionary;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;

import eu.greencom.api.service.TimeSeriesManager;
import eu.greencom.mgm.webapiconsumer.api.service.WebApiConsumer;
import eu.greencom.mgmbroker.commandlayer.CommandLayerUtils;
import eu.greencom.xgateway.dispatcher.impl.utils.DispatcherCleaner;
import eu.greencom.xgateway.dispatcher.impl.utils.DispatcherWorker;

/**
 * Class instantiating a Thread that check if there is some data to be sent to
 * the manager and listens for events coming from the manager about data
 * successfully stored, updating LocalDataStorage accordingly
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class DispatcherImpl {

	private static final Logger LOG = Logger.getLogger(DispatcherImpl.class);

	private TimeSeriesManager timeSeriesManager;
	private DispatcherWorker dispatcherWorker;
	private DispatcherCleaner dispatcherCleaner;
	private WebApiConsumer webApiConsumer;

	// Require these values to change
	private Integer dispatchingPeriodInSecs = -1;
	private Integer cleaningPeriodInSecs = -1;
	private String dataWarehouseHost = null;
	

	// Config ID:
	// CommandLayerUtils.GW_CONFIGURATION_PID_PREFIX + .dispatcher

	// DWH timer
	private Timer tw;
	// Cleaner timer
	private Timer tc;

	public void bindTimeSeriesManager(TimeSeriesManager timeSeriesManager) {
		this.timeSeriesManager = timeSeriesManager;
	}

	public void unbindTimeSeriesManager(TimeSeriesManager timeSeriesManager) {
		this.timeSeriesManager = null;
		// ff
		LOG.debug("unbindTimeSeriesManager " + timeSeriesManager.toString());
	}

	public void bindWebApiConsumer(WebApiConsumer wac) {
		this.webApiConsumer = wac;
	}

	public void unbindWebApiConsumer(WebApiConsumer wac) {
		this.webApiConsumer = null;
		LOG.debug("unbindWebApiConsumer " + wac.toString());
	}

	public void activate(ComponentContext context) {
		configure(context.getProperties());
		LOG.debug("Dispatcher activated");
	}

	public void deactivate(ComponentContext context) {
		// Both have been set in activate()
		tw.cancel();
		tc.cancel();
		LOG.debug("Dispatcher deactivated");
	}

	public void modified(ComponentContext context) throws ConfigurationException {
		LOG.debug("New Dispatcher configuration received");
		configure(context.getProperties());
	}

	private void configure(Dictionary properties) {
		if (properties != null) {
			
			// Maximum number of values uploaded at once
			Integer limit = (Integer) properties.get("Limit");
			
			Integer dispatchingPeriod = (Integer) properties.get("DispatchingPeriodInSecs");
			if (dispatchingPeriodInSecs != dispatchingPeriod) {
				// On subsequent run
				if (tw != null) {
					tw.cancel();
				}
				dispatcherWorker = new DispatcherWorker(timeSeriesManager, webApiConsumer, limit);
				tw = new Timer();
				// Use the new value for scheduling
				tw.schedule(dispatcherWorker, 10, dispatchingPeriod * 1000);
				dispatchingPeriodInSecs = dispatchingPeriod;
				LOG.debug("Dispatcher DispatchingPeriodInSecs changed.");
			}

			Integer cleaningPeriod = (Integer) properties.get("CleaningPeriodInSecs");
			if (!(cleaningPeriodInSecs.equals(cleaningPeriod))) {
				// On subsequent run
				if (tc != null) {
					tc.cancel();
				}
				dispatcherCleaner = new DispatcherCleaner(timeSeriesManager);
				tc = new Timer();
				tc.schedule(dispatcherCleaner, 10, cleaningPeriod * 1000);
				cleaningPeriodInSecs = cleaningPeriod;
				LOG.debug("Dispatcher CleaningPeriodInSecs changed.");
			}
			LOG.debug("Dispatcher successfully configured.");
		}
	}

}
