package eu.greencom.mgmbroker.manager.api;

public interface ControlLayerMGR {

	public void changeDeviceState(String gatewayID, String deviceID, boolean state);
}
