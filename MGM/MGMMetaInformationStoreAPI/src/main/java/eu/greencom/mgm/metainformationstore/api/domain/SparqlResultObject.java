package eu.greencom.mgm.metainformationstore.api.domain;

/**
 * Representation of a SPARQL Query result.
 * 
 * @see http://www.w3.org/TR/sparql11-results-json/
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResultObject {

	private SparqlResultHead head;

	private SparqlResults results;

	public SparqlResultObject(SparqlResultHead head, SparqlResults results) {
		this.head = head;
		this.results = results;
	}

	public SparqlResultHead getHead() {
		return head;
	}

	public SparqlResults getResults() {
		return results;
	}

}
