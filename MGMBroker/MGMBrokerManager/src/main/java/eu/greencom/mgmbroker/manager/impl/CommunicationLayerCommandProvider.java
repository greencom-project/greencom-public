package eu.greencom.mgmbroker.manager.impl;

import java.util.Date;
import java.util.Map;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import eu.greencom.mgmbroker.manager.api.CommunicationLayerMGR;

public class CommunicationLayerCommandProvider implements CommandProvider {

	private CommunicationLayerMGR communicationLayerMGR;

	public void bindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {
		this.communicationLayerMGR = communicationLayerMGR;
	}

	public void unbindCommunicationLayerMGR(CommunicationLayerMGR communicationLayerMGR) {//NOSONAR squid:S1172 - JPU: Parameter required by SCR
		this.communicationLayerMGR = null;
	}

	@Override
	public String getHelp() {
		return "---MGMBroker Manager Communication Layer commands---\n"
				+ "\tgetGateways - Prints a list of all gateways together with the latest timestamp of the received \"alive\"message\n";
	}

	public Object _getGateways(CommandInterpreter ci) {//NOSONAR squid:S00100 - JPU: Special purpose method (name)
		Map<String, Date> gateways = communicationLayerMGR.getGateways();
		ci.println(gateways.size() + " gateways available");
		ci.println("-----------------------------------------------");
		for (Map.Entry<String, Date> entry : gateways.entrySet()) {
			String gw = entry.getKey();
			ci.println("- " + gw + " : " + gateways.get(gw).toString());
		}
		ci.println("-----------------------------------------------");

		return null;

	}

}
