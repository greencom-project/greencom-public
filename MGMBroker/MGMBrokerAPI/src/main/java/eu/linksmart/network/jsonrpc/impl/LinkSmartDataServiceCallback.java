package eu.linksmart.network.jsonrpc.impl;

/**
 * Generic callback to pass to the @LinkSmartDataServiceProxy constructor.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 * @param <T>
 */
public interface LinkSmartDataServiceCallback<T> {

	/**
	 * Method invoked by @LinkSmartDataServiceProxy when a new message have been
	 * received by the endpoint.
	 * 
	 * @param t
	 */
	public void notify(T t);

}
