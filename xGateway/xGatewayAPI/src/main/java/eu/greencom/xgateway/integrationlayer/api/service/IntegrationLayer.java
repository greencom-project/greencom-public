package eu.greencom.xgateway.integrationlayer.api.service;

import eu.greencom.mgm.metainformationstore.api.domain.Graph;

/**
 * Functional interface of an adapter mediating a WSAN. Underlying message
 * exchange capabilities are defined by {@link CommunicationAdapter} interface.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface IntegrationLayer extends CommunicationAdapter {

	/**
	 * Indicate availability of the gateway sending a registration request.
	 */
	void register();

	/**
	 * Indicate absence (shut-down) of the gateway. WSAN may cache last n
	 * values...
	 */
	void unregister();

	/**
	 * Indicates whether this adapter is registered with WSAN (i.e. an response
	 * to it registration request has arrived).
	 * 
	 * @return
	 */
	boolean isRegistered();

	/**
	 * Request to restart the coordinator.
	 */
	void restart();

	/**
	 * Sends out a JSON-RPC message to retrieve the device list from gateway.
	 */
	void requestDeviceData();

	/**
	 * Updates the device description by the provided data.
	 */
	void updateDeviceData(Graph updatedGraph);

	/**
	 * Retrieves recent device description.
	 * 
	 * @return JSON string formatted according to IL API specification.
	 */
	Graph getDeviceData();

	/**
	 * Retrieves the most recent list of sensor observations.
	 * 
	 * @return JSON string formatted according to IL API specification.
	 */
	Graph getObservationData();

}
