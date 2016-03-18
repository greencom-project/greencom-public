package eu.greencom.mgm.metainformationstore.api.service;

import java.util.List;
import java.util.Map;

import eu.greencom.mgm.metainformationstore.api.domain.Graph;
import eu.greencom.mgm.metainformationstore.api.domain.PredicateValue;
import eu.greencom.mgm.metainformationstore.api.domain.ResourceGraph;

/**
 * Service managing the life-cycle of RDF resources.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface ResourceManager {

	/**
	 * Resolves internal resource CURIE for a resource identified by given
	 * external identifier (foreign key).
	 * 
	 * @param externalIdentifier
	 * @return CURIE of the resolved resource or null, if no resource matched.
	 */
	String resolveExternalIdentifier(String externalIdentifier);

	/**
	 * Creates an optionally typed resource. For any known type CURIE a type is
	 * associated with the resource.
	 * 
	 * @param typeCuries
	 *            Optional list of resource types.
	 * @return CURIE of the created resource.
	 */
	String create(String... typeCuries);

	/**
	 * Lists CURIEs of instances of given resource type;
	 * 
	 * @param typeCurie
	 * @return (Possibly empty) CURIE list of resource type instances or null,
	 *         if type undefined.
	 */
	List<String> list(String typeCurie);

	/**
	 * Retrieves a graph representation of specified resource.
	 * 
	 * @param resourceCurie
	 * @return
	 */
	ResourceGraph get(String resourceCurie);

	/**
	 * Retrieves CURIEs of resources matching any of the predicate values (OR).
	 * 
	 * @param predicateCurie
	 *            CURIE of the property to filter on
	 * @param filter
	 *            {@link PredicateValue} to compare on filtered property
	 * @return
	 */
	List<String> find(String predicateCurie, PredicateValue... filter);

	boolean exists(String resourceCurie);

	/**
	 * Removes specified resource, i.e. all incoming references to the resource
	 * and any outgoing arcs (property or relation) are deleted.
	 * 
	 * @param resourceCurie
	 */
	void remove(String resourceCurie);

	/**
	 * Assigns a new {@link PredicateValue} to given resources's predicate. If
	 * overwrite is true any existing value is replaced, otherwise a new value
	 * mapping will be added.
	 * 
	 * @param resourceCurie
	 * @param predicateCurie
	 * @param value
	 *            {@link PredicateValue} holding a URI/CURIE or typed literal.
	 */
	void set(String resourceCurie, String predicateCurie, PredicateValue value,
			boolean overwrite);

	/**
	 * Stores given representation(s) of the resource by replacing any previous
	 * state. A resource URI is created and returned for any temporary URI.
	 * 
	 * @param graph
	 * @return mapping of temporal to permanent resource URIs.
	 */
	Map<String, String> store(Graph graph);

}
