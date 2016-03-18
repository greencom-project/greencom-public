package eu.linksmart.network.jsonrpc.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.data.DataEndpoint;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

/**
 * Generic class representing a LinkSmart EndPoint exposed through data
 * backbone. Received messages are converted into class of type specified by the
 * parameter type using Jackson ObjectMapper. Once converted, an instance of
 * type is passed to a callback
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 * @param <T>
 *            The type the received message should be converted to.
 */
public class LinkSmartDataServiceProxy extends LinkSmartService implements DataEndpoint {

	private static final Logger LOG = Logger.getLogger(LinkSmartDataServiceProxy.class.getName());

	private ComponentContext context;
	private ObjectMapper objectMapper;
	private Part[] serviceParts;

	private String serviceName;
	private ServiceRegistration registration;
	private LinkSmartDataServiceCallback callback;

	private Class type;

	/**
	 * Constructor of LinkSmartDataServiceProxy, creates the object ready to be
	 * used as LinkSmart service over data backbone
	 * 
	 * @param context
	 *            The context of the component which is creating the service,
	 *            needed to register local OSGi services for endpoints
	 * @param objectMapper
	 *            An ObjectMapper from Jackson JSON library, properly configure
	 *            to unmarshal received messages into an object of the type @type
	 * @param serviceName
	 *            The _UNIQUE_ name of the service which is going to be created
	 * @param networkManager
	 *            A valid instance of LinkSmart NetworkManager
	 * @param callback
	 *            An object of a class implementing the @LinkSmartDataServiceCallback
	 *            properly. That instance will receive typed data
	 * @param type
	 *            The type of object this service is expected to receive
	 */
	public LinkSmartDataServiceProxy(ComponentContext context, ObjectMapper objectMapper, String serviceName,
			NetworkManager networkManager, LinkSmartDataServiceCallback callback, Class type) {
		this.context = context;
		this.objectMapper = objectMapper;
		this.serviceName = serviceName;
		this.networkManager = networkManager;
		this.callback = callback;
		this.type = type;
		initBackbone("eu.linksmart.network.backbone.impl.data.BackboneData");
	}

	/**
	 * Utility to start LinkSmart endpoint and OSGi service
	 */
	public void startEndPoint() {
		LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		LOG.info("Starging endpoint for LinkSmart Service: " + serviceName);
		LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		Dictionary<String, String> dict = new Hashtable<String, String>();//NOSONAR squid:S1149 - JPU: required by OSGi API
		dict.put("component.name", this.serviceName);
		registration = this.context.getBundleContext().registerService(DataEndpoint.class.getName(), this, dict);

		if (registration == null) {
			LOG.error("Unable to register the service " + serviceName + " from BundleContext");
			return;
		}

		if (!registerLinkSmartService(serviceParts, serviceName)) {
			LOG.error("Unable to register " + serviceName + " as LinkSmart Service");
		}
	}

	/**
	 * Utility to stop LinkSmart endpoint and OSGi service
	 */
	public void stopEndPoint() {
		LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		LOG.info("Stopping endpoint for LinkSmart Service: " + serviceName);
		LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		this.unregisterLinkSmartService();
		if (registration != null) {
			registration.unregister();
		}

	}

	@Override
	/**
	 * Method receiving data from data backbone
	 */
	public byte[] receive(byte[] message, VirtualAddress va) {
		// TODO: add here decription and verification of message
		// the encription/signing of outgoing messages should go in sednMessage
		// method of JsonRpcMessageManagerImpl

		LOG.debug("Received message " + Arrays.toString(message));
		try {
			callback.notify(objectMapper.readValue(message, type));
		} catch (IOException e) {
			LOG.error("Error parsing data: " + e.getMessage(), e);
		}
		return new byte[0];
	}

	/**
	 * Method setting service parts. It adds the ServiceName as
	 * ServiceAttribute.DESCRIPTION.name() part
	 * 
	 * @param serviceParts
	 */
	public void setServiceParts(Part[] serviceParts) {
		this.serviceParts = new Part[serviceParts.length + 1];
		this.serviceParts[0] = new Part(ServiceAttribute.DESCRIPTION.name(), serviceName);
		for (int i = 1; i < this.serviceParts.length; i++) {
			this.serviceParts[i] = new Part(serviceParts[i - 1].getKey(),serviceParts[i - 1].getValue());
		}
	}

	public Part[] getServiceParts() {
		// Copy the parts to prevent their modification
		Part[] partsCopy = new Part[serviceParts.length];
		for (int i = 0; i < serviceParts.length; i++) {
			Part part = new Part(serviceParts[i].getKey(), serviceParts[i].getValue());
			partsCopy[i] = part;
		}
		return partsCopy;
	}

}
