package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Singular result of a SPARQL Query comprising a list of variable bindings.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResults {

	List<SparqlResultBinding> bindings = new LinkedList<SparqlResultBinding>();

	public List<SparqlResultBinding> getBindings() {
		return bindings;
	}

}
