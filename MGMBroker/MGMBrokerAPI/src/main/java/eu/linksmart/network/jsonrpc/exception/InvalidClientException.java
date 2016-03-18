package eu.linksmart.network.jsonrpc.exception;

/**
 * Exception raised when an instance of a @JsonRpcMessageBroker is not valid
 * anymore because the LinkSmart service is not available anymore or its
 * VirtualAddress is changed
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class InvalidClientException extends Exception {

	public InvalidClientException(String message) {
		super(message);
	}

	public InvalidClientException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
