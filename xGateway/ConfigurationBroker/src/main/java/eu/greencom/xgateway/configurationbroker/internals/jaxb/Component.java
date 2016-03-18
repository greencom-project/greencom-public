package eu.greencom.xgateway.configurationbroker.internals.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class Component {
	
	@XmlAttribute
	public String name;
	
	@XmlElement
	public Param[] param;
	@Override
	public String toString(){
		String ret="["+this.name+"]\n";
		
		for(Param p: param){
			if(p!=null){
				ret+= "\t" + "["+p.key+"="+p.value+"]" + "\n";
			}else {
				ret+="\tnullparam\n";
			}
		}
		return ret;
	}
}
