/** 
 * Coded By Giorgio Dal Toè on 01/ago/2013 
 *
 * Internet of Things Service Management Unit 
 * Pervasive Technologies Area
 * Istituto Superiore Mario Boella
 * Tel. (+39) 011 2276614
 * Email: daltoe@ismb.it
 * Email: giorgio.daltoe@gmail.com 
 * 
 * '||'  .|'''.|  '||    ||' '||''|.   
 *  ||   ||..  '   |||  |||   ||   ||  
 *  ||    ''|||.   |'|..'||   ||'''|.  
 *  ||  .     '||  | '|' ||   ||    || 
 * .||. |'....|'  .|. | .||. .||...|'
 *
 * Via Pier Carlo Boggio 61 
 * 10138 Torino, Italy
 * T 011/2276201; F 011/2276299
 * info@ismb.it
 */
package eu.greencom.xgateway.metainformationstore.api.domain;

public class GeographicalLocation {

	private long latitude;
	private long longitude;
	
	public long getLatitude() {
		return latitude;
	}
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	public long getLongitude() {
		return longitude;
	}
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
}
