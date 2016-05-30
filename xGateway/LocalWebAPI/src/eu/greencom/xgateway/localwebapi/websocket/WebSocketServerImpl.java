package eu.greencom.xgateway.localwebapi.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.greencom.api.domain.SampledValue;

/**
 * @author Farmin Farzin
 *
 *         This class implements the websocket server which listens to the
 *         events of sensors which websocket clients subscribed.
 * 
 *         after receiving the event which has been subscribed, it sends back
 *         through websocket to client.
 * 
 *         This class will provide live data for GUI like live chart
 *
 */
@WebSocket
public class WebSocketServerImpl implements EventHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(WebSocketServerImpl.class.getName());
	private Session session;
	private static Gson gson;
	private ServiceRegistration registration;
	private static ComponentContext componentcontext;
	public Map<String, String> activeSubscriptionMap;

	protected void activate(Map<String, Object> props) {
		LOG.info("web socket server activated");
		BundleContext ff = FrameworkUtil.getBundle(WebSocketServerImpl.class).getBundleContext();
		gson = new Gson();
	}

	protected void modified(Map<String, Object> props) {
		LOG.info("web socket server modified");
	}

	protected void deactivate(Map<String, Object> props) {
		LOG.info("web socket server deactivated");
		this.session.close();
		unregisterEventHandlerService();
	}

	@Override
	public void handleEvent(Event event) {
		LOG.debug("Event received: " + event.toString());
		SampledValue sample = (SampledValue) event.getProperty("sampledValue");
		Map event_result = new HashMap<String, String>();
		event_result.put("device_id", sample.getDeviceID());
		event_result.put("value", sample.getValue());
		event_result.put("timestamp", sample.getTimestamp());
		JSONObject event_to_send_Obj = new JSONObject(event_result);
		String event_to_send = event_to_send_Obj.toString();
		try {
			if (this.session.isOpen()) {
				this.session.getRemote().sendString(event_to_send);
			}
		} catch (IOException e) {
			LOG.error("IOException in sending event message in websocket" + e);
		}

	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		LOG.debug("WebSocket Close: statusCode=" + statusCode + ", reason=" + reason);
		// unregister previous event handler
		this.session.close();
		unregisterEventHandlerService();

	}

	@OnWebSocketError
	public void onError(Throwable t) {
		LOG.error("WebSocket Error: " + t.getMessage());

	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		try {
			UUID sessionId = UUID.randomUUID();
			JSONObject conResponseObject = new JSONObject();
			conResponseObject.put("session", sessionId);
			session.getRemote().sendString(conResponseObject.toString());
		} catch (IOException e) {
			LOG.error("WebSocket IOEXception in websocket connection" + e);
		} catch (JSONException e) {
			LOG.error("WebSocket JSONException in websocket connection" + e);
		}

		activeSubscriptionMap = new HashMap<String, String>();

	}

	@OnWebSocketMessage
	public void onMessage(String message) {

		LOG.debug("WebSocket websocket received Message: " + message);
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(message).getAsJsonObject();
		String commandType = object.get("command").getAsString();
		if (commandType.equalsIgnoreCase("subscribe")) {
			// get Topic out of json and subscribe
			String topicToSubscribe = object.get("topic").getAsString();
			Hashtable properties = new Hashtable();
			// properties.put("EVENT_TOPIC", topicToSubscribe);
			properties.put("event.topics", topicToSubscribe);

			// register event handlers
			LOG.debug("WebSocket subscribing to event with this topic:" + topicToSubscribe);
			registration = FrameworkUtil.getBundle(WebSocketServerImpl.class).getBundleContext()
					.registerService(EventHandler.class.getName(), this, properties);
			// send Success msg
			try {
				JSONObject conResponseObject = new JSONObject();
				conResponseObject.put("result", "success");
				conResponseObject.put("topic", topicToSubscribe);
				session.getRemote().sendString(conResponseObject.toString());
			} catch (JSONException e) {
				LOG.error("JSONException in sending message in websocket" + e);
			} catch (IOException e) {
				LOG.error("IOException in sending message in websocket" + e);
			}

			// add subscription to activeSubscriptionMap
			activeSubscriptionMap.put("topic", topicToSubscribe);
		}

	}

	public void unregisterEventHandlerService() {
		LOG.debug("Unregistering websocket session");
		if (registration != null) {
			try {
				registration.unregister();
			} catch (IllegalStateException e) {
				// do nothing, the service was unregistered before
				LOG.debug("IllegalStateException, but no action needed because the service was unregistered before ",
						e);
			}
		}

	}
}
