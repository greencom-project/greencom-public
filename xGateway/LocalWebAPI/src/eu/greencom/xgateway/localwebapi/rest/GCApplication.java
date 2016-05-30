package eu.greencom.xgateway.localwebapi.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;


/**
 * @author Farmin Farzin
 * Creating API paths for REST resources
 */
public class GCApplication extends Application {

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		// register restlet resources
		// get all sensors ids
		router.attach("/sensors", SensorsResource.class);
		// get last value of a sensor
		router.attach("/sensors/{sensor_id}", SensorValueResource.class);
		//get 100 last values for a sensor
		router.attach("/sensors/{sensor_id}/historic", SensorHistoricResource.class);
		
		return router;
	}

}
