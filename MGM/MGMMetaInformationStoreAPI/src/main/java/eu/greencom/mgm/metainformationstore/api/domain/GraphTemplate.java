package eu.greencom.mgm.metainformationstore.api.domain;

/**
 * Container for a template graph and its meta-data. A template graph is a
 * static, parametrizable description of a domain aspect that uses static
 * temporary identifiers. Its purpose is to capture the main structure while
 * allowing for iterative refinement and concretization.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class GraphTemplate {

	private String name;

	private String label;

	private String description;

	private Graph template;

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Graph getTemplate() {
		return template;
	}

	public void setTemplate(Graph template) {
		this.template = template;
	}

}
