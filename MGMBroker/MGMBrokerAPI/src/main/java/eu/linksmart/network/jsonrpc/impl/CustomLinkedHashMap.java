package eu.linksmart.network.jsonrpc.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom @LinkedHashMap class used to create an in-memory cache of registered
 * response handlers. The implemented policy is based on a FIFO queue.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 */
public class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final int MAX_ENTRIES = 1000;

	/**
	 * Method called to decide whether the eldest entry should be removed. The
	 * entry is removed is the maximum size is reached for this queue
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry eldest) {
		return size() > MAX_ENTRIES;
	}

}
