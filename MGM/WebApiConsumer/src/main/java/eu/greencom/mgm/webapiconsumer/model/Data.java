package eu.greencom.mgm.webapiconsumer.model;

// Copy from: eu.greencom.mgm.mgmdatawareshouse.model
public class Data {
	public String date;// NOSONAR squid:S00116 squid:ClassVariableVisibilityCheck
	public String value;// NOSONAR squid:S00116 squid:ClassVariableVisibilityCheck
	
	public Data(String date, String value){
		this.date=date;
		this.value=value;
	}
}
