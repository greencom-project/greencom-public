package eu.linksmart.network.jsonrpc.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import eu.linksmart.network.Registration;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

public abstract class LinkSmartService {

	private static final Logger LOG = Logger.getLogger(LinkSmartService.class.getName()); 

	protected NetworkManager networkManager;

	protected Registration myVirtualAddress;
	protected String backbone;

	public void bindNetworkManager(NetworkManager networkManager) {
		LOG.info("Binding network manager");
		this.networkManager = networkManager;
	}

	public void unbindNetworkManager(NetworkManager networkManager) {//NOSONAR squid:S1172 - JPU: Required by SCR API
		this.networkManager = null;
	}

	protected void initBackbone(String backboneName) {
		try {
			String[] backbones = networkManager.getAvailableBackbones();
			this.backbone = null;
			for (String b : backbones) {
				LOG.info("BACKBONE: " + b);
				if (b.contains(backboneName)) {
					this.backbone = b;
				}
			}
			if (backbone == null) {
				LOG.error(backboneName + " not found.. forcing");
				backbone = backboneName;
			}

		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected boolean registerLinkSmartService(Part[] servicePart, String serviceName) {
		try {
			LOG.info("REGISTERING WITH BACKBONE:" + backbone);
			myVirtualAddress = networkManager.registerService(servicePart, serviceName, backbone);
			LOG.debug("#########################################");
			LOG.debug("Registering LinkSmart service with parts:");
			for (int i = 0; i < servicePart.length; i++) {
				LOG.debug(servicePart[i].getKey() + ": " + servicePart[i].getValue());
			}
			LOG.debug("#########################################");
			if (myVirtualAddress == null) {
				LOG.error("#########################################");
				LOG.error("ERROR: could not register service " + serviceName + " on backbone " + backbone);
				LOG.error("#########################################");
				return false;
			} else {
				LOG.info("#########################################");
				LOG.info("Created VirtualAddress: " + myVirtualAddress.getVirtualAddressAsString());
				LOG.info("#########################################");
				return true;
			}

		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	protected void unregisterLinkSmartService() {
		if (myVirtualAddress != null) {
			try {
				networkManager.removeService(myVirtualAddress.getVirtualAddress());
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
