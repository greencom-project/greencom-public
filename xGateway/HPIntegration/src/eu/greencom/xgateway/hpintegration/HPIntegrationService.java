package eu.greencom.xgateway.hpintegration;



/**
 * @author Farmin, Michele
 * 
 * This is the Service for Integrating Heat Pump in the greencom project
 * using reading and writing parameters of the Heat Pump
 *
 */
public interface HPIntegrationService {
	
//	public HPIntegrationImpl getInstance();
	public int sendRead(int ref);
	public boolean sendWrite(int ref,int value);
	
}
