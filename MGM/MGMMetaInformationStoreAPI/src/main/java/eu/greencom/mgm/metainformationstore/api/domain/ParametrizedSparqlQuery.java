package eu.greencom.mgm.metainformationstore.api.domain;


/**
 * Self-describing parametrized SPARQL Query.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class ParametrizedSparqlQuery {

	private QueryType queryType = null;

	private String name;

	private String label;

	private String query;

	private String description;

	private String namespaces;
	
	private String[] queryVariables;

	private String[] parameters;

	public static enum QueryType {
		// DESCRIBE?
		SELECT, CONSTRUCT, ASK
	}

	// Required by GSON serializer
	public ParametrizedSparqlQuery() {		
	}
	
	public ParametrizedSparqlQuery(String name, String query) {
		this.name = name;
		this.query = query;
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

	public QueryType getQueryType() {
		return queryType;
	}

	// GSON will silently fall and prevent setting unsupported values
	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
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

	public String[] getQueryVariables() {
		return queryVariables;//NOSONAR findbugs:EI_EXPOSE_REP - JPU: Pure data transfer object, not security relevant
	}

	public void setQueryVariables(String... queryVariables) {
		this.queryVariables = queryVariables;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getParameters() {
		return parameters;//NOSONAR findbugs:EI_EXPOSE_REP - JPU: Pure data transfer object, not security relevant
	}

	public void setParameters(String... parameters) {
		this.parameters = parameters;
	}

}
