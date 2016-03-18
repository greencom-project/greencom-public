package eu.greencom.mgm.metainformationstore.api.service;

import java.util.Map;

import eu.greencom.mgm.metainformationstore.api.domain.Graph;
import eu.greencom.mgm.metainformationstore.api.domain.SparqlResultObject;

/**
 * Service for execution of supplied ad-hoc queries and calling persistent,
 * parametrized queries against an implicit data store (configuration handled by
 * the implementation). The queries will be optionally augmented by prefix
 * definitions, when needed.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlQueryService {

	/**
	 * Generic method to invoke a supplied query regardless of its type. The
	 * client has to inspect and cast the result object accordingly.
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	Object executeQuery(String sparqlQuery);

	/**
	 * Generic method to invoke a query regardless of its type. The client has
	 * to inspect and cast the result object accordingly.
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	Object callQuery(String name, Map<String, String> params);

	/**
	 * Invocation of a SPARQL SELECT query. The resultant JSON structure is
	 * serialized according to the W3C SPARQL 1.1 Query Results JSON Format.
	 * 
	 * @see http://www.w3.org/TR/sparql11-query/#select
	 * @param name
	 * @param params
	 * @return JSON result structure.
	 */
	SparqlResultObject callSelectQuery(String name, Map<String, String> params);

	/**
	 * Invocation of a SPARQL CONSTRUCT query resulting in new graph serialized
	 * according to RDF/JSON format
	 * (http://jena.apache.org/documentation/io/rdf-json.html).
	 * 
	 * 
	 * @see http://www.w3.org/TR/sparql11-query/#construct
	 * @param name
	 * @param params
	 * @return
	 */
	Graph callConstructQuery(String name, Map<String, String> params);

	/**
	 * Invocation of a SPARQL ASK query resulting in a boolean value.
	 * 
	 * @see
	 * 
	 * @param name
	 *            Unique name of the query.
	 * @param params
	 *            Map of required parameter-value pairs.
	 * @return true or false, depending whether the pattern matches.
	 */
	Boolean callAskQuery(String name, Map<String, String> params);

}
