package eu.greencom.xgateway.localwebapi.rest;

import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.greencom.xgateway.localwebapi.fakeevent.SensorParameters;
import eu.greencom.xgateway.localwebapi.fakeevent.SensorsMap;

/**
 * @author Farmin Farzin
 * 
 *  returning the sensor properties including
 *        value,timestamp and name.
 */
public class SensorValueResource extends BaseServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(SensorValueResource.class);

	@Get("json")
	public String getrepresent() {
		LOG.debug("getting sensor value");
		Gson gson = new Gson();
		InvokeResponse resp = new InvokeResponse();
		// initially set 200, overwrite in case of exceptions
		resp.setCode(200);

		// Get variable_id parameter
		String sensor_id = (String) getRequest().getAttributes().get("sensor_id");

		// Result:
		SensorParameters params = SensorsMap.getInstance().get_one_sensor_params(sensor_id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("sensor_name", params.sensor_name);
		result.put("sensor_value", params.sensor_value);
		result.put("sensor_ts", params.sensor_timestamp);

		resp.setResult(result);

		this.addCustomHeaders();
		// write devices to client
		return gson.toJson(resp);
	}
}
