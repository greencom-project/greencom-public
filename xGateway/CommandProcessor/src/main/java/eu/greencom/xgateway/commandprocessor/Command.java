package eu.greencom.xgateway.commandprocessor;

import java.util.Map;

public class Command {
	
	
	private String Id;
	private Map<String, Object> Parameters;
	
	public Object getParameter(String name){
		return Parameters.get(name);
	}
	
	public String getId() {
		return Id;
	}
}
