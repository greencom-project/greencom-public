package eu.greencom.mgmbroker.manager.impl;

import java.util.Map;
import java.util.TimerTask;

import eu.greencom.mgmbroker.manager.api.InstallationsChecker;

public class MailSenderTask extends TimerTask {

	private InstallationsChecker installationsChecker;
	private Map<String, String> gatewaysInstallations;
	private String toAddress;

	public MailSenderTask(InstallationsChecker installationsChecker, Map<String, String> gatewayInstallations,
			String toAddress) {
		this.installationsChecker = installationsChecker;
		this.gatewaysInstallations = gatewayInstallations;
		this.toAddress = toAddress;
	}

	@Override
	public void run() {
		installationsChecker.sendReport(toAddress, gatewaysInstallations);
	}

}
