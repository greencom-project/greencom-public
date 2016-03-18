package eu.greencom.xgateway.integrationlayer.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.greencom.mgm.metainformationstore.api.domain.Graph;
import eu.greencom.mgm.metainformationstore.api.domain.PredicateValue;
import eu.greencom.mgm.metainformationstore.api.domain.PredicateValue.ValueType;
import eu.greencom.mgm.metainformationstore.api.domain.ResourceGraph;
import eu.greencom.xgateway.integrationlayer.JsonRpcSampledValue;
import eu.greencom.xgateway.integrationlayer.api.service.IntegrationLayer;
import eu.greencom.xgateway.integrationlayer.api.service.MessageListener;
import eu.greencom.xgateway.localdatastorage.api.service.LocalDataStorage;

/**
 * Implementation of the main integration layer interface
 * {@link IntegrationLayer}. Acts itself as {@link MessageListener} to content
 * received from serial port. Processing of messages being exchanged is either
 * exclusively handled by local message handlers (registration) or passed over
 * to external {@link MessageListener}s. Main configuration parameters (port
 * name, baud rate) are provided as component properties.
 * 
 * TODO: Handle external device list requests
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SerialCommunicationAdapter implements MessageListener, IntegrationLayer {

    /**
     * Service PID used to identify the component and CM configuration.
     */

    public static final String SERVICE_PID = "integration-layer";

    private static final String JSON_COLLECTION = "IL";

    private static final String JSON_OBJECT_ID = "IL_DEVICE_MAP";

    // ff: some Strings created
    private static final String EXTERNAL_IDENTIFIER = "gc:externalIdentifier";
    private static final String HASUNIT = "gc:hasUnit";
    private static final String PARAMS = "params";
    private static final String RDFTYPE = "rdf:type";
    private static final String RDFVALUE = "rdf:value";
    private static final String RESULT = "result";
    private static final String TEMP = "temp:";
    private static final String XSDSTRING = "xsd:string";   
    private static final int MNUM5 = 5 ;


    private static final Logger LOG = LoggerFactory.getLogger(SerialCommunicationAdapter.class.getName());

    // Null-offset time-zone used to parse and format date strings
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    // Format does not specify explicit time-zone since appended as string "Z"
    //ff changed from static to non-static
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    // Interpret dates as UTC
     
       
    

    // Executes scheduled tasks: update of the reference time on coordinator
    // private Timer timer new Timer()

    private ObjectMapper mapper = new ObjectMapper();

    private   MessageUtil util = new MessageUtil(); //NOSONAR squid : S1948 ff: this is actually seriallizable

    private   Set<MessageListener> listenerSet = new HashSet<MessageListener>();

    private  ConfigurationAdmin configAdmin;

    // Maps request IDs to local message handlers
    private Map<String, MessageListener> msgHandler = new HashMap<String, MessageListener>();

    // URI of the session created and returned from coordinator mote.
    private String sessionURI = null;

    // Low-level serial connector.
    private   SerialAdapter serialAdapter;

    private  LocalDataStorage dataStorage;

    private  EventAdmin eventAdmin;

    // Did not work with WebConsole and Equinox 3.9.1, removed
    // private LogService logService

    boolean registered = false;

    private long timeUpdatePeriod;

    private long initDelayPeriod;

    private String portName;

    private int baudRate;

    private Graph deviceGraph = new Graph();

    private Graph observationGraph = new Graph();

    private class ObservationListener implements MessageListener {
        @Override
        public void receive(String message) {
            LOG.debug("Received message {}", message);
            JsonNode msg = util.parseJson(message);
            if (msg == null) {
                return;
            }
            try {
                // Handle/forward observation message, resolve property URI
                if (msg.has("method") && "POST".equals(msg.get("method").asText()) && "gc:Observation".equals(msg.get(PARAMS).get("uri").asText())) {

                    // External sensor ID (foreign key)
                    String externalSensorId = msg.get(PARAMS).get("data").get(EXTERNAL_IDENTIFIER).asText();

                    // Convert null-padded string containing double value
                    double value = msg.get(PARAMS).get("data").get(RDFVALUE).asDouble();

                    /*
                     * Update: after observing considerable deviation from NTP
                     * synchronized time we don't use the coordinator time, but
                     * gateway time instead.
                     */
                    String dt = getReferenceTimestamp();

                    // Update observation graph: uri extends sensor uri
                    String observationUri = TEMP + externalSensorId + "_value";
                    if (!observationGraph.containsKey(observationUri)) {
                        observationGraph.put(observationUri, new ResourceGraph());
                    }
                    ResourceGraph observation = observationGraph.get(observationUri);
                    observation.set(RDFTYPE, new PredicateValue("gc:Observation"));
                    observation.set(RDFVALUE, new PredicateValue(value + "", ValueType.literal, "xsd:double"));
                    observation.set("gc:observationResultTime", new PredicateValue(dt + "", ValueType.literal, "xsd:dateTimeStamp"));
                    // Sensor
                    observation.set("gc:observedBy", new PredicateValue(TEMP + externalSensorId));

                    // Prepare event data
                    Calendar time = Calendar.getInstance(UTC);
                    // Create legacy JsonRpcSampledValue data wrapper
                    // TODO: consider mapping JSON properties directly on Event
                    JsonRpcSampledValue sample = new JsonRpcSampledValue();
                    sample.setSent(false);
                    sample.setDeviceID(externalSensorId);
                    // Not used:
                    sample.setTimeSeriesID("");
                    sample.setValue(value);
                    sample.setTimestamp(time.getTimeInMillis());

                    // Send out event
                    Map<String, Object> prop = new HashMap<String, Object>();
                    prop.put("sampledValue", sample);
                    prop.put("sampledValueString", msg);
                    eventAdmin.postEvent(new Event("eu/greencom/sensordata", prop));

                    return;
                }
            } catch (Exception e) {
                LOG.error(SerialCommunicationAdapter.class.getName() + ": " + e, e.getMessage());
            }
        }
    }

    protected void setEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    protected void removeEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = null;
        // ff
        LOG.info("removeEventAdmin in serial CommunicationAdapter" + eventAdmin.toString());

    }

    protected void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    protected void removeConfigurationAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = null;
        // ff
        LOG.info("removeConfigurationAdmin in serial CommunicationAdapter" + configAdmin.toString());
    }

    protected void setDataStorage(LocalDataStorage storage) {
        this.dataStorage = storage;
    }

    protected void removeDataStorage(LocalDataStorage storage) {
        this.dataStorage = null;
        // ff
        LOG.info("removeDataStorage in serial CommunicationAdapter" + storage.toString());
    }

    @SuppressWarnings("unchecked")
    protected void activate(ComponentContext context) {
        LOG.debug("Activating {}", getClass().getSimpleName());
        configure(context.getProperties());
        start();
        LOG.debug("Activated {}", getClass().getSimpleName());
    }

    @SuppressWarnings("unchecked")
    protected void modified(ComponentContext context) {
        /*
         * Ignore runtime configuration changes - modified() prevents restarting
         * the component. Changed configuration will be loaded on activate().
         */
        LOG.debug("IntegrationLayer configuration modified:");
        printConfiguration(context.getProperties());
    }

    protected void deactivate(ComponentContext context) { // NOSONAR squid :
                                                          // S1172 ff: this is
                                                          // standard
        unregister();

        // Shut-down serial connection
        if (serialAdapter != null) {
            serialAdapter.closeSerialConnection();
            serialAdapter = null;
        }

        LOG.debug(getClass().getSimpleName() + " deactivated " + (isRegistered() ? " and still registered" : " and unregistered"));
    }

    private void start() {
        serialAdapter = new SerialAdapter( baudRate ,portName);
        //ff made 2 threads read and write in Serial Adapter out of the constructor
        serialAdapter.readThread();
        serialAdapter.writeThread();
        serialAdapter.addListener(this);
        // Delay registration message otherwise port/comm library not ready
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                register();
            }
        }, initDelayPeriod);
        addMessageListener(new ObservationListener());
    }

    private void printConfiguration(Dictionary<String, ?> d) {
        Enumeration<String> e = d.keys();
        while (e.hasMoreElements()) {
            String k = e.nextElement();
            LOG.debug('\t' + k + ": " + d.get(k));
        }
    }

    private void configure(Dictionary<String, ?> d) {
        Configuration config = null;
        // Initiate persistent configuration
        try {
            config = configAdmin.getConfiguration(SERVICE_PID);
        } catch (Exception e) {
            LOG.error(SerialCommunicationAdapter.class.getName() + ": " + e, e.getMessage());
        }
        // For the first time there are no properties
        if (config != null && config.getProperties() == null) {
            LOG.debug("IntegrationLayer configuration initialized:");
            try {
                config.update(d);
            } catch (Exception e) {
                LOG.error(SerialCommunicationAdapter.class.getName() + ": " + e, e.getMessage());
            }
        } else {
            // Context properties have been updated/overridden by CM
            LOG.debug("IntegrationLayer configuration loaded:");
        }
        printConfiguration(d);
        portName = (String) d.get("port.name");
        baudRate = (Integer) d.get("port.baudrate");
        timeUpdatePeriod = (Long) d.get("timer.updateperiod");
        // ff: log it just to not be unused
        LOG.info("timeUpdatePeriod is: " + timeUpdatePeriod);

        initDelayPeriod = (Long) d.get("init.delayperiod");
    }

    @Override
    public void addMessageListener(MessageListener listener) {
        listenerSet.add(listener);
    }

    @Override
    public void removeMessageListener(MessageListener listener) {
        listenerSet.remove(listener);
    }

    protected void notifyListeners(String message) {
        for (MessageListener listener : listenerSet) {
            listener.receive(message);
        }
    }

    // Assigns one-shot local message handler. After being invoked the handler
    // is removed from the map. Used by serial console
    @Override
    public void setMessageHandler(MessageListener handler, String msgId) {
        msgHandler.put(msgId, handler);
    }

    void removeMessageHandler(String msgId) {
        msgHandler.remove(msgId);
    }

    @Override
    public void restart() {
        send(util.getTemplate("notification.restart"));
    }

    // Obsolete - observation time being now set by IL
    // To be called on timer base when isRegistered() == true
    // @Override
    // public void updateReferenceTimestamp()

    @Override
    public boolean isRegistered() {
        return sessionURI != null;
    }

    @Override
    public void register() {
        if (!isRegistered()) {
            String msgId = util.generateRequestId();
            // Add response handler
            setMessageHandler(new MessageListener() {
                @Override
                public void receive(String message) {
                    JsonNode msg = util.parseJson(message);
                    try {
                        sessionURI = msg.get(RESULT).get("uri").textValue();
                        LOG.debug("Session registered:\t" + getSessionURI());

                        // This is Comment: Device list loaded initially
                        requestDeviceData();
                    } catch (Exception e) {
                        LOG.error(SerialCommunicationAdapter.class.getName() + ": " + e, e.getMessage());
                    }
                }
            }, msgId);
            // Send registration request
            send(util.fillTemplate("request.create.session", msgId, getReferenceTimestamp()));
        }
    }

    @Override
    public void unregister() {
        if (isRegistered()) {

            // Let coordinator know of IL shutting-down immediately
            send(util.fillTemplate("request.delete.session", getSessionURI()), true);
            sessionURI = null;
            registered = false;
        }

    }

    int errorCount = 0;
    int errorCountLimit = MNUM5;

    @Override
    // Process serial message
    public void receive(String message) {
        LOG.debug("Received message:\t'" + message + "'");

        // Try to clean-up messy timestamp
        // ff
        String newMessage = message.replaceAll("gc:observationResultTime(.*)rdf:value", RDFVALUE);

        LOG.debug("Processing message:\t'" + newMessage + "'");

        JsonNode msg = null;
        String msgId = null;

        // TODO: update firmware

        try {
            msg = mapper.readTree(newMessage);
        } catch (Exception e) {
            LOG.error(SerialCommunicationAdapter.class.getName() + "Failed to parse message: {}, {}" + e, newMessage, e.getMessage());
        }

        // Can't proceed with broken message, try to re-register
        if (msg == null) {
            ++errorCount;
            if (errorCount > errorCountLimit) {
                LOG.debug("Trying to renew registration");
                unregister();
                register();
                errorCount = 0;
            }
            return;
        }

        // Try to extract message ID, might be null for notification
        JsonNode n = msg.get("id");
        if (n != null) {
            msgId = n.asText();
        }

        // Try to allocate exclusive, local handler
        if (msgId != null && msgHandler.containsKey(msgId)) {
            MessageListener m = msgHandler.get(msgId);
            m.receive(newMessage);
            // Remove one-shot handler
            msgHandler.remove(msgId);
            // Prevent further processing
            return;
        }
        // Otherwise notify any external listener
        notifyListeners(newMessage);
    }

    @Override
    public void send(String message) {
        send(message, false);
    }

    public void send(String message, boolean immediately) {
        // ff
        String newMessage = message + '\n';
        LOG.debug("Sending:\t'" + newMessage + "'");
        // Add expected newline delimiter
        serialAdapter.writeToSerial(newMessage, immediately);
    }

    protected String getSessionURI() {
        return sessionURI;
    }

    protected String getReferenceTimestamp() {
        long now = System.currentTimeMillis();
        // TODO: Add further sanity check: prevent updating with past time
        
        // ff moved from initialization to method
        formatter.setTimeZone(UTC);
        return formatter.format(now) + "Z";
    }

    @Override
    public void requestDeviceData() {
        String msgId = util.generateRequestId();
        setMessageHandler(new MessageListener() {
            @Override
            public void receive(String message) {
                JsonNode msg = util.parseJson(message);
                if (msg != null) {
                    try {
                        // An array of device descriptions
                        updateDeviceList(msg.get(RESULT));
                    } catch (Exception e) {
                        LOG.error(SerialCommunicationAdapter.class.getName() + ": " + e, e.getMessage());
                    }
                }
            }
        }, msgId);
        send(util.fillTemplate("request.transducer.list", msgId));
    }

    private void updateDeviceList(JsonNode node) {
        deviceGraph = null;
        // Assume this is the bare list
        ArrayNode update = (ArrayNode) node;
        // Load current device list
        deviceGraph = util.readJson(getDeviceListObject(), Graph.class);
        // Initialize the first time
        if (deviceGraph == null) {
            deviceGraph = new Graph();
        }
        // Add missing device descriptions
        Iterator<JsonNode> i = update.iterator();
        while (i.hasNext()) {
            ObjectNode device = (ObjectNode) i.next();
            // temp:3315BC001A0018C5
            String uri = TEMP + device.get(EXTERNAL_IDENTIFIER).textValue();
            ResourceGraph res = deviceGraph.get(uri);
            if (res == null) {
                res = new ResourceGraph();
                deviceGraph.put(uri, res);
                // Mandatory members
                res.add(RDFTYPE, new PredicateValue(device.get(RDFTYPE).textValue()));
                res.add(EXTERNAL_IDENTIFIER, new PredicateValue(device.get(EXTERNAL_IDENTIFIER).textValue(), ValueType.literal, XSDSTRING));
                // Optional members
                if (device.has(HASUNIT)) {
                    res.add(HASUNIT, new PredicateValue(device.get(HASUNIT).textValue()));
                }
                // Initialize fields for manual annotation - to be updated via
                // UI
                // Real GC URI of this sensor
                // res.add("rdf:about", new PredicateValue("Uri?"))
                // URI of the property being observed - used as time-series
                // identifier
                // res.add("gc:observedProperty", new
                // PredicateValue("Property?"))

                res.add("rdfs:label", new PredicateValue("Label?", ValueType.literal, XSDSTRING, "en"));
                res.add("rdfs:comment", new PredicateValue("Description?", ValueType.literal, XSDSTRING, "en"));
            }
        }
        storeDeviceGraph();
        LOG.debug("Current device list: " + deviceGraph);
    }

    // Intended to resolve the GC URI of the sensor
    private String resolvePropertyUri(String sensorId) { // NOSONAR squid :
                                                         // UnusedPrivateMethod
                                                         // ff: intended for
                                                         // something
        return sensorId;
    }

    // Intended to resolve the GC URI of the observed property
    private String resolveSensorUri(String sensorId) { // NOSONAR squid :
                                                       // UnusedPrivateMethod
                                                       // ff: intended for
                                                       // something
        return sensorId;
    }

    @Override
    public Graph getDeviceData() {
        return deviceGraph;
    }

    private void storeDeviceGraph() {
        dataStorage.put(JSON_COLLECTION, util.writeJson(deviceGraph), JSON_OBJECT_ID);
    }

    private JsonNode getDeviceListObject() {
        // Don't use local version, may have changed externally
        if (dataStorage.exists(JSON_COLLECTION, JSON_OBJECT_ID)) {
            String s = dataStorage.getJson(JSON_COLLECTION, JSON_OBJECT_ID);
            ObjectNode json = (ObjectNode) util.parseJson(s);
            // _id is string, no ResourceGraph, breaks de-serialization
            json.remove("_id");
            return json;
        }
        return null;
    }

    @Override
    public Graph getObservationData() {
        return observationGraph;
    }

    @Override
    public void updateDeviceData(Graph updatedGraph) {
        if (updatedGraph != null) {
            deviceGraph = updatedGraph;
            storeDeviceGraph();
        }
    }
}
