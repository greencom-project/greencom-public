package eu.greencom.mgmbroker.gateway;

public class MGMBrokerGateway {

	private MGMBrokerGateway(){
		
	}
	
	public static String getConfigurationPID() {
		return "eu.greencom.mgmbroker.gateway";
	}

	public static String getManagerIDPropertyName() {
		return "MANAGER_ID";
	}
}
