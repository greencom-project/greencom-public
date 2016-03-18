package eu.linksmart.network.jsonrpc.api;

import org.osgi.service.event.EventAdmin;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;

/**
 * This interface describes methods to be implemented by a message manager.
 * Implementation of this interface must handle the message exchange together
 * with OSGi mapping and messages cache management
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public interface JsonRpcMessageManager {
	public void receiveMessage(JsonRpcMessage message, EventAdmin eventAdmin);

	public String sendMessage(VirtualAddress address, NetworkManager networkManager, JsonRpcMessage message,
			JsonRpcMessageHandler handler);
}
