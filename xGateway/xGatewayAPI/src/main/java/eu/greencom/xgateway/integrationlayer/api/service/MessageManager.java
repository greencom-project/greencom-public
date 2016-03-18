package eu.greencom.xgateway.integrationlayer.api.service;

import java.util.List;


/**
 * Service managing asynchronous JSON-RPC request cycles and notifications. It
 * provides a central hub for reliable messaging. Implementations manage the
 * concrete life-cycle and retention of (stale) messages. They automatically
 * monitor and bind {@link MessageListener} services to be notified of any
 * messages according to the listener type. {@link CommunicationAdapter}s
 * utilize this service to publish responses to asynchronous requests.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface MessageManager {

	void put(String message);

	/**
	 * Indicates whether given request exists (is currently being processed).
	 * 
	 * @param id
	 * @return
	 */
	boolean hasRequest(String id);

	/**
	 * Retrieves a list of pending requests.
	 * 
	 * @param id
	 * @return
	 */
	List<String> listRequests();

	/**
	 * Indicates whether a response to given request exists.
	 * 
	 * @param id
	 * @return
	 */
	boolean hasResponse(String id);

	/**
	 * Retrieves a list of {@link RpcRestResponse}.
	 * 
	 * @param id
	 * @return
	 */
	List<String> listResponses();

	/**
	 * Retrieves a list of {@link RpcRestNotification}.
	 * 
	 * @param id
	 * @return
	 */
	List<String> listNotifications();

	/**
	 * Binds client-side message listeners.
	 * 
	 * @param listener
	 */
	void addListener(MessageListener listener);

	void removeListener(MessageListener listener);

}