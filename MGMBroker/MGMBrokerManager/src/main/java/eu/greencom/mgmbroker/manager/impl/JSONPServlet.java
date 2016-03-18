package eu.greencom.mgmbroker.manager.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.greencom.mgm.webapiconsumer.model.Sensor;
import eu.greencom.mgmbroker.manager.api.CommunicationLayerMGR;
import eu.greencom.mgmbroker.manager.api.InstallationsChecker;
import eu.greencom.mgmbroker.manager.api.SystemHealthMonitor;

public class JSONPServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(JSONPServlet.class);

	private transient CommunicationLayerMGR communicationLayerMGR;

	private transient SystemHealthMonitor systemHealthMonitor;

	private ObjectMapper mapper = new ObjectMapper();

	private transient InstallationsChecker installationsChecker;

	public JSONPServlet(CommunicationLayerMGR communicationLayerMGR, SystemHealthMonitor systemHealthMonitor,
			InstallationsChecker installationsChecker) {
		this.communicationLayerMGR = communicationLayerMGR;
		this.systemHealthMonitor = systemHealthMonitor;
		this.installationsChecker = installationsChecker;
	}

	public void activate(ComponentContext context) {
		LOG.debug(context.getProperties().get(ComponentConstants.COMPONENT_NAME) + " activated");
	}

	public void deactivate(ComponentContext context) {
		LOG.debug(context.getProperties().get(ComponentConstants.COMPONENT_NAME) + " deactivated");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (isJSONPRequest(request)) {
			String callback = request.getParameter("callback");
			String action = request.getParameter("action");
			String jsonObject = null;

			switch (action) {
			case "getInstallations":
				Map<String, String> installations = this.systemHealthMonitor.getInstallations();
				Map<String, String> sortedInst = new TreeMap<String, String>(installations);
				jsonObject = mapper.writeValueAsString(sortedInst);
				break;
			case "checkInstallationByGateway":// NOSONAR squid:S1151
				String gatewayID = request.getParameter("gatewayId");
				Map<String, Object> nodeMap = new HashMap<String, Object>();
				nodeMap.put("gatewayId", gatewayID);
				Date aliveTimestamp = this.communicationLayerMGR.getGateways().get(gatewayID);
				if (aliveTimestamp != null) {
					nodeMap.put("lastAlive", aliveTimestamp.getTime());
				} else {
					nodeMap.put("lastAlive", "none");
				}
				String installationId = this.systemHealthMonitor.getInstallations().get(gatewayID);
				if (installationId != null) {
					nodeMap.put("installationId", installationId);
					List<Sensor> sensorsCheck = installationsChecker.checkInstallation(installationId);
					nodeMap.put("sensors", sensorsCheck);
				}
				jsonObject = mapper.writeValueAsString(nodeMap);
				break;
			case "sendReport":
				Thread t = new Thread(systemHealthMonitor);
				t.start();
				jsonObject = mapper.writeValueAsString(true);
				break;
			case "solicitateAliveMessage":// NOSONAR squid:S1151
				String gwID = request.getParameter("gatewayId");
				Date d = communicationLayerMGR.solicitateAliveMessage(gwID);
				if (d != null) {
					jsonObject = mapper.writeValueAsString(d.getTime());
				} else {
					ObjectNode n = mapper.createObjectNode();
					n = n.put("error", "Unable to reach Gateway " + gwID);
					jsonObject = n.toString();
				}
				break;
			default:
				jsonObject = "'Unsupported operation'";
			}
			String responseString = callback + "(" + jsonObject + ")";
			response.getWriter().write(responseString);
		} else {
			response.getWriter().write("This is a JSONP servlet, bad reqest");
		}
	}

	// Switches JSON generation
	private boolean isJSONPRequest(final HttpServletRequest request) {
		if (request.getParameterMap().containsKey("callback")) {
			return true;
		}

		return false;
	}

}
