package eu.greencom.xgateway.integrationlayer.api.service;

/**
 * Adapter for bidirectional, asynchronous communication with an WSAN endpoint
 * via JSON-RPC messages. Arbitrary clients may use to send messages to remote
 * endpoint and/or register themselves to receive remote messages.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface CommunicationAdapter {

	/**
	 * Posts a JSON-RPC message (notification, request, response) to the remote
	 * endpoint in a non-blocking manner.
	 * 
	 * @param request
	 */
	void send(String message);

	/**
	 * Assigns a {@link MessageListener} to which received messages
	 * (asynchronous responses or remote WSAN requests/notifications) are
	 * passed.
	 * 
	 * @param listener
	 */
	void addMessageListener(MessageListener listener);

	void removeMessageListener(MessageListener listener);

	/**
	 * Assigns a {@link MessageListener} to handle the specified response ID.
	 * Upon invocation the listener will automatically be unregistered by a call
	 * to {@link #removeMessageListener(MessageListener)}.
	 * 
	 * @param listener
	 */
	void setMessageHandler(MessageListener listener, String id);

}
