package eu.greencom.mgm.webapiconsumer.model;//NOSONAR

public class DriverAzureCredentials {
	public String Namespace;//NOSONAR findbugs:NM_FIELD_NAMING_CONVENTION squid:S00116 squid:ClassVariableVisibilityCheck - JPU: Intended as serializable message property
	public String SharedSecretIssuer;//NOSONAR findbugs:NM_FIELD_NAMING_CONVENTION squid:S00116 squid:ClassVariableVisibilityCheck 
	public String SharedSecretKey;//NOSONAR findbugs:NM_FIELD_NAMING_CONVENTION squid:S00116 squid:ClassVariableVisibilityCheck 

	@Override	
	public String toString(){
		return "Namespace: " + Namespace + " SharedSecretIssuer: " + SharedSecretIssuer + " SharedSecretKey: " + SharedSecretKey;
	}
}