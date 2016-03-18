package eu.greencom.aggregation.service.api;

import java.util.Set;

import org.osgi.service.cm.ConfigurationAdmin;

import eu.greencom.aggregation.api.Aggregation;

/**
 * Implements CRUD operations on persistent {@link Aggregation} rules via OSGi
 * {@link ConfigurationAdmin} service.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface AggregationManager extends ConfigurationManager<Aggregation> {

	/**
	 * Stores an {@link Aggregation} using specified ID by potentially updating
	 * an existent configuration.
	 * 
	 * @param id
	 * @param aggregation
	 */
	@Override
	Aggregation put(String id, Aggregation aggregation);

	/**
	 * Retrieves an {@link Aggregation} configuration by specified ID.
	 * 
	 * @param id
	 * @return
	 */
	@Override
	Aggregation get(String id);

	/**
	 * Tests whether an {@link Aggregation} of given ID exists.
	 * 
	 * @param id
	 * @return
	 */
	@Override
	boolean exists(String id);

	/**
	 * Permanently removes the specified {@link Aggregation}.
	 * 
	 * @param id
	 */
	@Override
	Aggregation remove(String id);

	/**
	 * Retrieves a set of managed {@link Aggregation} IDs.
	 * 
	 * @return
	 */
	@Override
	Set<String> keySet();

}
