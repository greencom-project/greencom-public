package eu.greencom.aggregation.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.aggregation.service.api.AggregationSourceResolver;
import eu.greencom.mgm.gridtopology.service.GridTopologyService;
import eu.greencom.mgm.webapiconsumer.api.service.SensorManager;

/**
 * An {@link AggregationSourceResolver} that uses {@link GridTopologyService} to
 * resolve a microgrid ID to a list of cumulative smart meters IDs.
 * 
 * @author pullmann
 *
 */
// @Path("/RadialResolver")
public class GridResolver  implements AggregationSourceResolver {

	protected SensorManager sensorManager;

	protected GridTopologyService topologyService;

	// Radial to sensor mapping
	protected Map<String, List<String>> sensorsPerRadial = new HashMap<String, List<String>>();

	// House01:85f4d922-d876-47cf-bdc2-547924640bb5 - Sensors resolved via DW
	// protected Map<String, String> houseToGatewayMap = new HashMap<String,
	// String>();

	// Map of HouseID to cumulative smart meter ID: House01:2F15BC001B020E48 -
	// No resolution house -> sensor needed
	protected Map<String, String> houseToMeterMap = new HashMap<String, String>();

	protected static final Logger LOG = LoggerFactory
			.getLogger(AggregationSourceResolver.class.getName());

	//
	// protected void setSensorManager(SensorManager sensorManager) {
	// this.sensorManager = sensorManager;
	// }
	//
	// protected void removeSensorManager(SensorManager sensorManager) {
	// this.sensorManager = null;
	// }

	protected void setTopologyService(GridTopologyService topologyService) {
		this.topologyService = topologyService;
	}

	protected void removeTopologyService(GridTopologyService topologyService) {
		this.topologyService = null;
	}

	
	protected void activate(Map<String, Object> properties) {
		modified(properties);
		// LOG.debug("Sources of radial 20291 {}",getSources("20291"));
		LOG.debug("{} activated", this.getClass().getName());
	}

	protected void modified(Map<String, Object> properties) {

		// String gwMap = (String) properties.get("house_gateway_mapping");
		// houseToGatewayMap.putAll(parseConfigValues(gwMap));
		// LOG.debug("Loded house to gateway mapping: {}", houseToGatewayMap);

		String meterMap = (String) properties.get("house_meter_mapping");
		houseToMeterMap.putAll(AbstractAggregator.parseConfigValues(meterMap));
		LOG.debug("Loded house to smart meter mapping: {}", houseToMeterMap);
		LOG.debug("{} configuration loaded", this.getClass().getName());
	}

	protected void deactivate(Map<String, String> properties) {
		LOG.debug("{} deactivated", this.getClass().getName());
	}

	private void resolveSources(String radialId) {
		// Resolves to HouseXX IDs per radial
		List<String> houses = topologyService.getEnergyConsumers(radialId);
		if (houses != null) {
			List<String> sensors = new LinkedList<String>();
			for (String house : houses) {
				// Make house_house9 look "House09"
				house = house.replace("house_h", "H").replaceAll(
						"House([1-9])$", "House0$1");
				// String gateway = houseToGatewayMap.get(house);
				// Resolve smart meter ID of this gateway
				// List<String> gwSensors =
				// sensorManager.getSensorsByType(gateway,
				// SensorManager.TYPE_CUMULATIVE_SMART_METER);
				// if (gwSensors != null)
				// sensors.addAll(gwSensors);
				String cumulativeSmartMeter = houseToMeterMap.get(house);
				if (cumulativeSmartMeter != null) {
					sensors.add(cumulativeSmartMeter);
				} else {
					LOG.warn("No cumulative sensor ID found for house {}",
							house);
				}
			}
			if (sensors.size() > 0) {
				sensorsPerRadial.put(radialId, sensors);
			}
		}
	}

	@Override
	public List<String> getSources(String radialId) {
		if (!sensorsPerRadial.containsKey(radialId)) {
			resolveSources(radialId);
		}
		return sensorsPerRadial.get(radialId);
	}

	/*
	 * JAX-RS version throwing 404 exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{radial}")
	public List<String> doGetSources(@PathParam("radial") String radialId) {
		List<String> result = getSources(radialId);
		LOG.debug("Resolved sensors {} of radial {}", result, radialId);
		if (result == null) {
			throw new WebApplicationException(404);
		}
		return result;
	}

}
