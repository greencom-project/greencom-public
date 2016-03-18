package eu.greencom.xgateway.configurationbroker.internals.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	
	@XmlElement
	public Component[] component;
	@Override
	public String toString(){
		String ret="";
		
		for(Component c: component){
			if(c!=null){
				ret+= "Component: " + c.toString();
			}else {
				ret+="\tnullcomponent\n";
			}
		}
		return ret;
	}

}
