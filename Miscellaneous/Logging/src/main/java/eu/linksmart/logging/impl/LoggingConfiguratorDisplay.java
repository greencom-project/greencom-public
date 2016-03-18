package eu.linksmart.logging.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.logging.LoggingConfigurator;

/**
 * Apache Webconsole plug-in for management of logging configuration.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class LoggingConfiguratorDisplay extends HttpServlet {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(LoggingConfiguratorDisplay.class.getName());

	private static final long serialVersionUID = 4397125877111811771L;

	// PAX Logging configuration PID
	public static final String CONFIG_PID = "org.ops4j.pax.logging";

	private LoggingConfigurator configurator = null;

	protected void setLoggingConfigurator(LoggingConfigurator configurator) {
		this.configurator = configurator;
	}

	protected void removeLoggingConfigurator(LoggingConfigurator configurator) {
		this.configurator = null;
	}

	protected void activate(ComponentContext context) {

	}

	protected void deactivate(ComponentContext context) {
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			// GET form
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				response.setContentType("text/html");
				Properties p = configurator.getConfiguration();
				String config = formatConfiguration(p);
				String template = readFile("/res/include.html");
				String page = template.replace("%CONFIG%", config);
				pw.println(page);
				// LOG.debug("Rendered configuration page {}", page);
			} else
			// Handle POSTed data
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				String data = readRequestPayload(request);
				Properties configuration = parsePropertiesString(data);
				if (configuration != null) {
					configurator.configure(configuration);
					LOG.debug("Stored configuration {}", data);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			pw.flush();
			pw.close();
		}
	}

	/*
	 * Concatenate properties sorted by their names as text-area value.
	 */
	private String formatConfiguration(Properties p) {

		StringBuffer sb = new StringBuffer();
		List<String> sortedKeys = new ArrayList<String>();
		for (String key : p.stringPropertyNames()) {
			sortedKeys.add(key);
		}
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			// Put the loggers first
			public int compare(String left, String right) {
				if (left.startsWith("log4j.rootLogger")
						|| left.startsWith("log4j.logger")) {
					return -1;
				}
				if (right.startsWith("log4j.rootLogger")
						|| right.startsWith("log4j.logger")) {
					return 1;
				}
				return left.compareTo(right);
			}
		};
		Collections.sort(sortedKeys, comparator);
		for (String key : sortedKeys) {
			sb.append(key).append(" = ").append(p.get(key)).append('\n');
		}
		return sb.toString();
	}

	// See: http://stackoverflow.com/a/9672376
	private Properties parsePropertiesString(String properties) {
		Properties p = null;
		try {
			p = new Properties();
			p.load(new StringReader(properties));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return p;
	}

	// Decode via URLDecoder.decode(payload, "UTF-8")
	private String readRequestPayload(HttpServletRequest request)
			throws IOException {
		return readString(request.getInputStream());
	}

	private String readFile(String fileName) throws IOException {
		return readString(getClass().getResourceAsStream(fileName));
	}

	private String readString(InputStream input, String encoding)
			throws IOException {
		return IOUtils.toString(input, encoding);
	}

	private String readString(InputStream input) throws IOException {
		return readString(input, "UTF-8");
	}

}
