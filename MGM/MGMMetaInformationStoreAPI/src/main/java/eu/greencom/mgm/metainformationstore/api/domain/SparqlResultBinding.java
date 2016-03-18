package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.HashMap;

/**
 * Mapping of variable name to {@link PredicateValue}s. In contrast to
 * {@link ResourceGraph} the keys represent names of bound query variables and
 * not predicate CURIEs.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResultBinding extends HashMap<String, PredicateValue> {

	private static final long serialVersionUID = 6511730844979028400L;

}
