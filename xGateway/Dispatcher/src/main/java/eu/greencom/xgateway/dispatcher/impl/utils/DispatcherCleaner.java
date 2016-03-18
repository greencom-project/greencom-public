package eu.greencom.xgateway.dispatcher.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.greencom.api.service.TimeSeriesManager;

public class DispatcherCleaner extends TimerTask {

	private static final Logger LOG = Logger.getLogger(DispatcherCleaner.class);

	private TimeSeriesManager timeSeriesManager;

	public DispatcherCleaner(TimeSeriesManager timeSeriesManager) {
		this.timeSeriesManager = timeSeriesManager;
	}

	// Performs the blocking call to DB manager
	private Thread cleaner;

	@Override
	public void run() {
		try {
			// This should not happen, DispatcherImpl would have been reloaded
			if (timeSeriesManager == null) {
				LOG.error("Cannot clean the local db because the TIMESERIESMANAGER IS NULL!!!");
			} else {
				if (cleaner != null) {
					// Interrupt a blocked call
					if (!Thread.State.TERMINATED.equals(cleaner.getState())) {
						cleaner.interrupt();
						LOG.warn("DispatcherCleaner thread forced to terminate!");
					}
					cleaner = null;
				}
				// Null initially or in subsequent call
				if (cleaner == null) {
					cleaner = new Thread("DispatcherCleaner") {
						// Do the time consuming task
						@Override
						public void run() {
							Map<String, Object> query = new HashMap<String, Object>();
							query.put("sent", true);
							timeSeriesManager.removeSampledValues(query);
							LOG.debug("Cleaned stale values");
						}
					};
					cleaner.start();
				}
			}
		} catch (Exception e) {
			LOG.error("Failed to run cleaner task: " + e);
		}
	}
}
