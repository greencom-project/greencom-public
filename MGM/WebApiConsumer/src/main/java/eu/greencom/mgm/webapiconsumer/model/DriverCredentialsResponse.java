package eu.greencom.mgm.webapiconsumer.model;

public class DriverCredentialsResponse {
	public ResponseStatus ResponseStatus; // NOSONAR squid:S00116 squid:ClassVariableVisibilityCheck	- JPU: Proprietary message property name									
	public DriverAzureCredentials Credentials; // NOSONAR squid:S00116 squid:ClassVariableVisibilityCheck

	public DriverCredentialsResponse() {
		this.ResponseStatus = new ResponseStatus();
	}

}
