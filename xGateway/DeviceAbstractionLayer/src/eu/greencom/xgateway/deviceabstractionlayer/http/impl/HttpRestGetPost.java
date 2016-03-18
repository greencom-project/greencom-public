package eu.greencom.xgateway.deviceabstractionlayer.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class HttpRestGetPost {
	
	public HttpRestGetPost(){
		
	}
	
	public String getHTTP(String url) {
		String output = null;
		Boolean responseCode = false;
		String responseString = null;
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(url);

			getRequest.setHeader("Content-Type", "application/json");
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("No response from DAL");
				System.out.println("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
			}else {
				responseCode = true;
			}
			
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println("New Response: " + responseString);
			
						
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					(response.getEntity().getContent())));
//
//			System.out.println("Output from Server .... \n");
//			while ((output = br.readLine()) != null) {
//				//output = br.readLine();
//				System.out.println(output);
//				System.out.println("hey");
//			}

			httpClient.getConnectionManager().shutdown();

		} catch (IOException e) {
			System.out.println("error in connection" + e);
		}
		System.out.println("implementing httpClient for: " + url);

		if (responseCode) {
			return responseString;
		} else {
			return "unsuccessful";
		}

	}

	public String postHTTP(String url, String FID, String FProperty) {
		String respond = null;
		String completeURL = url + FID;
		HttpResponse httpResponse = null;
		StringEntity documentStringified = null;
		DefaultHttpClient Client = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(completeURL);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("Accept", "application/json");

		JSONCreator json = new JSONCreator(FProperty);
		JSONObject jobj = json.makeJson();
		try {
			documentStringified = new StringEntity(jobj.toString());
			httppost.setEntity(documentStringified);
			httpResponse = Client.execute(httppost);

		} catch (UnsupportedEncodingException e) {
			System.out.println("problem in post: " + e.getMessage());
			e.printStackTrace();
			return "unsuccessful";			
		} catch (ClientProtocolException e) {
			System.out.println("problem in post: " + e.getMessage());
			e.printStackTrace();
			return "unsuccessful";
		} catch (IOException e) {
			System.out.println("problem in post: " + e.getMessage());
			e.printStackTrace();
			return "unsuccessful";
		}
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			return "unsuccessful";
		}

		try {
			InputStream inputStream = httpResponse.getEntity().getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			respond = sb.toString();
			System.out.println("Response is: " + respond);
			JSONObject jres = new JSONObject(respond);
			if (jres.getInt("code") != 200){
				return "unsuccessful";
			}
		} catch (Exception e) {
			System.out.println("problem in reading post response: " + e.getMessage());
			return "unsuccessful";
		}
		return respond;
	}

}
