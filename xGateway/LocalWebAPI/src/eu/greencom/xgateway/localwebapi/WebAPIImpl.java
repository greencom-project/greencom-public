package eu.greencom.xgateway.localwebapi;

import java.util.Map;

import javax.servlet.ServletException;

import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;
import eu.greencom.xgateway.localwebapi.rest.RestApplicationServlet;
import eu.greencom.xgateway.localwebapi.websocket.APIWebSocketServlet;

/**
 * @author Farmin Farzin - ISMB
 * 
 *         This Component class will register the rest API and websocket server
 *         service to expose to GUI
 */
public class WebAPIImpl {

	private static final Logger LOG = LoggerFactory.getLogger(WebAPIImpl.class.getName());
	private EventAdmin eventAdmin;
	private HttpService httpService;
	public static final String REST_PATH = "/api";
	public static final String WEBSOCKET_API_PATH = "/ws";
	private static WebAPIImpl instance = null;
	private LocalDataStorage store;


	public static WebAPIImpl getInstance() {
		return WebAPIImpl.instance;
	}

	protected void activate(Map<String, Object> props) {
		LOG.info("local web API activate");
		WebAPIImpl.instance = this;
		start();
	}

	protected void modified(Map<String, Object> props) {
		LOG.info("local web API modified");
		stop();
		configure(props);
		start();
	}

	protected void deactivate(Map<String, Object> props) {
		LOG.info("local web API deactivate");
		stop();
	}

	private void configure(Map<String, Object> properties) {
		// Doesn't have any configuration for now
	}

	private void start() {

		// Registering Rest Service
		try {
			LOG.debug("registering rest path");
			httpService.registerServlet(REST_PATH, new RestApplicationServlet(), null, null);
		} catch (ServletException e) {
			LOG.error("ServletException in registering rest path", e);
		} catch (NamespaceException e) {
			LOG.error("NamespaceException in registering rest path", e);
		}
		 // Registering web Socket Service
		try {
			LOG.debug("registering websocket api path");
			httpService.registerServlet(WEBSOCKET_API_PATH, new APIWebSocketServlet(), null, null);
		} catch (ServletException e) {
			LOG.error("ServletException in registering websocket api path", e);
		} catch (NamespaceException e) {
			LOG.error("NamespaceException in registering websocket api path", e);
			LOG.warn("NamespaceException", e);
		}
	}

	private void stop() {
		httpService.unregister(REST_PATH);
		httpService.unregister(WEBSOCKET_API_PATH);
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	protected void bindHttpService(HttpService httpService) {
		LOG.debug("bindHttpService [" + httpService + "]");
		this.httpService = httpService;
	}

	protected void unbindHttpService(HttpService httpService) {
		LOG.debug("unbindHttpService [" + httpService + "]");
		this.httpService = null;
	}

	
	public void bindLocalDataStorage(LocalDataStorage store) {
		LOG.info("Binding LocalDataStorage");
		this.store = store;
	}

	public void unbindLocalDataStorage(LocalDataStorage store) {
		LOG.info("Unbinding LocalDataStorage" + store);
		this.store = null;
	}
	
	public LocalDataStorage getStore(){
		return store;
	}
}
