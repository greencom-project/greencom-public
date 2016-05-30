package eu.greencom.xgateway.localwebapi.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.domain.SampledValueImpl;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;
import eu.greencom.xgateway.localwebapi.WebAPIImpl;

/*
 * @author Farmin Farzin
 *  This class will respond to historic call to a sensor
 * Example of the url is:
 * http://localhost:9090/api/sensors/sensor_battery_soc/historic?from=2016-05-19T07:22:22Z&to=2016-05-19T14:22:22Z
 * 
 */

public class SensorHistoricResource extends BaseServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(SensorHistoricResource.class.getName());
	private LocalDataStorage store;

	@Get("json")
	public String getrepresent() {
		LOG.debug("getting sensors list");
		Gson gson = new Gson();
		InvokeResponse resp = new InvokeResponse();
		// initially set 200, overwrite in case of exceptions
		resp.setCode(200);

		// Get variable_id parameter
		String sensor_id = (String) getRequest().getAttributes().get("sensor_id");

		// Getting From and To
		Form queryParams = getRequest().getResourceRef().getQueryAsForm();

		Parameter from = queryParams.getFirst("from"); // ISO datetime
		Parameter to = queryParams.getFirst("to"); // ISO datetime

		// default values here
		long query_from = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
		long query_to = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		

		Calendar calendar = Calendar.getInstance();


		// converting the input to milliseconds
		try {
			if (from != null && from.getValue() != null) {
				String query_from_day = from.getValue();
				Date date = sdf.parse(query_from_day);
				calendar.setTime(date);
				query_from = calendar.getTimeInMillis();
			}

			if (to != null && to.getValue() != null) {
				String query_to_day = to.getValue();
				Date date;
				date = sdf.parse(query_to_day);
				calendar.setTime(date);
				query_to = calendar.getTimeInMillis();
			}
		} catch (ParseException e) {
			LOG.debug(e.getMessage());
		}
		
		LOG.info("from " + query_from + " to " + query_to);

		// Result:
		store = WebAPIImpl.getInstance().getStore();
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> x = new HashMap<String, Object>();
		x.put("$gte", query_from);
		x.put("$lte", query_to);
		query.put("timestamp", x);
		query.put("deviceID", sensor_id);
		List<Map<String, Object>> result = store.list("SampledValue", query, LocalDataStorage.NO_LIMIT);
		List<SampledValue> sampledValueList = new LinkedList<SampledValue>();
		for (Map<String, Object> r : result) {
			sampledValueList.add(new SampledValueImpl((String) r.get("deviceID"), (String) r.get("timeSeriesID"),
					(Long) r.get("timestamp"), (Double) r.get("value")));
		}

		resp.setResult(sampledValueList);

		this.addCustomHeaders();
		// write devices to client
		return gson.toJson(resp);
	}

}
