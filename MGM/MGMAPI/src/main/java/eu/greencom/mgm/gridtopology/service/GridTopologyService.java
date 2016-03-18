package eu.greencom.mgm.gridtopology.service;

import java.util.List;

/**
 * Utility service for querying the low-voltage microgrid. Instead of
 * duplicating the CIM structure it leverages plain identifiers in order to
 * refer to grid components.
 * 
 * @author pullmann
 *
 */
public interface GridTopologyService {

	public static final String TYPE_POWER_SYSTEM_RESOURCE = "cim:PowerSystemResource";

	public static final String TYPE_POWER_TRANSFORMER = "cim:PowerTransformer";

	public static final String TYPE_CONDUCTOR = "cim:Conductor";

	public static final String TYPE_ENERGY_CONSUMER = "cim:EnergyConsumer";

	public static final String TYPE_ENERGY_SOURCE = "cim:EnergySource";

	/**
	 * Retrieves a list of power system resources of given type via a recursive
	 * search starting at the indicated resource. The implementation takes care
	 * of component traversal according to resource type and a correct
	 * termination. The following resource type identifiers are supported:
	 * <ul>
	 * <li>cim:PowerSystemResource - Any type of component.</li>
	 * <li>cim:PowerTransformer - Class of (4k) transformers/microgrids.</li>
	 * <li>cim:Conductor - Superclass of AC/DC power lines.</li>
	 * <li>cim:EnergyConsumer - Class of energy customers (households).</li>
	 * <li>cim:EnergySource - Class of energy sources (PV or wind).</li>
	 * </ul>
	 * 
	 * @param startResource
	 * @param resourceType
	 * @return Power system resources of specified type connected to the given
	 *         start resource.
	 */
	List<String> getResourcesByType(String startResource, String resourceType);

	/**
	 * Retrieves a list of known microgrids (currently transformers).
	 * 
	 * @return
	 */
	List<String> getMicrogrids();

	List<String> getEnergyConsumers(String startResource);

	List<String> getEnergySources(String startResource);

}
