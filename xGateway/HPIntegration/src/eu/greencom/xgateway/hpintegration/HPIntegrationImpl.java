package eu.greencom.xgateway.hpintegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import eu.greencom.xgateway.integrationlayer.api.service.DeviceController;
import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXPort;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterResponse;
import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.util.SerialParameters;
import pojo.Const;

////////////////////////////////////	//////////////////	////////////////////////////////////////////////////////
///Singleton class to interact with HeatPumnp via Modbus RTU:
///Public API: Serialcomm getInstance()                               <SINGLETON CLASS> 
///Public API: int[] sendWrite(int ref, int value) ref = Memory address <SYNCHRONIZED>
///Public API: int[] sendRead(int ref) ref = Memory address             <SYNCHRONIZED>
//////////////////	//////////////////	//////////////////	////////////////////////////////////////////////////////
/**
 * @author Michele Ligios Singleton implementation class of a Serial
 *         Communication manager for exchange data with Heat Pump via Modbus
 *         RTU.
 */
public class HPIntegrationImpl implements HPIntegrationService, DeviceController{

	private static final Logger LOG = Logger.getLogger(HPIntegrationImpl.class.getName());
	private EventAdmin eventAdmin;
	private ComponentContext context;
	private ConfigurationAdmin configurationAdmin;
	public static final String PID = "HPIntegration";
	private ServiceRegistration<ManagedService> configService;

	public static String resourcesrootfolder = null;
	public static String devId = null;
	public static String prodId = null;
	public static String portname = null;

	public static String debugMode = null;
	/* ------------------------------------------------------- */
	static public SerialParameters params = null;
	static public SerialConnection con = null;
	/* ------------------------------------------------------- */

	/* The important instances of the classes mentioned before */
	static ModbusSerialTransaction trans = null; // the transaction
	/* ------------------------------------------------------- */
	static ReadInputRegistersRequest reqSI = null; // the request
	static ReadInputRegistersResponse resSI = null; // the response
	static ReadMultipleRegistersRequest reqMI = null; // the request
	static ReadMultipleRegistersResponse resMI = null; // the response
	/* ------------------------------------------------------- */
	static WriteSingleRegisterRequest reqSO = null; // the request
	static WriteSingleRegisterResponse resSO = null; // the response
	static WriteMultipleRegistersRequest reqMO = null; // the request
	static WriteMultipleRegistersResponse resMO = null; // the response
	/* ------------------------------------------------------- */
	static Register reg[] = null;
	private static Scanner scanner;
	/* ------------------------------------------------------- */
	static Charset charset = null;
	static Path p = null;
	static BufferedWriter writer = null;
	/* ------------------------------------------------------- */
	private static int totalCounter = 0;
	private static int countRegister = 1;

	////////////////// ////////////////// ////////////////// //////////////////
	// ******************************************************

	public void activate(ComponentContext context) {
		this.context = context;
		System.out.println("HP integraion Service Starting....");

		System.setProperty("net.wimpi.modbus.debug", "true");



		Thread t = new Thread(new MyLittleThread());
		t.start();
	}

	public class MyLittleThread implements Runnable {

		@Override
		public void run() {

			HPIntegrationImpl.setup();
			System.out.println("Thread is finished");
		}

	}



	public void deactivate(ComponentContext context) {
		this.context = null;
		System.out.println("HP integraion Service Stopping....");

		terminate();
	}

	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void unbindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = null;
		LOG.info("UnbindConfiguration " + configurationAdmin.toString());
	}

	// ***************************************************************************
	// ***************************************************************************
	/////////////////////////////////////////////////////////////////////////////////////
	public static void main() {
		try {
			/* Variables for storing the parameters */
			int unitid = Const.defaultUnitID; // the unit identifier we will be
												// talking to
			int ref = Const.addressOffsetDef; // the reference address, where to
												// start reading from
			int repeat = Const.repeatDefault; // a loop for repeating the
												// transaction
			byte buf[] = new byte[Const.buffer_length];

			reg = new Register[Const.endpointSize];

			if (Const.storeToFile)
				if (!initDataStorage())
					return;

			do {
				// Request
				System.out.println("Inserire l'indirizzo del registro desiderato: [-1 per uscire]:");
				scanner = new Scanner(System.in);
				String inputString = scanner.nextLine();
				try {
					ref = Integer.parseInt(inputString);
				} catch (Exception ex) {
					ex.printStackTrace();
					scanner.close();
					LOG.info("Error during parsing address value()!");
					return;
				}

				if (ref == Const.endMenu)
					break;

				int counter = 0;
				do {
					sendCommand(Const.readmultiple, ref, reg); // Send Request &
																// collect
																// Response
					counter++;
					Thread.sleep(Const.repeatDelay); // Wait for repeatDelay ms
				} while (counter < Const.repeatDefault);

			} while (ref != Const.endMenu);

			terminate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}// main

	/////////////////////////////////////////////////////////////////////////////////////
	private static String getPort() {
		String Port = null;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec("ls -al /dev/ | grep ttyUSB | awk '{print $10}'");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BufferedReader read = new BufferedReader(new InputStreamReader(p.getInputStream()));

		try {
			Port = read.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/dev/" + Port;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	public static void setup() {
		// if (Const.debug) {
		// String prop = System.getProperty("java.library.path");
		// LOG.info("java.library.path: " + prop);
		// }
		System.out.println("inside the setup");

		// portname = getPort();
		portname = "/dev/ttyUSB0";
		// portname = "/dev/ttyS1";
		// 3. Setup serial parameters
		params = new SerialParameters();
		params.setPortName(portname);
		params.setBaudRate(Const.baudrate);
		params.setDatabits(Const.databits);
		params.setParity(Const.modbusParity);
		params.setStopbits(Const.stopbits);
		params.setEncoding(Const.modbusMode);
		params.setEcho(false);
		params.setReceiveTimeout(3000);

		// if (Const.debug)
		// printSerialParams();

		// 4. Open the connection

		System.out.println("start of serial");
		con = new SerialConnection(params);
		if (Const.debug)
			LOG.info("Serial Connection [con] instantiated");

		try {
			con.open();
		} catch (Exception e) {
			LOG.info("Serial Connection [open] fails");
			System.out.println("Serial Connection [open] fails" + e);
			e.printStackTrace();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// count implies the number of bytes to read
	private static void setupSingleRequestRead(int ref, int count, int unitid) {
		if (Const.debug) {
			LOG.info("**** setupSingleRequestRead Parameters   ****");
			LOG.info("ref    is " + ref);
			LOG.info("count  is " + count);
			LOG.info("unitid is " + (byte) unitid);
			LOG.info("****-------------------------------------****");
		}
		// 5. Prepare a request
		reqSI = new ReadInputRegistersRequest(ref, count);
		reqSI.setUnitID(unitid);
		reqSI.setHeadless();

		// 6. Prepare a transaction
		trans = new ModbusSerialTransaction(con);
		// Set Delay? Because of simulation & virtual ports? 50?
		trans.setTransDelayMS(Const.transactionDelay);

		trans.setRequest(reqSI);

	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static void setupMultipleRequestRead(int cmd, int ref, int unitid) {
		int count = 1;
		if (Const.debug) {
			LOG.info("**** setupMultipleRequestRead Parameters   ****");
			LOG.info("ref    is " + ref);
			LOG.info("count  is " + count);
			LOG.info("unitid is " + (byte) unitid);
			LOG.info("****-------------------------------------****");
		}
		// 5. Prepare a request
		reqMI = new ReadMultipleRegistersRequest(ref, count);
		reqMI.setUnitID(unitid);
		reqMI.setHeadless();

		// 6. Prepare a transaction
		trans = new ModbusSerialTransaction(con);

		// Set Delay? Because of simulation & virtual ports? 50? 120?
		trans.setTransDelayMS(Const.transactionDelay * 5);

		trans.setRequest(reqMI);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static void setupSingleRequestWrite(int ref, Register reg, int unitid) {
		// 5. Prepare a request
		reqSO = new WriteSingleRegisterRequest(ref, reg);
		reqSO.setUnitID(unitid);
		reqSO.setHeadless();

		// 6. Prepare a transaction
		trans = new ModbusSerialTransaction(con);

		// Set Delay? Because of simulation & virtual ports? 50? 120?
		trans.setTransDelayMS(Const.transactionDelay);

		trans.setRequest(reqSO);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static void setupMultipleRequestWrite(int ref, Register[] registers, int unitid) {
		// 5. Prepare a request
		reqMO = new WriteMultipleRegistersRequest(ref, registers);
		reqMO.setUnitID(unitid);
		reqMO.setHeadless();

		// 6. Prepare a transaction
		trans = new ModbusSerialTransaction(con);

		// Set Delay? Because of simulation & virtual ports? 50? 120?
		trans.setTransDelayMS(Const.transactionDelay * 5);

		trans.setRequest(reqMO);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Write Register [PUBLIC API]: ref = Memory address
	/////////////////////////////////////////////////////////////////////////////////////
	public synchronized boolean sendWrite(int ref, int value) {
		int unitid = Const.defaultUnitID;
		int[] res = null;
		System.out.println("Writting parameter: " + ref + " with value " + value );
		LOG.debug("Writting parameter: " + ref + " with value " + value );
		
		/// TODO: CHECK because of possible conversion error!
		/// TODO: based on the Register -> find its type -> put inside the right
		/// register size
		/// Just because of the parameter of MultipleRequestWrite

		// Register tmp = Register.setValue(value);
		// tmp.setValue(value);

		// Register regs[] = new Register[1];
		// regs[0].setValue(value);
		SimpleInputRegister tmp = new SimpleInputRegister(value);
		// SimpleInputRegister tmp2 = new SimpleInputRegister(0);
		Register regs[] = new Register[] { tmp };
		// regs[0].setValue(value);

		if (Const.debug)
			LOG.info("Value to send: " + value + " = " + regs[0].getValue());

		setupMultipleRequestWrite(ref, regs, unitid);

		res = runNreceive(Const.setMultiple);

		if (res != null)
			return true;

		return false;

	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Read Register [PUBLIC API]: ref = Memory address
	/////////////////////////////////////////////////////////////////////////////////////
	public synchronized int sendRead(int ref) {
		int unitid = Const.defaultUnitID;
		int[] res = null;

		if (Const.debug)
			LOG.info("Register to read: " + ref);

		// setupSingleRequestRead(Const.getSingle, ref, unitid);
		setupMultipleRequestRead(Const.getMultiple, ref, unitid);

		// res = runNreceive(Const.getSingle);
		res = runNreceive(Const.getMultiple);

		return res[0];
	}

	/////////////////////////////////////////////////////////////////////////////////////
	public static synchronized int[] sendCommand(int cmdtype, int ref, Register[] reg) {
		int unitid = Const.defaultUnitID;
		int[] res = null;
		switch (cmdtype) {
		case Const.getSingle: {
			setupSingleRequestRead(ref, countRegister, unitid);
			res = runNreceive(cmdtype);
		}
			break;
		case Const.getMultiple: {
			setupMultipleRequestRead(ref, countRegister, unitid);
			res = runNreceive(cmdtype);
		}
			break;
		case Const.setSingle: {
			if (reg.length <= 0) {
				LOG.warn("Parameter [Register] Error");
				terminate();
				System.exit(1);
			}
			setupSingleRequestWrite(ref, reg[0], unitid);
			res = runNreceive(cmdtype);
		}
			break;
		case Const.setMultiple: {
			if (reg.length <= 0) {
				LOG.warn("Parameter [Register] Error");
				terminate();
				System.exit(1);
			}
			setupMultipleRequestWrite(ref, reg, unitid);
			res = runNreceive(cmdtype);
		}
			break;
		default: {
			LOG.warn("Trying to send a message with a command type not recognized! [" + cmdtype + "]");
		}
			break;
		}

		return res;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static int[] runNreceive(int cmdtype) {
		int[] results = null;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			trans.execute();
		} catch (ModbusException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}

		switch (cmdtype) {
		case Const.readsingle: {
			resSI = (ReadInputRegistersResponse) trans.getResponse();
			results = new int[resSI.getWordCount()];
			for (int n = 0; n < resSI.getWordCount(); n++) {
				if (Const.debug)
					LOG.info("Word " + n + "=" + resSI.getRegisterValue(n));

				results[n] = resSI.getRegisterValue(n);
			}
		}
			break;
		case Const.readmultiple: {
			resMI = (ReadMultipleRegistersResponse) trans.getResponse();
			results = new int[resMI.getWordCount()];
			for (int n = 0; n < resMI.getWordCount(); n++) {
				if (Const.debug)
					LOG.info("Word " + n + "=" + resMI.getRegisterValue(n));

				results[n] = resMI.getRegisterValue(n);
			}
		}
			break;
		case Const.writesingle: {
			resSO = (WriteSingleRegisterResponse) trans.getResponse();
			results = new int[1];
			if (Const.debug)
				LOG.info("Register " + "=" + resSO.getRegisterValue());
			results[0] = resSO.getRegisterValue();
		}
			break;
		case Const.writemultiple: {
			int offset = 0;
			resMO = (WriteMultipleRegistersResponse) trans.getResponse();
			results = new int[resMO.getWordCount()];
			offset = resMO.getDataLength() / resMO.getWordCount();
			for (int n = 0; n < resMO.getWordCount(); n++) {
				results[n] = resMO.getDataLength();
				if (Const.debug)
					LOG.info("Reference about Register #" + n + " with offset[" + (n * offset) + "]= "
							+ resMO.getReference() + (n * offset));
			}
		}
			break;
		}

		// collectData(results);
		if (results != null) {
			System.out.println("The return result is not null");
		}
		return results;

	}

	private static boolean initDataStorage() {
		if (Const.storeToFile) {
			// file
			p = Paths.get("./" + Const.fileNameDef);
			charset = Charset.forName("US-ASCII");
			try {
				writer = Files.newBufferedWriter(p, charset);
			} catch (IOException e) {
				e.printStackTrace();
				LOG.warn("Storage error");
				Const.storeToFile = false;
				return false;
			}
		} else {
			if (Const.debug)
				LOG.info("Output File not set!");
		}

		return true;
	}

	private static void collectData(int[] results) {
		String s = "Modbus packet [" + Integer.toString(totalCounter) + "]: ";
		for (int i = 0; i < results.length; i++)
			s += Integer.toString(results[i]) + " ";

		if (Const.storeToFile) {
			// file
			try {
				writer.write(s, 0, s.length());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
				LOG.warn("Write Fails!");
			}
		} else {
			if (Const.debug)
				LOG.info("Packet Content: " + s);
		}

		try {
			writer.flush();
		} catch (IOException e) {
			LOG.warn("Flush Fails!");
			e.printStackTrace();
		}

		totalCounter++;

	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static void terminate() {

		if (Const.storeToFile) {
			if (Const.debug)
				LOG.info("Terminating Modbus Communication");

			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 8. Close the connection
		con.close();
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// type = type of REQUEST
	/// ref = Register Address
	/// count = # of Register to Read/Write
	private static boolean parseRequest(int type, int ref, int count) {
		switch (type) {
		case Const.readsingle: {
			LOG.info("Read Single Register: [" + ref + "]");
		}
			break;
		case Const.readmultiple: {
			LOG.info("Read Multiple Register: #[" + count + "] from [" + ref + "]");
		}
			break;
		case Const.writesingle: {
			LOG.info("Write Single Register: [" + ref + "]");
		}
			break;
		case Const.writemultiple: {
			LOG.info("Write Multiple Register: #[" + count + "] from [" + ref + "]");
		}
			break;
		default: {
			LOG.info("Given command type is not correct: " + type);
			return false;
		}
		}

		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static String getType(int type) {
		String s = null;
		switch (type) {
		case Const.u8: {
			s = new String("Given type [" + type + "] is: uint8");
		}
			break;
		case Const.u16: {
			s = new String("Given type [" + type + "] is: uint16");
		}
			break;
		case Const.u32: {
			s = new String("Given type [" + type + "] is: uint32");
		}
			break;
		case Const.s8: {
			s = new String("Given type [" + type + "] is: signed8");
		}
			break;
		case Const.s16: {
			s = new String("Given type [" + type + "] is: signed16");
		}
			break;
		case Const.s32: {
			s = new String("Given type [" + type + "] is: signed32");
		}
			break;
		}

		return s;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	private static void printSerialParams() {
		LOG.info("**** Serial Parameters   ****");
		LOG.info("portname is " + portname);
		LOG.info("baudrate is " + Const.baudrate);
		LOG.info("databits is " + Const.databits);
		LOG.info("parity is " + Const.modbusParity);
		LOG.info("stopbits is " + Const.stopbits);
		LOG.info("modbus-mode is " + Const.modbusMode);
		LOG.info("**** ---------------------- ****");
	}
	/////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String get(String deviceId, String propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean put(String deviceId, String propertyId, String value) {
		// TODO Auto-generated method stub
		return false;
	}

}
