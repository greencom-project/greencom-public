package eu.linksmart.network.jsonrpc.utils;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.event.Event;

import eu.linksmart.network.jsonrpc.api.JsonRpcNotification;
import eu.linksmart.network.jsonrpc.api.JsonRpcRequest;
import eu.linksmart.network.jsonrpc.api.JsonRpcResponse;
import eu.linksmart.network.jsonrpc.api.Params;

/**
 * Utilities to transform messages into OSGi events and vice versa
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class OSGiUtils {

	public static final String JSONRPC_RESPONSE_RESULT_TOPIC = "event/jsonrpc/response/RESULT";
	public static final String JSONRPC_RESPONSE_ERROR_TOPIC = "event/jsonrpc/response/ERROR";
	public static final String JSONRPC_REQUEST_TOPIC = "event/jsonrpc/request/";
	public static final String JSONRPC_NOTIFICATION_TOPIC = "event/jsonrpc/notification/";

	// Prevent initialization
	private OSGiUtils() {

	}

	public static Event toEvent(JsonRpcNotification notification) {
		return new Event(JSONRPC_NOTIFICATION_TOPIC + notification.getMethod(),
				OSGiUtils.notificationToMap(notification));
	}

	public static Event toEvent(JsonRpcRequest request) {
		return new Event(JSONRPC_REQUEST_TOPIC + request.getMethod(), OSGiUtils.requestToMap(request));
	}

	public static JsonRpcRequest toRequest(Event event) {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setId((String) event.getProperty("id"));
		Params params = new Params();
		params.setData((String) event.getProperty("data"));
		params.setHost((String) event.getProperty("host"));
		params.setPriority((Integer) event.getProperty("priority"));
		params.setSource((String) event.getProperty("source"));
		params.setTimestamp((Long) event.getProperty("timestamp"));
		params.setUri((String) event.getProperty("uri"));
		request.setParams(params);
		return request;
	}

	public static JsonRpcNotification toNotification(Event event) {
		JsonRpcNotification notification = new JsonRpcNotification();
		Params params = new Params();
		params.setData((String) event.getProperty("data"));
		params.setHost((String) event.getProperty("host"));
		params.setPriority((Integer) event.getProperty("priority"));
		params.setSource((String) event.getProperty("source"));
		params.setTimestamp((Long) event.getProperty("timestamp"));
		params.setUri((String) event.getProperty("uri"));
		notification.setParams(params);
		return notification;
	}

	public static Event toEvent(JsonRpcResponse response) {
		Map<String, Object> eventProperties = OSGiUtils.responseToMap(response);
		eventProperties.put("id", response.getId());
		return new Event(response.getError() != null ? JSONRPC_RESPONSE_ERROR_TOPIC : JSONRPC_RESPONSE_RESULT_TOPIC,
				eventProperties);
	}

	public static Map<String, Object> requestToMap(JsonRpcRequest request) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", request.getId());
		Params params = request.getParams();
		if (params != null) {
			properties.put("data", params.getData());
			properties.put("host", params.getHost());
			properties.put("priority", params.getPriority());
			properties.put("source", params.getSource());
			properties.put("timestamp", params.getTimestamp());
			properties.put("uri", params.getUri());
		}
		return properties;
	}

	public static Map<String, Object> notificationToMap(JsonRpcNotification notification) {
		Map<String, Object> properties = new HashMap<String, Object>();

		Params params = notification.getParams();
		if (notification.getParams() != null) {
			properties.put("data", params.getData());
			properties.put("host", params.getHost());
			properties.put("priority", params.getPriority());
			properties.put("source", params.getSource());
			properties.put("timestamp", params.getTimestamp());
			properties.put("uri", params.getUri());
		}
		return properties;
	}

	public static Map<String, Object> responseToMap(JsonRpcResponse response) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if (response.getError() != null) {
			properties.put("code", response.getError().getCode());
			properties.put("data", response.getError().getData());
		} else if (response.getResult() != null) {
			properties.put("code", response.getResult().getCode());
			properties.put("data", response.getResult().getData());
			properties.put("host", response.getResult().getHost());
			properties.put("uri", response.getResult().getUri());
		}

		return properties;
	}

	public static Map<String, Object> dictionaryToMap(Dictionary<String, ?> properties) {
		if (properties == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			map.put(key, properties.get(key));
		}
		return map;
	}

	public static Dictionary<String, Object> mapToDictionary(Map<String, Object> properties) {
		Dictionary<String, Object> dict = new Hashtable<String, Object>();// NOSONAR
																			// squid:S1149
																			// -
																			// JPU:
																			// required
																			// by
																			// OSGi
																			// API
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey();
			dict.put(key, properties.get(key));
		}
		return dict;
	}

}
