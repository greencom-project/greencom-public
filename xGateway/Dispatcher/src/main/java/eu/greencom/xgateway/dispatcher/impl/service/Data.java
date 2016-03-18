package eu.greencom.xgateway.dispatcher.impl.service;

public class Data {
    

    private String date;
	private String value;
	
	
	public Data() {
    }

    public void setData(String date, String value){
	    this.date=date;
        this.value=value;
	}
    
    public String getValue() {
        return value;
    }
    
    public String getDate() {
        return date;
    }
    
}
