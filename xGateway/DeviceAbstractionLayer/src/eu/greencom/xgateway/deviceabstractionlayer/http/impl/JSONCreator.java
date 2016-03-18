package eu.greencom.xgateway.deviceabstractionlayer.http.impl;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONCreator {

	String value1;
	String value2;
	//String key1 = "dal.function.UID";
	String key2 = "operation";
	JSONObject obj;

	public JSONCreator(String sentValue2) {
		//this.value1 = sentValue1;
		this.value2 = sentValue2;
	}

	public JSONObject makeJson() throws JSONException {
		obj = new JSONObject();
		//obj.put(key1, value1);
		obj.put(key2, value2);
		return obj;

	}

}
