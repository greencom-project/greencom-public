/** 
 * Coded By Giorgio Dal Toè on 06/ago/2013 
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
package eu.greencom.xgateway.metainformationstore.api.service;

public class MalformedRequestException extends Exception {

	private static final long serialVersionUID = -7265008930535365736L;

	public MalformedRequestException(String message){
		super(message);
	}
	
	public MalformedRequestException(Throwable cause){
		super(cause);
	}
	
	public MalformedRequestException(String message, Throwable cause){
		super(message, cause);
	}
}
