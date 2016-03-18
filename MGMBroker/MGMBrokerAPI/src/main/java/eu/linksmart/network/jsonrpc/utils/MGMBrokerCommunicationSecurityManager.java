package eu.linksmart.network.jsonrpc.utils;

import java.util.ArrayList;
import java.util.List;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;
import eu.linksmart.security.communication.SecurityProtocol;

public class MGMBrokerCommunicationSecurityManager implements CommunicationSecurityManager {

	@Override
	public boolean canBroadcast() {
		return false;
	}

	@Override
	public SecurityProtocol getBroadcastSecurityProtocol(VirtualAddress clientAddress) {
		throw new UnsupportedOperationException("Broadcasting not supported by security protocol!");
	}

	@Override
	public List<SecurityProperty> getProperties() {
		List<SecurityProperty> properties = new ArrayList<SecurityProperty>();
		properties.add(SecurityProperty.Authenticity);
		properties.add(SecurityProperty.Confidentiality);
		properties.add(SecurityProperty.Integrity);
		properties.add(SecurityProperty.Symmetric);
		properties.add(SecurityProperty.Unicast);
		return properties;
	}

	@Override
	public SecurityProtocol getSecurityProtocol(VirtualAddress clientAddress, VirtualAddress serverAddress) {
		return new MGMBrokerSecurityProtocol(clientAddress, serverAddress, "password");
	}

}
