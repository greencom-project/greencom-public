/** 
 * Coded By Giorgio Dal To� on 26/mag/2013 
 *
 * Internet of Things Service Management Unit 
 * Pervasive Technologies Area
 * Istituto Superiore Mario Boella
 * Tel. (+39) 011 2276614
 * Email: daltoe@ismb.it
 * Email: giorgio.daltoe@gmail.com 
 * 
 * '||'  .|'''.|  '||    ||' '||''|.   
 *  ||   ||..  '   |||  |||   ||   ||  
 *  ||    ''|||.   |'|..'||   ||'''|.  
 *  ||  .     '||  | '|' ||   ||    || 
 * .||. |'....|'  .|. | .||. .||...|'
 *
 * Via Pier Carlo Boggio 61 
 * 10138 Torino, Italy
 * T 011/2276201; F 011/2276299
 * info@ismb.it
 */
package eu.greencom.xgateway.integrationlayer;

import eu.greencom.api.domain.SampledValue;

public class JsonRpcSampledValue implements SampledValue {

    private static final long serialVersionUID = -3790441237330931245L;
    private String timeSeriesID;
    private String deviceID;
    private long timestamp;
    private double value;
    private boolean sent;
    private String id;

    @Override
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean isSent() {
        return sent;
    }

    @Override
    public String getTimeSeriesID() {
        return timeSeriesID;
    }

    public void setTimeSeriesID(String timeSeriesID) {
        this.timeSeriesID = timeSeriesID;
    }

    @Override
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    @Override
    public String get_id() {
        return id;
    }

    @Override
    public void set_id(String id) {
        this.id = id;
    }

}
