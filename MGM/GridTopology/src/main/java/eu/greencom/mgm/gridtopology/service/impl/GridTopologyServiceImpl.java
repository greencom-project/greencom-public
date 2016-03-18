package eu.greencom.mgm.gridtopology.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.mgm.gridtopology.service.GridTopologyService;

/**
 * Simple implementation based on Sebastian's GeoJSON data of the grid on Fur.
 * 
 * @author pullmann
 *
 */
@Path("/GridTopologyService")
public class GridTopologyServiceImpl implements GridTopologyService {

	private static final Logger LOG = LoggerFactory.getLogger(GridTopologyService.class
			.getName());

	private JsonObject topology;

	public static final String PROPERTY_TOPOLOGY_FILE_REMOTE = "topology.file.remote";

	public static final String PROPERTY_TOPOLOGY_FILE_LOCAL = "topology.file.local";

	// TODO: check and optionally load Fur data from inline file
	protected void activate(Map params) {
		LOG.info("{} activated", this.getClass().getSimpleName());
		loadTopology(params);
	}

	protected void modified(Map params) {
		loadTopology(params);
	}

	protected void deactivate() {
		LOG.info("{} deactivated", this.getClass().getSimpleName());
	}

	private void loadTopology(Map params) {
		String location = (String) params.get(PROPERTY_TOPOLOGY_FILE_REMOTE);
		try {
			topology = loadTopology(new URL(location));
		} catch (Exception e) {
			LOG.error("Failed to load topology from URL {}, {}", location,e);
		}
		if (topology == null) {
			location = (String) params.get(PROPERTY_TOPOLOGY_FILE_LOCAL);
			try {
				topology = loadTopology(getClass().getResource(location));
			} catch (Exception e) {
				LOG.error("Failed to load topology from local file {}, {}",
						location,e);				
			}
		}
		if (topology != null) {
			LOG.info("Topology loaded successfully from {}", location);
		}
	}

	private JsonObject loadTopology(URL url) throws IOException {		
		InputStream is = url.openConnection().getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		JsonReader jsonReader = Json.createReader(br);
		JsonArray features = jsonReader.readObject().getJsonArray("features");
		JsonObjectBuilder ob = Json.createObjectBuilder();
		// Turn Features-array into a map keyed by Feature "id"
		int i = 0;
		while (i < features.size()) {
			JsonObject feature = features.getJsonObject(i);
			ob.add(feature.getString("id"), feature);
			i++;
		}
		return ob.build();
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{startResource}/{resourceType}")
	public List<String> getResourcesByType(
			@PathParam("startResource") String startResource,
			// Resolve subresources of any type per default
			@PathParam("resourceType") @DefaultValue(TYPE_POWER_SYSTEM_RESOURCE) String resourceType) {

		Set<String> result = new HashSet<String>();
		findResourcesByType(startResource, resourceType, result);
		return new ArrayList(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(TYPE_POWER_TRANSFORMER)
	public List<String> getMicrogrids() {
		return getResourcesByType(null, TYPE_POWER_TRANSFORMER);
	}

	@Override
	public List<String> getEnergyConsumers(String startResource) {
		return getResourcesByType(startResource, TYPE_ENERGY_CONSUMER);
	}

	@Override
	public List<String> getEnergySources(String startResource) {
		return getResourcesByType(startResource, TYPE_ENERGY_SOURCE);
	}

	// Recursively resolves resources of given type
	public void findResourcesByType(String startResource, String resourceType,
			Set<String> found) {

		if (startResource == null) {
			// Resolve transformers
			if (TYPE_POWER_TRANSFORMER.equals(resourceType)) {
				List<String> transformers = getTransformers();
				if (transformers != null) {
					found.addAll(transformers);
				}
			}
			// else can't search for non-grid resources missing a start node
			return;
		}

		String startType = getResourceType(startResource);

		if (startType == null) {
			LOG.info("No resource type found for {}", startResource);
			return;
		}

		// Add current start resource, if appropriate or any resource requested
		if (resourceType.equals(startType)
				|| resourceType.equals(TYPE_POWER_SYSTEM_RESOURCE)) {
			found.add(startResource);
		}

		switch (startType) {
		// "transformer" ~ microgrid
		case TYPE_POWER_TRANSFORMER://NOSONAR squid:S1151 - JPU can't really avoid this number of cases 
			// Stop if looking for microgrids, there are not embedded
			if (!resourceType.equals(TYPE_POWER_TRANSFORMER)) {
				List<String> lines = getLines(startResource);
				if (lines != null) {
					for (String line : lines) {
						findResourcesByType(line, resourceType, found);
					}
				}
			}
			break;
		// "radial"
		case TYPE_CONDUCTOR://NOSONAR squid:S1151 
			if (!resourceType.equals(TYPE_CONDUCTOR)) {
				List<String> houses = getHouses(startResource);
				if (houses != null) {
					for (String house : houses) {
						found.add(house);
					}
				}
			}
			break;
		// "house"
		case TYPE_ENERGY_CONSUMER:
		case TYPE_ENERGY_SOURCE:
			// Terminal node, end here
			break;
		default:
			break;
		}

	}

	// Retrieves the type of GeoJSON Feature and maps to CIM
	protected String getResourceType(String id) {

		JsonObject feature = topology.getJsonObject(id);
		// May be unknown
		if (feature == null){
			throw new NotFoundException();
		}

		String type = feature.getJsonObject("properties").getString("Type");

		if (type == null){
			return null;
		}

		// Map type literals used in GeoJSON to assumed CIM types:
		switch (type) {
		case "transformer":
			return TYPE_POWER_TRANSFORMER;
		case "radial":
			return TYPE_CONDUCTOR;
			// TODO: Distinguish consumer and source at model level!
		case "house":
			return TYPE_ENERGY_CONSUMER;
		default:
			return TYPE_POWER_SYSTEM_RESOURCE;
		}
	}

	// Retrieves transformer nodes (microgrids)
	protected List<String> getTransformers() {

		List<String> transformers = new LinkedList<String>();

		for (String id : topology.keySet()) {
			JsonObject feature = topology.getJsonObject(id);
			String type = feature.getJsonObject("properties").getString("Type");
			if ("transformer".equals(type)) {
				transformers.add(id);
			}
		}
		LOG.info("Retrieved transformers {}", transformers);
		return transformers;
	}

	// Queries the lines attached to specified transformer. Currently no need to
	// assemble lines, since they comprise entire radial.
	protected List<String> getLines(String transformerId) {
		List<String> lines = null;
		JsonObject transformer = topology.getJsonObject(transformerId);
		JsonArray ls = transformer.getJsonObject("properties").getJsonArray(
				"SingleLines");
		if (ls != null && !ls.isEmpty()) {
			lines = new ArrayList<String>();
			// Can't cast to JsonString, extract string value, will return "xx"
			// otherwise
			for (JsonValue line : ls) {
				lines.add(((JsonString) line).getString());
			}
		}
		LOG.info("Retrieved lines {} for transformer {}", lines, transformerId);
		return lines;
	}

	// Retrieves terminal nodes (currently houses/consumers) attached to a line.
	protected List<String> getHouses(String radial) {
		List<String> houses = null;
		JsonObject transformer = topology.getJsonObject(radial);
		JsonArray hs = transformer.getJsonObject("properties").getJsonArray(
				"Houses");
		if (hs != null && !hs.isEmpty()) {
			houses = new ArrayList<String>();
			for (JsonValue line : hs) {
				houses.add(((JsonString) line).getString());
			}
		}
		LOG.info("Retrieved houses {} for radial {}", houses, radial);
		return houses;
	}

}
