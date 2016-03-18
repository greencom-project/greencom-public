package eu.linksmart.logging.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.linksmart.logging.LoggingConfigurator;

/**
 * Component supporting dynamic PAX Logging configuration. It first tests for
 * its existence and initializes it accordingly, if not present. By default the
 * initial configuration is supplied by a fragment of this bundle, otherwise
 * hard-coded configuration is used. Any updates to the configuration are
 * performed by calls to {@link #configure(Properties)} method.
 * 
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class LoggingConfiguratorImpl implements LoggingConfigurator {

	private static final Logger LOG = LoggerFactory
			.getLogger(LoggingConfiguratorImpl.class.getName());

	// Constant PAX Logging configuration PID
	public static final String CONFIG_PID = "org.ops4j.pax.logging";

	private ConfigurationAdmin configurationAdmin = null;

	protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	protected void removeConfigurationAdmin(
			ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = null;
	}

	protected void activate(ComponentContext context) {
		try {
			Properties p = getConfiguration();
			// Initialize if there is no configuration
			if (p == null || p.isEmpty()) {
				// Load supplied fragment configuration
				p = readConfigurationFile();
				if (p == null) {
					// Use static configuration
					p = getStaticConfiguration();
				}
				configure(p);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void deactivate(ComponentContext context) {
	}

	@Override
	public Properties getConfiguration() {
		Configuration c = null;
		Properties p = null;
		try {
			c = configurationAdmin.getConfiguration(CONFIG_PID, null);
			p = fromConfigDictionary(c.getProperties());
			// Remove PID as part of log configuration
			if (p != null) {
				p.remove("service.pid");
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return p;
	}

	// To prevent ClassCastException:
	// equinox.internal.cm.ConfigurationDictionary to java.util.Properties
	private Properties fromConfigDictionary(Dictionary<String, Object> d) {
		if (d == null)
			return null;
		Properties p = new Properties();
		Enumeration<String> keys = d.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			p.put(key, d.get(key));
		}
		return p;
	}

	@Override
	public void configure(Properties properties) {
		try {
			Configuration c = configurationAdmin.getConfiguration(CONFIG_PID,
					null);
			// Need to convert Properties to Dictionary<String, Object>
			Dictionary<String, Object> d = new Hashtable<String, Object>();
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				d.put(key, properties.get(key));
			}
			c.update(d);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getLoggerList() {
		/*
		 * TODO: Iterate log back-ends used by PAX and request registered
		 * loggers
		 */
		throw new UnsupportedOperationException(
				"Listing of registered loggers is not implemented yet");
	}

	private Properties readConfigurationFile() {
		Properties p = new Properties();
		try {
			p.load(getClass().getResourceAsStream("/log4j.properties"));// parsePropertiesString(readFile("/log4j.properties"));
			LOG.debug("Loaded logging configuration from fragment bundle");
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return p;
	}

	private Properties getStaticConfiguration() {
		Properties p = new Properties();
		p.put("log4j.rootLogger", "DEBUG, CONSOLE");
		p.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
		p.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
		p.put("log4j.appender.CONSOLE.layout.ConversionPattern",
				"%d %-5p (%C#%M:%L) - %m%n");
		return p;
	}

	// Console command
	public void set(String... args) {
		String property = null;
		String value = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// Expects a single string "property = value"
		if (args.length == 1 && args[0] != null && args[0].contains("=")) {
			String[] s = args[0].split("=");
			property = s[0].trim();
			value = s[1].trim();
		}
		if (property != null && value != null) {
			Properties p = getConfiguration();
			p.setProperty(property, value);
			System.out.println("Setting logger property '" + property
					+ "' to '" + value + "'");
			configure(p);
		} else
			System.err
					.println("Could not set logger property '"
							+ property
							+ "' to '"
							+ value
							+ "'! Usage example:\nlogger:set \"log4j.rootLogger = DEBUG, CONSOLE\"");
	}
}
