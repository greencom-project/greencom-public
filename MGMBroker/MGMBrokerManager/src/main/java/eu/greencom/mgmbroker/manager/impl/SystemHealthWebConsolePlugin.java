package eu.greencom.mgmbroker.manager.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.greencom.mgmbroker.manager.api.CommunicationLayerMGR;
import eu.greencom.mgmbroker.manager.api.InstallationsChecker;
import eu.greencom.mgmbroker.manager.api.SystemHealthMonitor;

/**
 * Class implementing webconsole plugin exposing pages to access LinkSmart
 * network and sensors network features It register a JSONP servlet defined in
 * JSONPServlet.java Through this page is possible to:
 * <ul>
 * <li>get the list of configured gateways</li>
 * <li>send an email with the report of sensors network status</li>
 * <li>get the report of sensor network status for a single installation</li>
 * </ul>
 * 
 * @author einann
 *
 */
public class SystemHealthWebConsolePlugin extends AbstractWebConsolePlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * JPU: Made these members transient (workaround, since we can't expect
	 * mandatory HttpService be serializable). We do not expect heavy load and
	 * need for backup serialization by J2EE container.
	 */
	private transient InstallationsChecker installationsChecker;
	private transient SystemHealthMonitor systemHealthMonitor;
	private transient CommunicationLayerMGR communicationLayerMGR;
	private transient HttpService httpService;

	private static final String JSONP_SERVLET_ALIAS = "/jsonp";
	private static final Logger LOG = Logger.getLogger(SystemHealthWebConsolePlugin.class);

	public void activate(ComponentContext context) {// NOSONAR squid:S1172 -
													// JPU: Parameter required
													// by SCR
		// register jsonp servlet
		try {
			httpService.registerServlet(JSONP_SERVLET_ALIAS, new JSONPServlet(communicationLayerMGR,
					systemHealthMonitor, installationsChecker), null, null);
		} catch (ServletException | NamespaceException e) {
			LOG.error("Error registering JSONP servlet", e);
		}
	}

	@Override
	public String getLabel() {
		return "system-monitor";
	}

	@Override
	public String getTitle() {
		return "System Health Monitor";
	}

	public SystemHealthWebConsolePlugin() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		StringTokenizer tokenizer = new StringTokenizer(request.getPathInfo(), "/");
		String lastPath = "";
		while (tokenizer.hasMoreTokens()) {
			lastPath = tokenizer.nextToken();
		}
		if ("ajax-loader.gif".equals(lastPath)) {
			response.setContentType("image/gif");
			sendFile(response.getOutputStream(), "/res/ajax-loader.gif");
		} else if ("edit.png".equals(lastPath)) {
			response.setContentType("image/png");
			sendFile(response.getOutputStream(), "/res/edit.png");
		} else {
			super.doGet(request, response);
		}
	}

	@Override
	protected void renderContent(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		PrintWriter pw = response.getWriter();
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			response.setContentType("text/html");

			// Parametrize the page, it should not hard-code references to
			// GreenCom services
			String pageTemplate = readFile(getClass(), "/res/page.html");
			// No need to configure twice, retrieve from service impl.
			String dataWarehouse = ((InstallationsCheckerImpl) installationsChecker).getDataWarehouse();
			String page = pageTemplate.replaceAll("DATA_WAREHOUSE", dataWarehouse);
			pw.println(page);
		}
		pw.flush();
	}

	private final void sendFile(OutputStream stream, final String file) {
		InputStream inputStream = getClass().getResourceAsStream(file);

		byte[] buffer = new byte[4096];

		int length;
		try {
			while ((length = inputStream.read(buffer)) > 0) {
				stream.write(buffer, 0, length);
			}
		} catch (IOException e) {
			LOG.error("Unable to read file" + file, e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOG.error("Unable to close input stream", e);
			}
		}
		try {
			stream.flush();
		} catch (IOException e) {
			LOG.error("Error flushing output stream", e);

		}

	}

	private final String readFile(final Class clazz, final String templateFile) {
		InputStream templateStream = getClass().getResourceAsStream(templateFile);
		if (templateStream != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			try {
				int len = 0;
				while ((len = templateStream.read(data)) > 0) {
					baos.write(data, 0, len);
				}
				return baos.toString("UTF-8");
			} catch (IOException e) {
				// don't use new Exception(message, cause) because cause is 1.4+
				throw new IllegalStateException("readTemplateFile: Error loading " + templateFile + ": " + e);
			} finally {
				try {
					templateStream.close();
				} catch (IOException e) {
					LOG.error(e);
				}

			}
		}
		// template file does not exist, return an empty string
		log("readTemplateFile: File '" + templateFile + "' not found through class " + clazz);
		return "";
	}

	public void bindInstallationsChecker(InstallationsChecker installationsChecker) {
		this.installationsChecker = installationsChecker;
	}

	public void unbindInstallationsChecker(InstallationsChecker installationsChecker) {// NOSONAR
																						// squid:S1172
																						// -
																						// JPU:
																						// Parameter
																						// required
																						// by
																						// SCR
		this.installationsChecker = null;
	}

	public void bindSystemHealthMonitor(SystemHealthMonitor systemHealthMonitor) {
		this.systemHealthMonitor = systemHealthMonitor;
	}

	public void unbindSystemHealthMonitor(SystemHealthMonitor systemHealthMonitor) {// NOSONAR
																					// squid:S1172
																					// -
																					// JPU:
																					// Parameter
																					// required
																					// by
																					// SCR
		this.systemHealthMonitor = null;
	}

	public void bindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {
		this.communicationLayerMGR = communicationLayerMGR;
	}

	public void unbindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {// NOSONAR
																							// squid:S1172
																							// -
																							// JPU:
																							// Parameter
																							// required
																							// by
																							// SCR
		this.communicationLayerMGR = null;
	}

	public void bindHttpService(HttpService service) {
		this.httpService = service;
	}

	public void unbindHttpService(HttpService service) {// NOSONAR squid:S1172 -
														// JPU: Parameter
														// required by SCR
		this.httpService = null;
	}

}
