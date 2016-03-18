package eu.greencom.mgm.metainformationstore.api.domain;

/**
 * Self-describing parametrized SPARQL 1.1. UPDATE Query. These comprise graph
 * updates (triple management) and bulk operation on graphs (graph management).
 * The SPARQL 1.1. Protocol prescribes the usage of HTTP status to indicate
 * execution success of update queries which are implicitly void - there is no
 * result structure (variables) defined here.
 * 
 * @see http://www.w3.org/TR/sparql11-update/
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class ParametrizedSparqlUpdate {

	private String name;

	private String label;

	private String update;

	private String description;

	private String namespaces;

	private String[] parameters;

	// required by GSON serializer
	public ParametrizedSparqlUpdate() {		
	}
	
	public ParametrizedSparqlUpdate(String name, String update) {
		this.name = name;
		this.update = update;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// Read-only: key
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String namespaces) {
		this.namespaces = namespaces;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String[] getParameters() {
		return parameters;//NOSONAR findbugs:EI_EXPOSE_REP - JPU: Pure data transfer object, not security relevant
	}

	public void setParameters(String... parameters) {
		this.parameters = parameters;
	}

}
