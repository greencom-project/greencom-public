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
package eu.greencom.localdatastorage.metainformationstore.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.greencom.localdatastorage.impl.MongoLocalDataStorage;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;
import eu.greencom.xgateway.metainformationstore.api.domain.MetaInformation;
import eu.greencom.xgateway.metainformationstore.api.service.MalformedRequestException;
import eu.greencom.xgateway.metainformationstore.api.service.MetaInformationManager;

public class MongoMetaInformationManager implements MetaInformationManager {
    
    private static final Logger LOG = Logger.getLogger(MongoMetaInformationManager.class);  

	private static final String COLLECTIONNAME="resource";
	private LocalDataStorage store;
	
	public void bindLocalDataStorage(LocalDataStorage store){
		this.store=store;
	}
	
	public void unbindLocalDataStorage(LocalDataStorage store){
	    LOG.info("UnbindLocalDataStorage "+ store.toString());
		this.store=null;
	}
	
	public void activate(ComponentContext c, Map<String, Object> props){ //NOSONAR squid : S1172 ff: this is standard
		//store equal to MongoLocalDataStorage.getInstance((String)props.get("db.url") - (String)props.get("db.name"))
        LOG.info("It is in MongoMetaInfomangaer , activate method" );
	}
	
	public void deactivate(){
	       LOG.info("It is in MongoMetaInfomangaer , deactivate method");
	}

	@Override
	public <T extends MetaInformation> boolean put(T info) throws MalformedRequestException{
		if(info.getId()==null || info.getId().length()==0){
			throw new MalformedRequestException("The property _id of the class MetaInformation cannot be null or empty string");
		}
		return store.put(COLLECTIONNAME, info);
	}

	@Override
	public <T extends MetaInformation> boolean delete(String id) {
		return store.delete(COLLECTIONNAME, id);
	}

	@Override
	public <T extends MetaInformation> T get(String id, Class<T> type) throws MalformedRequestException{
		if(id==null || id.length()==0){
			throw new MalformedRequestException("Cannot be search for a null id");
		}
		return store.get(COLLECTIONNAME, id, type);
	}

	@Override
	public String put(String info, String id) throws MalformedRequestException {
		if(id==null || id.length()==0){
			throw new MalformedRequestException("Cannot be insert a meta-information with a null id");
		}
		return store.put(COLLECTIONNAME, info, id) ? id:null;
	}

	@Override
	public Map<String,Object> get(String id) throws MalformedRequestException {
		if(id==null || id.length()==0){
			throw new MalformedRequestException("Cannot be insert a meta-information with a null id");
		}
		
		return store.get(COLLECTIONNAME, id);
	}

	@Override
	public String put(Map<String, Object> info, String id)
			throws MalformedRequestException {
		if(id==null || id.length()==0){
			throw new MalformedRequestException("Cannot be insert a meta-information with a null id");
		}
		return store.put(COLLECTIONNAME, info, id) ? id:null;
	}

}
