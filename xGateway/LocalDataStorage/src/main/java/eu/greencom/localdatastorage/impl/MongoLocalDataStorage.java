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
package eu.greencom.localdatastorage.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import eu.greencom.localdatastorage.mongomanager.MongoUtils;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;

public class MongoLocalDataStorage implements LocalDataStorage {
	private static final Logger LOG = Logger
			.getLogger(MongoLocalDataStorage.class);

	private DB database;

	public DB getDatabase() {
		return database;
	}

	public void setDatabase(DB database) {
		this.database = database;
	}

	public void activate(ComponentContext c, Map<String, Object> props) {
		LOG.info("Activating LocalDataStorage " + c.toString());
		try {
			database = MongoUtils.getInstance()
					.getDB((String) props.get("db.url"),
							(String) props.get("db.name"));
		} catch (UnknownHostException e) {
			LOG.error(
					"Error creating MongoLocalDataStorage: " + e.getMessage(),
					e);
			return;
		}
	}

	public void deactivate() {
		LOG.info("Deactivating LocalDataStorage");
	}

	@Override
	public List<Map<String, Object>> list(String collectionName,
			Map<String, Object> query, int limit) {
		LOG.debug("Request received for querying collection -> "
				+ collectionName);
		DBCollection coll = database.getCollection(collectionName);
		DBObject q = new BasicDBObject(query);
		// limit to 2500 values returned because of the heap memory problem on
		// raspberry
		DBCursor curs;
		if (limit > 0) {
			curs = coll.find(q).limit(limit);
		} else {
			curs = coll.find(q);
		}
		LOG.debug("Query returns " + curs.count() + " elements");
		List<Map<String, Object>> res = new LinkedList<Map<String, Object>>();

		for (DBObject o : curs) {
			res.add(o.toMap());
		}

		return res;
	}

	@Override
	public boolean insertOrUpdateJson(String json) {

		LOG.debug("Received object to store");
		Map<String, Object> o = parseJsonString(json);
		DBObject toStore = new BasicDBObject(o);

		if (!toStore.keySet().isEmpty()) {
			String className = o.getClass().getSimpleName();
			if (toStore.get("_class") != null) {
				try {
					String strClassName = (String) toStore.get("_class");
					Class c = Class.forName(strClassName);
					className = c.getSimpleName();
				} catch (ClassNotFoundException e) {
					// ff
					LOG.error(
							"insertOrUpdateJson ClassNotFoundException error:"
									+ e.getMessage(), e);

				}
			}
			DBCollection c = database.getCollection(className);

			WriteResult result = c.save(toStore);
			if (result.getError() != null) {
				LOG.error("An error occurs during insert of the object -> "
						+ toStore);
				return false;
			}
			LOG.info("Insert success: Object -> " + toStore);
			return true;
		} else {
			LOG.info("Nothing to insert for object -> " + toStore);
			return false;
		}
	}

	private <T> T convertMapToObject(Class<T> c, DBObject src) {
		ObjectMapper o = new ObjectMapper();
		o.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			T dst = o.readValue(src.toString(), c);
			return dst;
		} catch (JsonParseException e) {
			LOG.error(e);
		} catch (JsonMappingException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}

	private DBObject convertObjectToMap(Object src) {
		ObjectMapper o = new ObjectMapper();
		String serializedObject = null;
		try {
			serializedObject = o.writeValueAsString(src);
		} catch (JsonProcessingException e) {
			// ff
			LOG.error("convertObjectToMap error:" + e.getMessage(), e);
		}
		BasicDBObject ret = (BasicDBObject) JSON.parse(serializedObject);
		return ret;
	}

	private Map<String, Object> parseJsonString(String jsonString) {
		ObjectMapper m = new ObjectMapper();
		try {
			return m.readValue(jsonString,
					new TypeReference<HashMap<String, Object>>() {
					});
		} catch (JsonParseException e) {
			LOG.error(
					"parseJsonString An error occurs parsing json"
							+ e.getMessage(), e);
		} catch (JsonMappingException e) {
			// ff
			LOG.error(
					"parseJsonString JsonMappingException error:"
							+ e.getMessage(), e);
		} catch (IOException e) {
			// ff
			LOG.error("parseJsonString IOException error:" + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean put(String collection, Map<String, Object> json, String id) {

		if (id == null) {
			return false;
		}

		DBCollection c = database.getCollection(collection);

		json.put("_id", id);

		WriteResult w = c.save(new BasicDBObject(json));
		if (w.getError() != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean put(String collection, Map<String, Object> json) {
		DBCollection c = database.getCollection(collection);

		WriteResult w = c.save(new BasicDBObject(json));
		if (w.getError() != null) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> get(String collection, String id) {
		DBCollection c = database.getCollection(collection);
		DBObject o = c.findOne(new BasicDBObject("_id", id));

		if (o == null) {
			return null;
		}

		return o.toMap();
	}

	@Override
	public String getJson(String collection, String id) {
		DBCollection c = database.getCollection(collection);
		DBObject o = c.findOne(new BasicDBObject("_id", id));
		if (o == null) {
			return null;
		}

		return o.toString();
	}

	@Override
	public boolean delete(String collection, String id) {
		DBCollection c = database.getCollection(collection);
		WriteResult w = c.remove(new BasicDBObject("_id", id));
		if (w.getError() != null) {
			return false;
		}
		return true;
	}

	// Bulk deletion based on query used by TimeSeriesManager
	// Intended for internal usage
	public boolean delete(String collectionName, Map<String, Object> query) {
		DBCollection c = database.getCollection(collectionName);
		try {
			c.remove(new BasicDBObject(query));
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean put(String collection, Object o) {
		LOG.debug("Received object to store");

		DBObject toStore = convertObjectToMap(o);

		if (toStore == null) {
			LOG.warn("Null object will be ignored");
			return false;
		}
		if (!toStore.keySet().isEmpty()) {
			toStore.put("_class", o.getClass().getName());
			DBCollection c = database.getCollection(collection);

			WriteResult result = c.save(toStore);
			if (result.getError() != null) {
				LOG.error("An error occurs during insert of the object -> "
						+ toStore);
				return false;
			}
			LOG.info("Insert success: Object -> " + toStore);
			return true;
		} else {
			LOG.info("Nothing to insert for object -> " + toStore);
			return false;
		}
	}

	@Override
	public <T> T get(String collection, String id, Class<T> returnType) {
		LOG.debug("Request received for querying collection -> " + collection);
		DBCollection coll = database.getCollection(collection);
		DBObject o = coll.findOne(new BasicDBObject("_id", id));

		if (o == null) {
			return null;
		}

		return convertMapToObject(returnType, o);
	}

	@Override
	public boolean put(String collection, String json, String id) {
		if (id == null) {
			return false;
		}

		LOG.info("Received json to insert for id=" + id + " : " + json);
		DBCollection c = database.getCollection(collection);

		DBObject o = (DBObject) JSON.parse(json);
		o.put("_id", id);

		WriteResult w = c.save(o);
		if (w.getError() != null) {
			LOG.error("Error inserting json: " + w.getError());
			return false;
		}
		LOG.info("Json successfully inserted");
		return true;
	}

	@Override
	public boolean put(String collection, String json) {
		DBCollection c = database.getCollection(collection);

		DBObject o = (DBObject) JSON.parse(json);

		WriteResult w = c.save(o);
		if (w.getError() != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean exists(String id) {
		Set<String> collections = database.getCollectionNames();
		for (String collection : collections) {
			DBCollection c = database.getCollection(collection);
			DBCursor curs = c.find(new BasicDBObject("_id", id));
			if (curs != null && curs.length() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean exists(String collection, String id) {
		DBCollection c = database.getCollection(collection);
		DBCursor curs = c.find(new BasicDBObject("_id", id));
		if (curs != null && curs.length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<String> list(String collection, int limit) {
		DBCollection c = database.getCollection(collection);
		List<String> ret = new LinkedList<String>();
		DBCursor curs;
		if (limit > 0) {
			curs = c.find().limit(limit);
		} else {
			curs = c.find();
		}
		for (DBObject o : curs) {
			ret.add(JSON.serialize(o));
		}
		return ret;
	}
	
	@Override
	public void removeDB(String collection) {
		DBCollection c = database.getCollection(collection);
		if (c != null) {
			c.drop();
		}
	}

}
