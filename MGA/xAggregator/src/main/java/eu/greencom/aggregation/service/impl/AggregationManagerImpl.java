package eu.greencom.aggregation.service.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.aggregation.api.Aggregation;
import eu.greencom.aggregation.service.api.AggregationManager;
import eu.greencom.aggregation.service.api.ConfigurationManager;

public class AggregationManagerImpl implements AggregationManager {

	private static final Logger LOG = LoggerFactory
			.getLogger(AggregationManagerImpl.class.getName());

	private ConfigurationAdmin admin;

	protected void setConfigurationAdmin(ConfigurationAdmin admin) {
		this.admin = admin;
	}

	protected void removeConfigurationAdmin(ConfigurationAdmin admin) {
		this.admin = null;
	}

	protected void activate(ComponentContext c) {
		LOG.debug("{} activated", this.getClass().getName());
	}

	protected void deactivate(ComponentContext c) {
		LOG.debug("{} deactivated", this.getClass().getName());
	}

	@Override
	public Aggregation put(String id, Aggregation a) {
		// Retrieve configuration by external ID
		Configuration c = getConfiguration(id);
		if (c == null) {
			// Create new anonymous instance
			try {
				c = admin.getConfiguration(a.getAggregationType());
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		// Store external ID as part of Aggregation's data set
		a.put(Aggregation.PROPERTY_ID, id);
		a.put(ConfigurationManager.PROPERTY_CONFIGURATION_TYPE,
				Aggregation.class.getName());
		try {
			c.update((Dictionary) a);
			LOG.debug("Aggregation {} stored: {}", id, a);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return a;
	}

	@Override
	public Aggregation get(String id) {
		Aggregation result = null;
		Configuration c = getConfiguration(id);
		if (c != null) {
			// Configuration found via list() has non-null properties
			result = new Aggregation(c.getProperties());
		}
		return result;
	}

	@Override
	public boolean exists(String id) {
		return getConfiguration(id) != null;
	}

	@Override
	public Aggregation remove(String id) {
		Aggregation result = null;
		Configuration c = getConfiguration(id);
		if (c != null) {
			result = new Aggregation(c.getProperties());
			try {
				c.delete();
				LOG.debug("Aggregation {} successfully removed", id);
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		return result;
	}

	protected Configuration getConfiguration(String id) {
		Configuration result = null;
		String filter = "(" + Aggregation.PROPERTY_ID + "=" + id + ")";
		try {
			Configuration[] list = admin.listConfigurations(filter);
			if (list != null && list.length > 0) {
				result = list[0];
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	@Override
	public Set<String> keySet() {
		Set<String> result = null;
		// Search for any configuration pertaining to AggregatorFactory
		String filter = "(" + ConfigurationManager.PROPERTY_CONFIGURATION_TYPE
				+ "=" + Aggregation.class.getName() + ")";
		try {
			Configuration[] list = admin.listConfigurations(filter);
			if (list != null && list.length > 0) {
				result = new HashSet<String>();
				for (Configuration config : list) {
					// Extract ID property specified by the client
					String id = (String) config.getProperties().get(
							Aggregation.PROPERTY_ID);
					if (id != null && id.length() > 0)
						result.add(id);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	@Override
	public void setValue(String id, Object key, Object value) {
		Aggregation a = get(id);
		if (a != null) {
			a.put(key, value);
		}
		put(id, a);
	}

}
