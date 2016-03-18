package eu.greencom.aggregation.service.api;

import java.util.List;

import eu.greencom.mgm.gridtopology.service.GridTopologyService;
import eu.greencom.mgm.webapiconsumer.api.service.SensorManager;

/**
 * Enumerates identifiers of aggregation sources (sensors) within the specified
 * context (radial). This component pairs the {@link GridTopologyService} aware
 * of the grid structure up to the level of houses and the {@link SensorManager}
 * to resolve identifiers of energy meters for given homes.
 * 
 * @author pullmann
 *
 */
public interface AggregationSourceResolver {

	List<String> getSources(String topologyContextId);

}
