package eu.linksmart.network.jsonrpc.impl;

import org.apache.log4j.Logger;

import eu.linksmart.network.Registration;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

public abstract class LinkSmartServiceConsumer {
	private static final Logger LOG = Logger.getLogger(LinkSmartServiceConsumer.class.getName());

	protected NetworkManager networkManager;

	public void bindNetworkManager(NetworkManager networkManager) {
		LOG.info("Binding network manager");
		this.networkManager = networkManager;
	}

	public void unbindNetworkManager(NetworkManager networkManager) {// NOSONAR
																		// squid:S1172
																		// -
																		// JPU:
																		// Parameter
																		// required
																		// by
																		// API

	}

	protected Registration[] resolveService(Part[] serviceParts) {
		try {
			// Todo: it's a workaround! Service resolution by attribute does not
			// handle
			// properly concurrent queries in a distributed environment
			String description = "";
			for (int i = 0; i < serviceParts.length; i++) {
				if ("DESCRIPTION".equals(serviceParts[i].getKey())) {
					description = serviceParts[i].getValue();
				}
			}
			LOG.debug("Calling getServiceByDescription: \"" + description + "\"");
			Registration[] r = networkManager.getServiceByDescription(description);
			return r;

		} catch (Exception e) {
			LOG.error("Unable to resolve service! exception:" + e.getMessage(), e);
			return null;// NOSONAR squid:S1168 - JPU: Returns null by purpose
		}

	}
}
