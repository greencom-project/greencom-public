package eu.greencom.mgmbroker.manager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.greencom.mgm.webapiconsumer.api.service.WebApiConsumer;
import eu.greencom.mgm.webapiconsumer.model.Sensor;
import eu.greencom.mgmbroker.manager.api.InstallationsChecker;

public class InstallationsCheckerImpl implements InstallationsChecker {

	private static final Logger LOG = Logger.getLogger(InstallationsCheckerImpl.class);

	private String pilotTracker = null;

	private String dataWarehouse = null;

	private WebApiConsumer webApiConsumer;

	private String smtpServer;

	private String smtpServerPort;

	private String smtpUser;

	private String smtpPassword;

	public String getPilotTracker() {
		return pilotTracker;
	}

	public String getDataWarehouse() {
		return dataWarehouse;
	}

	private void configure(Dictionary<String, Object> properties) {
		if (properties != null) {
			pilotTracker = (String) properties.get("PILOT_TRACKER");
			dataWarehouse = (String) properties.get("DATA_WAREHOUSE");
			smtpServer = (String) properties.get("SMTP_SERVER");
			smtpServerPort = (String) properties.get("SMTP_SERVER_PORT");
			smtpUser = (String) properties.get("SMTP_USER");
			smtpPassword = (String) properties.get("SMTP_PASSWORD");
		}
	}

	protected void activate(ComponentContext context) {
		configure(context.getProperties());
		LOG.debug("Activated: " + getClass().getSimpleName());
	}

	protected void modified(ComponentContext context) {
		configure(context.getProperties());
		LOG.debug("Modified: " + getClass().getSimpleName());
	}

	protected void deactivate(ComponentContext context) {
		LOG.debug("Deactivated: " + getClass().getSimpleName());
	}

	@Override
	public List<Sensor> checkInstallation(String installationID) {

		List<Sensor> sensors = webApiConsumer.getRealSensors(installationID);

		Collections.sort(sensors, new Comparator<Sensor>() {

			@Override
			public int compare(Sensor o1, Sensor o2) {
				return o1.getId().compareTo(o2.getId());
			}

		});
		return sensors;
	}

	public void bindWebApiConsumer(WebApiConsumer webApiConsumer) {
		this.webApiConsumer = webApiConsumer;
	}

	public void unbindWebApiConsumer(WebApiConsumer webApiConsumer) {// NOSONAR
																		// squid:S1172
		this.webApiConsumer = null;
	}

	@Override
	public void sendReport(String toAddress, Map<String, String> installations) {
		ReportMailSender mailSender = new ReportMailSender(smtpServer, smtpServerPort, smtpUser, smtpPassword);
		try {
			mailSender.sendHTMLEmail(toAddress, "[GreenCom] Automatic report from installations",
					prepareReportFromTracker());
		} catch (Exception e) {
			LOG.error("Error sending email", e);
		}
	}

	private String prepareReportFromTracker() throws Exception {// NOSONAR
																// squid:S00112
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet request = new HttpGet(pilotTracker);
		CloseableHttpResponse resp;
		resp = httpClient.execute(request);
		StatusLine statusLine = resp.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 200 && statusCode < 300) {
			String html = EntityUtils.toString(resp.getEntity());
			return html;
		} else {
			throw new Exception("The Pilot Tracker returned an error");// NOSONAR
																		// squid:S00112:
																		// Generic
																		// exception
																		// is
																		// enough
		}

	}

	@Deprecated
	/**
	 * @deprecated
	 * @param installations
	 * @return
	 */
	private String prepareReport(Map<String, String> installations) {// NOSONAR
																		// squid:UnusedPrivateMethod

		StringBuilder mailBody = new StringBuilder();
		Date now = new Date();
		List<String> sortedInst = new ArrayList<String>(installations.keySet());
		Collections.sort(sortedInst);
		for (String installation : sortedInst) {
			// here add call to function that checks an installation and returns

			List<Sensor> sensors = this.checkInstallation(installations.get(installation));
			StringBuilder buf = new StringBuilder("");
			int errFound = 0;

			for (Sensor sensor : sensors) {
				// the last value is older than 1 hour?
				if (now.getTime() - sensor.lastDateValue.getTime() > 60 * 60 * 1000) {
					errFound++;
					buf.append("<tr>");
					buf.append("<td><a href=\"" + dataWarehouse + "/Sensors/" + sensor.getId() + "\">" + sensor.getId()
							+ "</a></td>" + "<td>" + sensor.getName() + "</td>" + "<td>" + sensor.getType() + "</td>"
							+ "<td><font color=\"red\">" + sensor.lastDateValue.toString() + "</font></td>"
							+ "<td><a href=\"" + dataWarehouse + "/installations/" + installations.get(installation)
							+ "/Sensors/" + sensor.getId() + "/Details\">Edit</a></td>");
					buf.append("</tr>");
				}
			}
			mailBody.append("<p>Errors on installation [LinkSmart JSONRPC SERVER ID: <b>" + installation + "</b>, "
					+ "DataWareHouse Installation ID: <a href=\"" + dataWarehouse + "/installations/"
					+ installations.get(installation) + "/Sensors\">" + installations.get(installation) + "</a>] </p>");
			if (errFound > 0) {
				mailBody.append("<p>Found errors on <i>" + errFound + "</i> out of <i>" + sensors.size()
						+ "</i> sensors</p>");
				mailBody.append("<table border=\"1\"><thead><th>Sensor Id</th>" + "<th>Name</th>" + "<th>Type</th>"
						+ "<th>Last seen date</th>" + "<th>Edit Details</th>" + "</thead><tbody>" + buf
						+ "</tbody></table>");
			} else {
				mailBody.append("<p><i>no error found</i></p>");
			}
			mailBody.append("<hr>");

		}
		return mailBody.toString();
	}

}
