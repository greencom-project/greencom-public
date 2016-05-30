package eu.greencom.xgateway.localwebapi.fakeevent;

public class SensorParameters {
	
	public String sensor_name;
	public double sensor_value;
	public long sensor_timestamp;
	
	public SensorParameters (String sensor_name , double sensor_value , long sensor_timestamp){
		this.sensor_name = sensor_name;
		this.sensor_value = sensor_value;
		this.sensor_timestamp = sensor_timestamp;
	}
	
}
