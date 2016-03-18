package eu.greencom.mgm.metainformationstore.api.service;

import java.util.List;

import eu.greencom.mgm.metainformationstore.api.domain.ParametrizedSparqlQuery;

/**
 * Service for management of SPARQL 1.1 data retrieval queries. These are
 * read-only as opposed to the read-write update queries.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlQueryManager {

	/**
	 * Creates or updates a parametrized SPARQL query. The implementation must
	 * parse and validate the query string and determine the validity, query
	 * type, parameter names etc.). Throws an IllegalArgumentException if an
	 * invalid query was supplied.
	 * 
	 * @param name
	 * @param query
	 */
	void create(ParametrizedSparqlQuery query);

	void remove(String name);

	ParametrizedSparqlQuery get(String name);
	
	boolean exists(String name);

	/**
	 * Lists managed queries.
	 * 
	 * @return
	 */
	List<ParametrizedSparqlQuery> list();
}
