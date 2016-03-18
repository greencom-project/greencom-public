package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Singular head object of a SPARQL Query result.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResultHead {

	private List<String> vars = null;

	// Binding variables in order of appearance.
	public List<String> getVars() {
		return vars;
	}

	public SparqlResultHead(List<String> vars) {
		this.vars = vars;
	}

	public SparqlResultHead(String... vars) {
		if (vars != null && vars.length > 0){
			this.vars = Arrays.asList(vars);
		}
	}

}
