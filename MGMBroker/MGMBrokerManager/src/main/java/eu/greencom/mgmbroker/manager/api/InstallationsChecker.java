package eu.greencom.mgmbroker.manager.api;

import java.util.List;
import java.util.Map;

import eu.greencom.mgm.webapiconsumer.model.Sensor;

public interface InstallationsChecker {

	public List<Sensor> checkInstallation(String installationID);

	public void sendReport(String toAddress, Map<String, String> installations);
}
