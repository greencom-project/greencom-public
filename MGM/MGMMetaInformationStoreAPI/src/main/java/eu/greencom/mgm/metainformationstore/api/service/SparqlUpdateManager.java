package eu.greencom.mgm.metainformationstore.api.service;

import java.util.List;

import eu.greencom.mgm.metainformationstore.api.domain.ParametrizedSparqlUpdate;

/**
 * Service for management of SPARQL 1.1 UPADTE queries. These RW queries
 * manipulate the repository at graph or triple level.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlUpdateManager {

	/**
	 * Creates or updates a parametrized SPARQL UPDATE query. The implementation
	 * must parse and validate the query string and determine its validity.
	 * Throws an IllegalArgumentException if an invalid query was supplied.
	 * 
	 * @param name
	 * @param query
	 */
	void create(ParametrizedSparqlUpdate query);

	void remove(String name);

	ParametrizedSparqlUpdate get(String name);

	boolean exists(String name);

	/**
	 * Lists managed queries.
	 * 
	 * @return
	 */
	List<ParametrizedSparqlUpdate> list();

}
