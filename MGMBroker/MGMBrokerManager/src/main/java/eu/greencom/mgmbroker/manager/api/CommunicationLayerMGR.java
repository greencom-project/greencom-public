package eu.greencom.mgmbroker.manager.api;

import java.util.Date;
import java.util.Map;

public interface CommunicationLayerMGR {

	/**
	 * Function to be used to get the list fo alive gateways
	 * 
	 * @return a Map with the Gateway ID as key and the timestamp of the latest
	 *         alice message received for each gateways
	 */
	public Map<String, Date> getGateways();

	/**
	 * Function to be used to clan the list of old gateways removing those which
	 * did not send an alive message in the lst 10 minutes
	 * 
	 */
	public void cleanOldGateways();

	/**
	 * Function to be used to solicitate an alive message from a gateway
	 * 
	 * @param gatewayId
	 */
	public Date solicitateAliveMessage(String gatewayId);

}
