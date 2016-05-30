package eu.greencom.xgateway.localwebapi.rest;

import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.greencom.xgateway.localwebapi.fakeevent.SensorsMap;

/**
 * @author Farmin Farzin
 * 
 *         Returning the sensor list json
 *
 */
public class SensorsResource extends BaseServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(SensorsResource.class);

	@Get("json")
	public String getrepresent() {
		LOG.debug("getting sensors list");
		Gson gson = new Gson();
		InvokeResponse resp = new InvokeResponse();
		// initially set 200, overwrite in case of exceptions
		resp.setCode(200);
		// Result:
		resp.setResult(SensorsMap.getInstance().getSensor_map().keySet());

		this.addCustomHeaders();
		// write devices to client
		return gson.toJson(resp);
	}
}
