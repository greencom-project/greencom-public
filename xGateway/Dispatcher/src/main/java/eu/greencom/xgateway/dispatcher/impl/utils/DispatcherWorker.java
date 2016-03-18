package eu.greencom.xgateway.dispatcher.impl.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.greencom.api.domain.SampledValue;
import eu.greencom.api.service.TimeSeriesManager;
import eu.greencom.mgm.webapiconsumer.api.service.WebApiConsumer;
import eu.greencom.xgateway.dispatcher.impl.service.Data;
import eu.greencom.xgateway.dispatcher.impl.service.JsonDatapoints;

public class DispatcherWorker extends TimerTask {

    private static final Logger LOG = Logger.getLogger(DispatcherWorker.class);
    // ff: I removed static for TimeSeriesManager
    private TimeSeriesManager timeSeriesManager;    
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
    private ObjectMapper mapper = new ObjectMapper();
    private WebApiConsumer webApiConsumer;
    private int limit;

    public DispatcherWorker(TimeSeriesManager timeSeriesManager, WebApiConsumer wac, int limit) {
        this.timeSeriesManager = timeSeriesManager;
        this.webApiConsumer = wac;
        this.limit = limit;
    }

    // Performs the blocking call to DB manager
    private Thread worker;

    @Override
    public void run() {
        try {
            // This should not happen, DispatcherImpl would have been reloaded
            if (timeSeriesManager == null) {
                LOG.error("Cannot read local db because the TIMESERIESMANAGER IS NULL!!!");
            } else {
                if (worker != null) {
                    // Interrupt a blocked call
                    if (!Thread.State.TERMINATED.equals(worker.getState())) {
                        worker.interrupt();
                        LOG.warn("DispatcherWorker thread forced to terminate!");
                    }
                    worker = null;
                }
                // Null initially or in subsequent call
                if (worker == null) {
                    worker = new Thread("DispatcherWorker") {
                        // Get unsent TimeSeries from TimeSeriesManager and send
                        // them to the MGMDataWareHouse
                        @Override
                        public void run() {
                            List<SampledValue> values = timeSeriesManager.getUnsentValues(limit);
                            // ff I change the values != tp is empty also removed
                            // values.size() > 0
                            if (!values.isEmpty()) {
                                LOG.info("There are values " + values.size() + " to be published");
                                List<SampledValue> sv = put(values);
                                LOG.debug(sv.size() + " values were successfully published. Updating local database");
                                timeSeriesManager.put(sv);
                            } else {
                                LOG.info("No data to publish");
                            }
                        }
                    };
                    worker.start();
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to run cleaner task: " + e);

        }
    }

    public List<SampledValue> put(List<SampledValue> values) {
        List<SampledValue> result = new LinkedList<SampledValue>();
        try {

            LOG.info("Going to store values in the MGMDataWareHouse");

            // Temporarily caches the last value time stamps per deviceID
            Map<String, Long> timestamps = new HashMap<String, Long>();

            // will contain a list of Data grouped by sensor id (it is used to
            // send values to the manager)
            Map<String, ArrayList<Data>> groupedValues = new HashMap<String, ArrayList<Data>>();
            // will contain a list (same as above) of SampledValue grouped by
            // sensor id (it is used to update values to the local data storage)
            Map<String, ArrayList<SampledValue>> groupedValuesOrig = new HashMap<String, ArrayList<SampledValue>>();

            // for each value received
            for (SampledValue v : values) {
                // get the device id
                String deviceID = v.getDeviceID();

                // try to get the list of values from the aggregated hash map
                List<Data> value = groupedValues.get(deviceID);
                // try to get the list of values from the aggregated hash map
                List<SampledValue> valueOrig = groupedValuesOrig.get(deviceID);

                // if there is no value yet for this device inside the map
                if (!groupedValues.containsKey(deviceID)) {
                    // put an entry for the manager
                    groupedValues.put(deviceID, new ArrayList<Data>());
                    // put an entry for the local data storage
                    groupedValuesOrig.put(deviceID, new ArrayList<SampledValue>());
                }
                // then retrieve to get the value list (data)
                value = groupedValues.get(deviceID);
                // then retrieve to get the value list (sampledvalue)
                valueOrig = groupedValuesOrig.get(deviceID);

                // Optionally gather last value's timestamp (other values of
                // this device will use a cached version of the last value
                // timestamp)
                if (!timestamps.containsKey(deviceID)) {
                    timestamps.put(deviceID, getLastValueTimestamp(deviceID));
                }

                Long timestamp = timestamps.get(deviceID);
                if (timestamp != null) {
                    // Add value when last timestamp missing or entry is newer
                    // than this

                    // add this value for further sending if it is newer than
                    // the last present inside the manager
                    if (v.getTimestamp() > timestamp) {
                        Data data = new Data();
                        data.setData(dateFormatter.format(v.getTimestamp()), String.valueOf(v.getValue()));
                        value.add(data);
                    }
                } else {// something went wrong searching the timestamp of the
                        // last value
                        // probably there is no values for this sensor yet, so
                        // it will be insert
                    Data data = new Data();
                    data.setData(dateFormatter.format(v.getTimestamp()), String.valueOf(v.getValue()));
                    value.add(data);
                }
                // anyway add it to the list of sampled value (for the local
                // data storage)
                // ATTENCTION: THE SENT VALUE IS STILL AND WILL BE FALSE
                valueOrig.add(v);
            }

            // encapsulates a list of Data (for the manager)
            JsonDatapoints dps = new JsonDatapoints();
            boolean retApp;
            // for each device
            // ff instead of this line : for String deviceId :
            // groupedValues.keySet() , written 2 next lines
            for (Entry iter : groupedValues.entrySet()) {
                // get the list of values
                String deviceId = (String) iter.getKey();
                List<Data> v = groupedValues.get(deviceId);
                
                
                dps.datapoints = v.toArray(new Data[v.size()]);
                LOG.info("Attempt to send " + v.size() + " values for the device " + deviceId);
                String valuesString = mapper.writeValueAsString(dps);
                retApp = webApiConsumer.publishReadingData(valuesString, deviceId);
                // if the call to the manager success
                if (retApp) {
                    LOG.info("WebApiConsumer call success: Going to update values in the localdatastorage");
                    // set sent=true for ALL the values sent (this includes:
                    // values with timestamp < last value timestamp)
                    for (SampledValue voSampled : groupedValuesOrig.get(deviceId)) {
                        voSampled.setSent(true);
                    }
                    result.addAll(groupedValuesOrig.get(deviceId));
                } else {
                    LOG.error("WebApiConsumer call failed");
                }

            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return result;
    }

    private Long getLastValueTimestamp(String deviceID) {
        // Maps timestamp to value
        Map<Long, Double> value = webApiConsumer.getLastValue(deviceID);
        if (value != null && !value.isEmpty()) {
            return value.keySet().iterator().next();
        }
        return null;
    }

    

}
