package eu.greencom.xgateway.localwebgui;

import java.util.Map;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Farmin Farzin
 *
 *         Class for implementing the HTP server, creating local GUI for
 *         GreenCom software
 * 
 * 
 */
public class WebGUIImpl {
	private static final Logger LOG = LoggerFactory.getLogger(WebGUIImpl.class.getName());
	private HttpService httpService;
	private String conf_external_webgui_pages_path = "/var/www";
	private boolean getConf_use_external_webgui_pages = false;

	protected void activate(Map<String, Object> props) {
		LOG.info("local web gui activated");

		try {
			// registering the webroot bundle folder to be shown as
			// http://127.0.0.1/
			LOG.debug("registeration of index.html");
			httpService.registerResources("/index.html", "/webroot/index.html", null);

			// registering the bundle folders to be shown
			LOG.debug("registeration of webgui-static");
			httpService.registerResources("/lib", "/lib", null);
			httpService.registerResources("/js", "/js", null);
			httpService.registerResources("/templates", "/templates", null);
			httpService.registerResources("/views", "/views", null);
			httpService.registerResources("/pics", "/pics", null);

			// registering the servlet to serve all dynamic pages
			LOG.debug("registeration of servlet ");
			httpService.registerServlet("/webgui/*", new WebGUIApplicationServlet(this), null, null);

		} catch (NamespaceException e1) {
			LOG.warn("NamespaceException in registering resources", e1);
		} catch (ServletException e) {
			LOG.warn("NamespaceException in registeration of servlet", e);
		}

	}

	protected void deactivate(Map<String, Object> props) {
		LOG.info("local web gui deactivate");
	}

	protected void bindHttpService(HttpService httpService) {
		LOG.debug("bindHttpService [" + httpService + "]");
		this.httpService = httpService;
	}

	protected void unbindHttpService(HttpService httpService) {
		LOG.debug("unbindHttpService [" + httpService + "]");
		this.httpService = null;
	}

	public String getConf_external_webgui_pages_path() {
		return conf_external_webgui_pages_path;
	}

	public void setConf_external_webgui_pages_path(String conf_external_webgui_pages_path) {
		this.conf_external_webgui_pages_path = conf_external_webgui_pages_path;
	}

	public boolean isGetConf_use_external_webgui_pages() {
		return getConf_use_external_webgui_pages;
	}

	public void setGetConf_use_external_webgui_pages(boolean getConf_use_external_webgui_pages) {
		this.getConf_use_external_webgui_pages = getConf_use_external_webgui_pages;
	}

}
