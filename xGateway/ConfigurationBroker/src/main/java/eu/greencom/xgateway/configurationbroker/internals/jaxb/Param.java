package eu.greencom.xgateway.configurationbroker.internals.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


public class Param {
	
	public Param(){
		
	}
	
	public Param(String key, String value) {
		this.key=key;
		this.value=value;
	}

	@XmlAttribute
	public String key;
	
	@XmlValue
	public String value;

}
