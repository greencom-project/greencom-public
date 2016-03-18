package eu.greencom.xgateway.integrationlayer.impl;

import eu.greencom.xgateway.integrationlayer.api.service.MessageListener;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing an asynchronous exchange of text data (JSON-RPC messages)
 * from and to a serial device.
 */
public class SerialAdapter implements SerialPortEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(SerialAdapter.class.getName());

    // Default for 8-bit communication
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");

    // Number of messages to queue
    private static final int BUFFER_CAPACITY = 50;

    // Worker thread sleep interval
    private static final int THREAD_INTERVAL = 200;

    // Buffer used to (asynchronously) pass data to serial device
    private Buffer toSerial = (Buffer) BufferUtils.synchronizedBuffer(new CircularFifoBuffer(BUFFER_CAPACITY));

    // Buffer used to (asynchronously) read from serial device
    private Buffer fromSerial = (Buffer) BufferUtils.synchronizedBuffer(new CircularFifoBuffer(BUFFER_CAPACITY));

    // Thread extracting and re-posting messages from the "fromSerial" buffer
    private Thread readerThread;

    // Thread extracting and writing messages from the "toSerial" buffer
    private Thread writerThread;

    // Flag indicating the overall activity status (threads)
    private boolean active = true;

    // True, when reading from serial, otherwise false (safe to write)
    private boolean isReading = false;

    // True when the output buffer is empty, partial condition for writing.
    private boolean isOutputBufferEmpty = true;

    // Underlying SerialPort implementation
    private SerialPort serialPort;

    // Milliseconds to block while waiting for port open
    private static final int TIME_OUT = 2000;

    private String portName;

    private BufferedReader input;

    private BufferedWriter output;

    /**
     * The set of {@link MessageListener}s that are notified of new events.
     */
    private Set<MessageListener> listeners = new HashSet<MessageListener>();

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Creates a new SerialAdapter instance using the given port name.
     * 
     * @param portName
     * @param baudRate
     */
    public SerialAdapter(int baudRate , String portName) {
        if (portName == null || "".equals(portName)) {
            throw new IllegalArgumentException("Parameter portName must be provided!");
        }
        this.portName = portName;
        LOG.info("port info is: " + this.portName);

        CommPortIdentifier portId = null;
        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
        } catch (NoSuchPortException e) {
            LOG.error(SerialAdapter.class.getName() + ": " + e, e.getMessage());
            throw new IllegalArgumentException("Port " + portName + " not found");
        }
        if (portId == null) {
            throw new IllegalArgumentException("Could not open ComPort " + portName);
        }
        try {
            // Open port, app name (client) == class name
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            serialPort.setDTR(true);
            serialPort.setRTS(true);

            // set port parameters
            serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), CHARSET));
            output = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream(), CHARSET));

            // Explicit event type subscription required
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnOutputEmpty(true);

        } catch (Exception e) {
            LOG.error(SerialAdapter.class.getName() + ": " + e, e.getMessage());
        }

       

    }

    
    public void writeThread(){

        // Start writer thread writing to serial when possible
        writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (active) {
                    if (!toSerial.isEmpty() && !isReading && isOutputBufferEmpty) {
                        String message = null;
                        synchronized (toSerial) {
                            message = (String) toSerial.get();
                            toSerial.remove();
                        }
                        if (message != null) {
                            doWrite(message);
                        }
                    }
                    try {
                        Thread.sleep(THREAD_INTERVAL);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }} }
        });
        writerThread.start();
    }
    
    public void readThread(){
        // Start reader thread publishing buffer contents
        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (active) {
                    if (!fromSerial.isEmpty()) {
                        String message = null;
                        synchronized (fromSerial) {
                            message = (String) fromSerial.get();
                            fromSerial.remove();
                        }
                        if (message != null) {
                            publishSensorEvent(message);
                        }
                    }
                    try {
                        Thread.sleep(THREAD_INTERVAL);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }}}
        });
        readerThread.start();
    }
    
    /**
     * Provides text content to asynchronously write to serial port.
     * 
     * @param line
     */
    @SuppressWarnings("unchecked")
    public void writeToSerial(String line, boolean immediately) {
        if (immediately) {
            // Don't buffer
            doWrite(line);
        } else {
            if (line != null) {
                toSerial.add(line);
            }
        }
    }

    private void doWrite(String line) {
        try {
            isOutputBufferEmpty = false;
            output.write(line);
            output.flush();
            LOG.debug("Written to serial: {}", line);
        } catch (IOException e) {
            LOG.error(SerialAdapter.class.getName() + ": " + e, e.getMessage());
        } finally {
            // Should have been reset by OUTPUT_BUFFER_EMPTY event
            // Commented-out: isOutputBufferEmpty = true
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        // Indicate writing is o.k.
        // ff: switch case changed to if clauses
        if (oEvent.getEventType() == SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
            isOutputBufferEmpty = true;
        } else if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = null;
                isReading = true;
                /*
                 * Reading line-wise from Reader
                 */
                if (input.ready()) {
                    inputLine = input.readLine();
                }
                /*
                 * Upon a time there was a strange BufferOverflowException,
                 * apparently due to missing synchronization since ring buffer
                 * may not overrun!
                 */
                if (inputLine != null) {
                    fromSerial.add(inputLine);
                }

            } catch (Exception e) {
                LOG.error(SerialAdapter.class.getName() + ": " + e, e.getMessage());
            } finally {
                isReading = false;
            }

        }

    }

    private void publishSensorEvent(String value) { // NOSONAR squid :
                                                    // UnusedPrivateMethod ff:
                                                    // intended for something
        for (MessageListener listener : listeners) {
            try {
                LOG.debug("SerialAdapter sending: {}", value);
                listener.receive(value);
            } catch (Exception e) {
                LOG.error(SerialAdapter.class.getName() + "Failed to publish serial message {}: {}" + e, value, e.getMessage());
            }
        }
    }

    private void flush() {
        try {
            output.flush();
        } catch (IOException e) {
            LOG.error(SerialAdapter.class.getName() + ": " + e, e.getMessage());
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    private synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    protected void closeSerialConnection() {
        // Stop threads
        active = false;
        flush();
        close();
    }

}
