package eu.greencom.mgmbroker.manager.api;

import java.util.Map;

public interface SystemHealthMonitor extends Runnable {

	public Map<String, String> getInstallations();

	public void sendReport();
}
