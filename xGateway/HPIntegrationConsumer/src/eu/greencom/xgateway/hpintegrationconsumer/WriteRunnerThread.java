package eu.greencom.xgateway.hpintegrationconsumer;

import org.apache.log4j.Logger;
import org.osgi.service.event.EventAdmin;

import eu.greencom.xgateway.hpintegration.HPIntegrationService;

/**
 * @author Farmin Farzin
 *
 * This runnable will write the value given in Configuration Admin to the HP via modbus.
 * using HPIntegration service
 * 
 * This is NOT USING FOR CONTROLLING FROM DWH
 */
public class WriteRunnerThread implements Runnable {

	
	private static final Logger LOG = Logger.getLogger(WriteRunnerThread.class);
	HPIntegrationService hpservice;
	private int parameterToWrite;
	private int value;
	
	public WriteRunnerThread(HPIntegrationService hp , int parameter , int value) {
		this.hpservice = hp;
		this.parameterToWrite = parameter;
		this.value=value;
	}
	
	@Override
	public void run() {
		System.out.println("is going to Write...." + parameterToWrite + " value: " + value);
		
		//for negative offset
//		if (parameterToWrite == 47011 && value < 0){
//				value = -value;
//		value--;
//		value = value ^ 0x00FF;
//		}
		
		
		Boolean success = hpservice.sendWrite(parameterToWrite, value);
			if (success){
				System.out.println("write has been done successfully");
				LOG.debug("write has been done successfully");
			}else{
				System.out.println("write has been done UNsuccessfully");
				LOG.debug("write has been done UNsuccessfully");
			}
	}
	
	public void stop() {

	}
	


}
