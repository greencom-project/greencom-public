package eu.greencom.mgm.servicebus.model;

import java.util.Map;

@SuppressWarnings("all")
//JPU: members public for serialization/access purposes
public class NAssistResponse extends NAssistParentMsg {
	public String Result;//NOSONAR
	public Map<String, String> Values;//NOSONAR
}
