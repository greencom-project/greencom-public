package eu.greencom.mgm.webapiconsumer.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensor {

	@JsonProperty("Id")
	private String id;

	@JsonProperty("GatewayId")
	private String gatewayId;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Value")
	private double value = 0.0;

	@JsonProperty("Status")
	private String status = "ok";

	@JsonProperty("Manufacturer")
	private String manufacturer = "greencom";

	@JsonProperty("LastSeenDate")
	// @JsonSerialize(using=CustomDateSerializer.class)
	// The date format requires Java 1.7!
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX", timezone = "UTC")
	public Date lastSeenDate = new Date();

	@JsonProperty("LastDateValue")
	// @JsonSerialize(using=CustomDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX", timezone = "UTC")
	public Date lastDateValue = new Date();

	@JsonProperty("CreationDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX", timezone = "UTC")
	public Date creationDate = new Date();

	@JsonProperty("LastModificationDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX", timezone = "UTC")
	public Date lastModificationDate = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Date getLastSeenDate() {
		return lastSeenDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public void setLastSeenDate(Date lastSeenDate) {
		this.lastSeenDate = lastSeenDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public Date getLastDateValue() {
		return lastDateValue;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public void setLastDateValue(Date lastDateValue) {
		this.lastDateValue = lastDateValue;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public Date getCreationDate() {
		return creationDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public Date getLastModificationDate() {
		return lastModificationDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;//NOSONAR findbugs:EI_EXPOSE_REP2 - JPU: Trusted code/objects passed
	}
	

}
