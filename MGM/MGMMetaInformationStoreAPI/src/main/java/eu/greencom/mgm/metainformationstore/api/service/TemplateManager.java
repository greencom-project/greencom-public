package eu.greencom.mgm.metainformationstore.api.service;

import java.util.List;

import eu.greencom.mgm.metainformationstore.api.domain.GraphTemplate;

public interface TemplateManager {

	void create(GraphTemplate template);

	GraphTemplate get(String name);

	void remove(String name);

	boolean exists(String name);

	List<GraphTemplate> list();
}
