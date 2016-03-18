package eu.greencom.xgateway.hpintegrationconsumer.constant;

import eu.greencom.api.domain.SampledValue;

public class JsonSampledValue implements SampledValue {

	private static final long serialVersionUID = -3790441237330931238L;
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
