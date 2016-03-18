package eu.greencom.mgm.metainformationstore.api.service;

import java.util.Map;

/**
 * Service for execution of SPARQL 1.1 UPADTE queries. Because these RW queries
 * persistently alter the RDF repository the this service API was separated from
 * {@link SparqlQueryService} and its provision and execution should be limited
 * by further security mechanism.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlUpdateService {

	/**
	 * Generic method to invoke the supplied update query.
	 * 
	 * @param sparqlUpdate
	 */
	void executeUpdate(String sparqlUpdate);

	/**
	 * Invokes an update query parametrized with supplied parameter map.
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	void callUpdate(String name, Map<String, String> params);

}
