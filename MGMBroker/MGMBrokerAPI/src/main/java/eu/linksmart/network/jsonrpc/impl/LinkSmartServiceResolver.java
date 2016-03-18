package eu.linksmart.network.jsonrpc.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;

public class LinkSmartServiceResolver {

	private NetworkManager networkManager;

	private static final Logger LOG = Logger.getLogger(LinkSmartServiceResolver.class);

	public LinkSmartServiceResolver(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	/**
	 * Method resolving one LinkSmart service by its description, return the
	 * VirtualAddress of the service if the resolution was successful, null
	 * otherwise
	 * 
	 * @param description
	 * @return
	 */
	public VirtualAddress resolveServiceByDescription(String description) {

		LOG.debug("Resolving services with description:" + description);

		Registration[] r = null;
		try {
			r = networkManager.getServiceByDescription(description);
		} catch (RemoteException e) {
			LOG.error("Error resolving service with description\"" + description + "\"", e);
		}

		if (r == null || r.length == 0) {
			LOG.error("Could not resolve any service with description \"" + description + "\"");
			return null;
		}

		if (r.length > 1) {
			LOG.warn("Found more than 1 service with description \"" + description + "\", using first");
		}

		return r[0].getVirtualAddress();
	}

}
