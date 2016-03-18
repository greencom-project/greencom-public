package eu.linksmart.network.jsonrpc.utils;

import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import eu.linksmart.network.Registration;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.utils.Part;

/**
 * A @CommandProvider which allows to query NetworkManager to get the list of
 * LinkSmart services
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class UtilCommandProvider implements CommandProvider {
	private IdentityManager identityManager;

	public void bindIdentityManager(IdentityManager identityManager) {
		this.identityManager = identityManager;
	}

	public void unbindIdentityManager(IdentityManager identityManager) {//NOSONAR squid:S1172
		this.identityManager = null;
	}

	public Object _getLSservices(CommandInterpreter ci) {//NOSONAR squid:S00100 - Special purpose method
		Set<Registration> regs = identityManager.getLocalServices();
		ci.print("Local Services:\n------------------\n");
		for (Registration r : regs) {
			ci.print("> Service with VA " + r.getVirtualAddress().toString() + " \n");
			for (Part p : r.getAttributes()) {
				ci.println("\t" + p.getKey() + ": " + p.getValue());
			}
		}

		regs = identityManager.getRemoteServices();
		ci.print("Remote Services:\n------------------\n");
		for (Registration r : regs) {
			ci.print("> Service with VA " + r.getVirtualAddress().toString() + " \n");
			for (Part p : r.getAttributes()) {
				ci.println("\t" + p.getKey() + ": " + p.getValue());
			}
		}
		return null;
	}

	@Override
	public String getHelp() {
		return "---MGMBrokerAPI commands---\n"
				+ "\t getLSservices - Prints all services known by this NetworkManager\n";
	}
}
