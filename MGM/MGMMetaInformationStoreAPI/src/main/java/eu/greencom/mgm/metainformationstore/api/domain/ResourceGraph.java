package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Graph representing immediate resource's predicates. Map of predicate URIs to
 * list of typed {@link PredicateValue}s.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class ResourceGraph extends HashMap<String, Set<PredicateValue>> {

	private static final long serialVersionUID = 3335975266020120959L;

	public void add(String key, PredicateValue value) {
		put(key, value, false);
	}

	public void set(String key, PredicateValue value) {
		put(key, value, true);
	}

	protected void put(String key, PredicateValue value, boolean replace) {
		Set<PredicateValue> values = get(key);
		if (values == null) {
			values = new HashSet<>();
			put(key, values);
		}
		if (replace){
			values.clear();
		}
		values.add(value);
	}

}
