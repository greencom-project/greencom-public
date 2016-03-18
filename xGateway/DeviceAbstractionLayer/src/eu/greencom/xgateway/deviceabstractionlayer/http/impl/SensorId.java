package eu.greencom.xgateway.deviceabstractionlayer.http.impl;

/**
 * @author Farmin Farzin
 * This class gets the name string in Jemma format and return the sensor name in GreenCom format
 *
 */
public class SensorId {
	
	String originalId;
	String[] firstPartArray ;
	String firstpartg;
	String SensorIDArray [] ;
	String SensorID;
	
	public String getSensorId(String original){
		
		firstPartArray = original.split("app.");
		firstpartg = firstPartArray[1];
		String SensorIDArray[] = firstpartg.split("-");
		String SensorID = SensorIDArray[0];
		
		return SensorID;
	
	}

}

