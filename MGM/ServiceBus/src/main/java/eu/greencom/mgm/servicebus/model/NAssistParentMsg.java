package eu.greencom.mgm.servicebus.model;

@SuppressWarnings("all")
// JPU: suppress "squid:S00116" complaining about upper-case writing since
// serialized as message property names and "squid:ClassVariableVisibilityCheck"
// since they need to be public.
public abstract class NAssistParentMsg {

	public String ID;//NOSONAR 
	public String Type;//NOSONAR 
	public String DriverID;//NOSONAR 

}
