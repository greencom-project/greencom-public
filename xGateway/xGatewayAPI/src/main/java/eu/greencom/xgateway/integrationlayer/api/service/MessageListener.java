package eu.greencom.xgateway.integrationlayer.api.service;

/**
 * Generic listener for any JSON-RPC message type (notification, request or
 * response). {@link #receive(String)} must return immediately.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface MessageListener {

	void receive(String message);

}