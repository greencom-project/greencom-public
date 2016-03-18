/** 
 * Coded By Giorgio Dal Toè on 02/ago/2013 
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

public class MetaInformationLink {

	private String predicate;
	private String subject;
	
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		} else if (obj == null || getClass() != obj.getClass()){
			return false;
		}
		//if (getClass() != obj.getClass()){  //NOSONAR RR
			//return false;  //NOSONAR RR
		//} //NOSONAR RR
		MetaInformationLink other = (MetaInformationLink) obj;
		/*if (predicate == null) { //NOSONAR RR
			if (other.predicate != null){
				return false;
			}
		} else if (!predicate.equals(other.predicate)){
			return false;
		}*/
		
		
		return equals2( other );
	}
	
	public boolean equals2(MetaInformationLink other){
		if (predicate == null || other.predicate == null || !predicate.equals(other.predicate)) {
				return false;
		} 
		if (subject == null || other.subject == null) {
				return false;
		} else if (!subject.equals(other.subject)){
			return false;
		}
		return true;
	}
}


