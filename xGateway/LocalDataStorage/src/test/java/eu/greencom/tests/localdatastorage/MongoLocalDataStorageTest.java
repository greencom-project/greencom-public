/** 
 * Coded By Giorgio Dal Toè on 30/lug/2013 
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
package eu.greencom.tests.localdatastorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

import eu.greencom.localdatastorage.impl.MongoLocalDataStorage;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;

public class MongoLocalDataStorageTest {

	static MongoLocalDataStorage storage;
	
	public static DB mockDatabase(){
		DBCollection coll=mock(DBCollection.class);
		
		//the db contains 3 object {a:1},{b:2},{c:3} for query sent=true
		DBCursor c1=mock(DBCursor.class);
		when(c1.iterator()).thenReturn(c1);
		when(c1.hasNext()).thenReturn(true,true,true, false);
		when(c1.next()).thenReturn(new BasicDBObject("a",1),new BasicDBObject("b",2),new BasicDBObject("c",3));
		when(coll.find(new BasicDBObject("sent", "false"))).thenReturn(c1);
		
		DB db=mock(DB.class);
		when(db.getCollection("prova")).thenReturn(coll);
		return db;
	}
	
	@BeforeClass
	public static void setUpClass(){
		storage=new MongoLocalDataStorage();
		storage.setDatabase(MongoLocalDataStorageTest.mockDatabase());
	}
	
	@Test
	public void testList(){
		List<Map<String,Object>> res=storage.list("prova",new BasicDBObject("sent", "false"),LocalDataStorage.NO_LIMIT);
		assertNotNull(res);
		assertEquals(3, res.size());
		assertEquals("{ \"a\" : 1}", JSON.serialize(res.get(0)));
	}

}
