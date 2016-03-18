package eu.greencom.xgateway.integrationlayer.impl; //findbugs : SE_BAD_FIELD ff: explained in field declaration

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import eu.greencom.mgm.metainformationstore.api.domain.Graph;
import eu.greencom.mgm.metainformationstore.api.domain.PredicateValue;
import eu.greencom.mgm.metainformationstore.api.domain.PredicateValue.ValueType;
import eu.greencom.mgm.metainformationstore.api.domain.ResourceGraph;
import eu.greencom.xgateway.integrationlayer.api.service.IntegrationLayer;
import eu.greencom.xgateway.integrationlayer.api.service.MessageListener;

public class SerialWebConsolePlugin extends AbstractWebConsolePlugin {

	private static final Logger LOG = LoggerFactory.getLogger(SerialWebConsolePlugin.class.getName());

	private static final long serialVersionUID = 5231612586564345694L;

	private static final String JSON_MIMETYPE = "application/json";

	private static final String LABEL = "serial-webconsole";

	// ff
	private static final int MNUM1024 = 1024;
	private static final int MNUM128 = 128;
	private static final int MNUM200 = 200;

	private transient IntegrationLayer controller;

	private transient MessageUtil util = new MessageUtil();

	// Stores request responses
	private transient Map<String, String> responseMap = new HashMap<>();

	public SerialWebConsolePlugin() {
		super();
	}

	protected void setController(IntegrationLayer controller) {
		this.controller = controller;
	}

	protected void removeController(IntegrationLayer controller) {
		this.controller = null;
		LOG.info("removeController in SerialWebConsolePlugin " + controller.toString());
	}

	protected void activate(ComponentContext context) {
		activate(context.getBundleContext());
	}

	protected void deactivate(ComponentContext context) {
		deactivate();
		// ff
		LOG.info("Deactivate in SerialWebConsolePlugin " + context.toString());
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	// Top-level category header
	@Override
	public String getCategory() {
		return "GreenCom";
	}

	// Label nested beneath category
	@Override
	public String getTitle() {
		return "Integration layer";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (isJsonRequest(request)) {
			// Retrieve data
			PrintWriter pw = response.getWriter();
			response.setContentType(JSON_MIMETYPE);
			String requestId = request.getParameter("id");
			// Asking for previous request's response
			if (request.getParameterMap().containsKey("response") && requestId != null) {
				if (responseMap.containsKey(requestId)) {
					pw.write(util.writeJson(responseMap.get(requestId)));
				} else {
					// No data yet
					pw.write("{}");
				}
			} else if (request.getParameterMap().containsKey("deviceList")) {
				pw.write(util.writeJson(controller.getDeviceData()));
			} else {
				pw.write(util.writeJson(controller.getObservationData()));
			}
			pw.flush();
		} else {
			// Draw the UI
			super.doGet(request, response);
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String uri = request.getParameter("uri");
		String label = request.getParameter("label");
		String comment = request.getParameter("comment");
		// Store updated annotation
		if (uri != null) {
			Graph graph = controller.getDeviceData();
			synchronized (graph) {
				if (graph.containsKey(uri)) {
					ResourceGraph resource = graph.get(uri);
					resource.set("rdfs:label", new PredicateValue(label, ValueType.literal));
					resource.set("rdfs:comment", new PredicateValue(comment, ValueType.literal));
					controller.updateDeviceData(graph);
				}
			}

			// Instruct IL to receive new device list from coordinator
		} else if (request.getParameterMap().containsKey("requestDeviceData")) {
			controller.requestDeviceData();
		}
	}

	/**
	 * PUTs/changes values of the device model.
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String msg = getBody(request);

		try {
			JsonNode req = util.parseJson(msg);
			// Extract request id and register one-time handler
			final String requestId = req.get("id").asText();

			// Delete any previous response for this request ID
			synchronized (responseMap) {
				responseMap.remove(requestId);
			}

			// Let the IL update response map upon Coordinator response
			((SerialCommunicationAdapter) controller).setMessageHandler(new MessageListener() {
				@Override
				public void receive(String response) {
					// Update map of (actuation) responses
					// Get rid of junk characters
					responseMap.put(requestId, response);
				}
			}, requestId);
		} catch (Exception e) {
			LOG.error(SerialWebConsolePlugin.class.getName() + " : " + e, e.getMessage());
		}

		// Pass the unaltered message
		controller.send(msg);
		// Assume success: 200 (o.k.), wait for asynchronous response
		response.setStatus(MNUM200);
	}

	// Copy of: http://stackoverflow.com/a/14885950
	private String getBody(HttpServletRequest request) throws IOException {
		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				// ff recommendation : , "ISO-8859-1" to input stream
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
				char[] charBuffer = new char[MNUM128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					LOG.error(SerialWebConsolePlugin.class.getName() + ": " + ex, ex.getMessage());
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

	// Switches JSON generation
	private boolean isJsonRequest(final HttpServletRequest request) {
		String accept = request.getHeader("Accept");
		String mime = request.getContentType();
		return // GET
		(accept != null && accept.contains(JSON_MIMETYPE))
		// POST PUT
				|| (mime != null && mime.equals(JSON_MIMETYPE));
	}

	@Override
	protected void renderContent(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		PrintWriter pw = response.getWriter();
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			response.setContentType("text/html");
			pw.println(readTemplateFile(getClass(), "/res/include.html"));
		}
		pw.flush();
	}

	// Adopted from Event-plugin source
	private final String readTemplateFile(final Class clazz, final String templateFile) {
		InputStream templateStream = getClass().getResourceAsStream(templateFile);
		if (templateStream != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] data = new byte[MNUM1024];
			try {
				int len = 0;
				while ((len = templateStream.read(data)) > 0) {
					baos.write(data, 0, len);
				}
				return baos.toString("UTF-8");
			} catch (IOException e) {
				// don't use new Exception(message, cause) because cause is 1.4+
				// ff
				LOG.error(SerialWebConsolePlugin.class.getName() + " : " + e, e.getMessage());
			} finally {
				try {
					templateStream.close();
				} catch (IOException e) {
					LOG.error(SerialWebConsolePlugin.class.getName() + " : " + e, e.getMessage());
					/* ignore */
				}

			}
		}
		// template file does not exist, return an empty string
		log("readTemplateFile: File '" + templateFile + "' not found through class " + clazz);
		return "";
	}

}
