package eu.greencom.xgateway.deviceabstractionlayer.http.impl;


import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * @author Farmin Farzin
 * This Class provide a timertask which is scheduled in httpClientConsumerImpl and it request the http service for device list by sending the get http.
 * after receiving the response, it updates the device string [] for functionInvoker
 */
public class DeviceUpdater extends TimerTask {

	private String dalAddress;
	private String[] devices;
	HttpRestGetPost hc;
	DeviceValue devicevalue;

	public DeviceUpdater(HttpRestGetPost hc, DeviceValue dv) {
		this.hc = hc;
		this.devicevalue = dv;
	}

	@Override
	public void run() {
		devices = null;
		//making the link for getting device list
		String deviceList = hc.getHTTP("http://" + dalAddress + "/api/devices");
		if (deviceList.contains("unsuccessful")) {
			System.out.println("Not Connected to jemma");
		} else {
			System.out.println(deviceList);
			// putting answer in Json Format

			// For jemma version2:
			JSONObject wholeRespond = new JSONObject(deviceList);
			int code = wholeRespond.getInt("code");
			if (code == 200) {
				
				JSONArray devicelistArray = new JSONArray();
				devicelistArray = wholeRespond.getJSONArray("result");
				if (devicelistArray.isNull(0)) {
					System.out
							.println("there are no Device connected to Jemma");
				} else {
					devices = new String[devicelistArray.length()];
					int deviceLenght = 0;
					for (int i = 0; i < devicelistArray.length(); i++) {

						JSONObject deviceObj = new JSONObject();
						deviceObj = devicelistArray.getJSONObject(i);
						// check if device is not on sleep
						if (deviceObj.getInt("dal.device.status") == 2) {
							devices[deviceLenght] = deviceObj
									.getString("dal.device.UID");
							deviceLenght++;
						}
					}
				}
				devicevalue.setDevices(devices);
			}

		}
	}

	public void setDalIPAddress(String ip) {
		dalAddress = ip;

	}

}
