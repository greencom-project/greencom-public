package eu.linksmart.logging;

import java.util.List;
import java.util.Properties;

/**
 * Service for dynamic Logging configuration.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface LoggingConfigurator {

	/**
	 * Retrieves an identifier list of all registered loggers. This may support
	 * the (interactive) set-up of logging configuration.
	 * 
	 * @return
	 */
	List<String> getLoggerList();

	/**
	 * Configures logging according to supplied (Log4J) configuration.
	 * 
	 * @param loggingProperties
	 */
	void configure(Properties loggingProperties);

	/**
	 * Retrieves effective logging configuration (Log4J compliant).
	 * 
	 * @return
	 */
	Properties getConfiguration();

}
