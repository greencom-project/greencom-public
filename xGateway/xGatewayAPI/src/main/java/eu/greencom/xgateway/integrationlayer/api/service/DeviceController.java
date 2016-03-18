package eu.greencom.xgateway.integrationlayer.api.service;

/**
 * Service for accessing and setting properties of devices. Properties are
 * identified by conventional compact URIs defined in GreenCom ontology. The
 * method calls will be translated to JSON-RPC commands exchanged with the
 * coordinator mote.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface DeviceController {

	/**
	 * Reads the specified property from the device representation.
	 * 
	 * @param deviceId
	 * @param propertyId
	 * @return
	 */
	String get(String deviceId, String propertyId);

	/**
	 * Sets the specified property on the device representation.
	 * 
	 * @param deviceId
	 * @param propertyId
	 * @param value
	 * @return
	 */
	boolean put(String deviceId, String propertyId, String value);

}
