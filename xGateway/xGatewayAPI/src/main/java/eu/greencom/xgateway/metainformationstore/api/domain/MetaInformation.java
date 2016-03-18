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

import java.util.HashSet;
import java.util.Set;


public abstract class MetaInformation {

	private String metaid;
	private Set<MetaInformationLink> links=new HashSet<MetaInformationLink>();
	
	public String getId() {
		return metaid;
	}
	public void setId(String metaid) {
		this.metaid = metaid;
	}

	public void addLink(String predicate, String subject){
		MetaInformationLink l=new MetaInformationLink();
		l.setPredicate(predicate);
		l.setSubject(subject);
		links.add(l);
	}
	
	public void removeLink(String predicate, String subject){
		MetaInformationLink l=new MetaInformationLink();
		l.setPredicate(predicate);
		l.setSubject(subject);
		links.remove(l);
	}
	public Set<MetaInformationLink> getLinks() {
		return links;
	}
	public void setLinks(Set<MetaInformationLink> links) {
		this.links = links;
	}
}
