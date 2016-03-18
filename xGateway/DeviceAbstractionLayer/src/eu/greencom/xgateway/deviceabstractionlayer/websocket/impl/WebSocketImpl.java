package eu.greencom.xgateway.deviceabstractionlayer.websocket.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;



public class WebSocketImpl implements ManagedService {
	
	private static final Logger LOG = Logger.getLogger(WebSocketImpl.class
			.getName());
	WebSocketClient client = new WebSocketClient();
	EchoSocket socket;
	private EventAdmin eventAdmin;
	ComponentContext context;
	private ConfigurationAdmin configAdmin;
	public static final String SERVICE_PID = "DeviceAbstractionLayerWebSocket";
	String dalIPAddress = "192.168.0.1";
	private ServiceRegistration<ManagedService> configService;
	
	

	public void activate(ComponentContext context) {
		System.out.println("WebSocket client Starting....");
		LOG.info("Activating...");
		this.context = context;
		socket = new EchoSocket(eventAdmin);
		// For configuring the bundle
		Hashtable<String, String> d = new Hashtable<String, String>();
		d.put(Constants.SERVICE_PID,SERVICE_PID);
		configService = context.getBundleContext().registerService(
				ManagedService.class, this, d);

		// configure(context.getProperties());

		// ws.openWebSocket("ws://130.192.86.173/ws");
		String url = "ws://" + dalIPAddress + "/ws";
		openWebSocket(url);
		sendData("{'dal.function.UID':'*','dal.function.property.name':'*'}");

	}


	public void deactivate(ComponentContext context) {
		closeWebSocket();
		this.context = null;
		configService.unregister();
		System.out.println("WebSocket client Stopping....");

	}


	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		LOG.info("New configuration received");
		if (properties == null || properties.get("JemmaAddress") == null) {
			LOG.info("Configuration received is empty, set the default values");
			Properties p = new Properties();
			p.put("JemmaAddress", dalIPAddress);
			try {
				configAdmin.getConfiguration(SERVICE_PID)
						.update((Dictionary) p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		String updatedDalAddress = (String) properties.get("JemmaAddress");
		if(!updatedDalAddress.equals(dalIPAddress)){
			LOG.info("Initiating new WebSocket Connection to " + updatedDalAddress);
			dalIPAddress = updatedDalAddress;
		}
		
	}
	

	protected void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	protected void removeConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = null;
		LOG.info("removeConfigurationAdmin in serial CommunicationAdapter"
				+ configAdmin.toString());
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}
	
	public void openWebSocket(String URL) {
		// TODO Auto-generated method stub
		URI destURI;

		try {
			client.start();
			destURI = new URI(URL);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, destURI, request);
			System.out.printf("Connecting to : %s%n", destURI);

			socket.awaitClose(5, TimeUnit.SECONDS);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public void closeWebSocket() {
		LOG.info("Closing.....");
		if (socket.isConnected) {
			LOG.info("socket is connected , closing...");
			socket.closeSession();
			try {
				client.stop();
			} catch (Exception e) {
				LOG.info("problem in stopping WebSocket Client");
				e.printStackTrace();
			}
		}
		
	}

	public void sendData(String data) {
		System.out.println("Sending data to webSocket");
		socket.sendMessage(data);
	}

}
