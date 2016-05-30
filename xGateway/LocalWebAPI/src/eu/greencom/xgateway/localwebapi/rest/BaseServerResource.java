package eu.greencom.xgateway.localwebapi.rest;

import org.restlet.engine.header.Header;
import org.restlet.resource.Options;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseServerResource extends ServerResource {
	private static final Logger LOG = LoggerFactory.getLogger(BaseServerResource.class.getName());

	// Adds custom header
	protected void addCustomHeaders() {
		LOG.debug("adding custom headers");
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
			responseHeaders = new Series(Header.class);
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		// allow request from other origins
		responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));
	}

	// Method added for CORS requests support: pre-flight request use OPTION
	// method
	@Options
	public void doOptions() {
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
			responseHeaders = new Series(Header.class);
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add("Access-Control-Allow-Origin", "*");
		responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
		responseHeaders.add("Access-Control-Max-Age", "60");
	}

}
