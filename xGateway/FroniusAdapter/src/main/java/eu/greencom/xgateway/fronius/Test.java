package eu.greencom.xgateway.fronius;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class Test {

	public static void main(String[] args) throws ExecuteException, IOException {
		//String command ="sudo arp-scan --interface=eth0 --localnet -t 1000";
		String command = "/home/pullmann/test";
		
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    CommandLine commandline = CommandLine.parse(command);
	    DefaultExecutor exec = new DefaultExecutor();
	    // Does not work - can't stop process
	    ExecuteWatchdog watchdog = new ExecuteWatchdog(100);
	    exec.setWatchdog(watchdog);
	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
	    exec.setStreamHandler(streamHandler);
	    exec.execute(commandline);
	    System.out.println(outputStream.toString());
	}

}
