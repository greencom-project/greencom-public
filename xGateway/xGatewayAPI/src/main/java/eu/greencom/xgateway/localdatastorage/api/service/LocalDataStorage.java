/** 
 * Coded By Giorgio Dal To� on 17/apr/2013 
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
package eu.greencom.xgateway.localdatastorage.api.service;

import java.util.List;
import java.util.Map;

public interface LocalDataStorage {

	/**
	 * Constant to retrieve all values without a limit.
	 */
	public static int NO_LIMIT = 0;

	/**
	 * Store or update a generic POJO in a given collection.
	 * 
	 * All get* methods are used by reflection. All get* methods returning a
	 * non-native type will be embedded inside the object.
	 * 
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param o
	 *            - the object to insert
	 * 
	 * @return true - if the object "o" was successfully stored
	 * @return false - otherwise
	 */
	boolean put(String collection, Object o);

	/**
	 * Store or update a generic json.
	 * 
	 * @param id
	 *            - the json will be augmented with this UID
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param json
	 *            - a key-value representation of the json to be insert
	 * 
	 * @return true - if the json was successfully inserted or updated
	 * @return false - otherwise
	 */
	boolean put(String collection, Map<String, Object> json, String id);

	/**
	 * Store or update a generic json without UID.
	 * 
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param json
	 *            - a key-value representation of the json to be insert
	 * 
	 * @return true - if the json was successfully inserted or updated
	 * @return false - otherwise
	 */
	boolean put(String collection, Map<String, Object> json);

	/**
	 * Store or update a generic json.
	 * 
	 * @param id
	 *            - the json will be augmented with this UID
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param json
	 *            - a string representation of the json to be insert
	 * 
	 * @return true - if the json was successfully inserted or updated
	 * @return false - otherwise
	 */
	boolean put(String collection, String json, String id);

	/**
	 * Store or update a generic json without UID.
	 * 
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param json
	 *            - a string representation of the json to be insert
	 * 
	 * @return true - if the json was successfully inserted or updated
	 * @return false - otherwise
	 */
	boolean put(String collection, String json);

	/**
	 * Find an object by id in a given collection.
	 * 
	 * All set* methods are used by reflection.
	 * 
	 * @param collection
	 *            - the name of the collection in which the json will be
	 *            inserted
	 * @param id
	 *            - the id of the object to find
	 * @param return type - the class of the object to return
	 * 
	 * @return the object with id "id" of the class "return type"
	 */
	<T> T get(String collection, String id, Class<T> returnType);

	/**
	 * Retrieve all object present in a collection.
	 * 
	 * @param collection
	 *            - the name of the collection in which to search
	 * 
	 * @param limit
	 *            maximum number of listed values (0 for all).
	 * 
	 * @return a list of string in json format
	 *
	 */
	List<String> list(String collection, int limit);

	/**
	 * Find an object by a query.
	 * 
	 * @param collectionName
	 * 
	 * @param query
	 * 
	 * @param limit
	 *            maximum number of listed values (0 for all).
	 * 
	 */
	List<Map<String, Object>> list(String collectionName,
			Map<String, Object> query, int limit);

	/**
	 * Find the object with a given id in the given collection.
	 * 
	 * @return a key-value representation of the object - if found
	 * @return null - otherwise
	 */
	Map<String, Object> get(String collection, String id);

	/**
	 * Find the object with a given id in the given collection.
	 * 
	 * @param collection
	 *            - the collection name
	 * @param id
	 *            - the id of the object requested
	 * @return a string serialization of the json object requested
	 */
	String getJson(String collection, String id);

	/**
	 * Remove the object with a given id in the given collection.
	 * 
	 * @return true - if object was successfully removed
	 * @return false - otherwise
	 */
	boolean delete(String collection, String id);

	/**
	 * Deserialize and insert a json.
	 * 
	 * If the json contains the _class param, the object will be stored in a
	 * correct collection and can be retrieved usign the correct class.
	 * Otherwise the object will be inserted in the HashMap collection and can
	 * be retrived as an HashMap.
	 */
	boolean insertOrUpdateJson(String json);

	/**
	 * Check if an object with a given id already exists. The search is
	 * performed in all the collection present in the database.
	 * 
	 * @return true - if the object exists
	 * @return false - otherwise
	 */
	boolean exists(String id);

	/**
	 * Check if an object with a given id already exists in a given collection.
	 * 
	 * @return true - if the object exists
	 * @return false - otherwise
	 */
	boolean exists(String collection, String id);

}
