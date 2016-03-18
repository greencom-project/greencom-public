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
package eu.greencom.xgateway.metainformationstore.api.service;

import java.util.Map;

import eu.greencom.xgateway.metainformationstore.api.domain.MetaInformation;

public interface MetaInformationManager {

	/**
	 * Insert a new meta-information in the MetaInformationStore.
	 * If the meta-information exists will be replaced.
	 * 
	 * @throws MalformedRequestException - if the object to insert is not conformed to 
	 * 										the MetaInformationManager policy
	 * @return true - if the meta-information was inserted <br> false - if was updated
	 */
	<T extends MetaInformation> boolean put(T info) throws MalformedRequestException;
	
	/**
	 * Insert a new meta-information in the MetaInformationStore.
	 * If the meta-information exists will be replaced.
	 * 
	 * @param info - A json string representing a meta-information
	 * @param id - the UUID of the meta-information
	 * 
	 * @throws MalformedRequestException - if the object to insert is not conformed to 
	 * 										the MetaInformationManager policy
	 * @return the id of the inserted object - if the meta-information was inserted or updated
	 */
	String put(String info, String id) throws MalformedRequestException;
	String put(Map<String,Object> info, String id) throws MalformedRequestException;
	Map<String,Object> get(String id) throws MalformedRequestException;
	
	/**
	 * Delete a meta-information with a give id from the MetaInformationStore.
	 */
	<T extends MetaInformation> boolean delete(String id);	
	
	/**
	 * Search for a meta-information having a given id and perform an object cast.
	 */
	<T extends MetaInformation> T get(String id, Class<T> type) throws MalformedRequestException;
}
