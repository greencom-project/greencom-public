package eu.greencom.mgm.webapiconsumer.model;

import java.util.List;

public class ResponseStatus {
	public ResponseStatus() {

	}

	public ResponseStatus(String errorCode) {

	}

	public ResponseStatus(String errorCode, String message) {

	}

	public String errorCode;// NOSONAR squid:ClassVariableVisibilityCheck
	public List<ResponseError> errors;// NOSONAR
										// squid:ClassVariableVisibilityCheck
	public String message;// NOSONAR squid:ClassVariableVisibilityCheck
	public String stackTrace;// NOSONAR squid:ClassVariableVisibilityCheck
}
