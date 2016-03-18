package eu.greencom.xgateway.gwdatabroker.impl.service;


import org.apache.log4j.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.service.TimeSeriesManager;

public class GWDataBroker implements EventHandler{

	private static final  Logger LOG=Logger.getLogger(GWDataBroker.class.getName());
	
	private TimeSeriesManager timeSeriesManager;
	
	public void bindTimeSeriesManager(TimeSeriesManager timeSeriesManager){
		this.timeSeriesManager=timeSeriesManager;
	}
	/*RRTimeSeriesManager timeSeriesManager*/
	public void unbindTimeSeriesManager(TimeSeriesManager timeSeriesManager){ //NOSONAR , the parameter is required by  osgi as standard configuration
		this.timeSeriesManager=null;
	}
	
	public void activate(){
		LOG.info("Activating GWDataBroker");
	}
	
	@Override
	public void handleEvent(Event arg0) {

		SampledValue sample=(SampledValue) arg0.getProperty("sampledValue");
		LOG.info("Adding Sampled data to localstorage");
		//they have not been sent to the mgmdatawarehouse at the moment, set this parameter to false
		sample.setSent(false);
		this.timeSeriesManager.put(sample);

		LOG.info("Data added");
	}

}
