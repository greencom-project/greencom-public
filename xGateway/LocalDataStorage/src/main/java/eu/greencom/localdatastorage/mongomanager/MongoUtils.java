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
package eu.greencom.localdatastorage.mongomanager;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;


public class MongoUtils {

	private Map<String, MongoClient> clients;
	private static MongoUtils instance;
	
	private MongoUtils(){
		clients = new HashMap<String, MongoClient>();
	}
	
	public static MongoUtils getInstance(){
		if(instance == null){
		    instance = new MongoUtils();
		}
	
		return instance;
	}
	
	public DB getDB(String url, String dbName) throws UnknownHostException{
	
		if(!clients.containsKey(url)) {
			clients.put(url, new MongoClient(url));
		}
	
		return ((MongoClient)clients.get(url)).getDB(dbName);
	
	}
	
}

