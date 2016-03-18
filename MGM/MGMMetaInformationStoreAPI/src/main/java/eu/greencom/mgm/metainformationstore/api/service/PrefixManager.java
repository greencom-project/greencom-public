package eu.greencom.mgm.metainformationstore.api.service;

import java.util.Map;

/**
 * Simplified version of com.hp.hpl.jena.shared.PrefixMapping exposed as OSGi
 * service. It is used to interface the translation of URIs into compact URIs
 * (CURIEs) and vice versa.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface PrefixManager {

	/**
	 * Creates a new namespace-prefix mapping, given neither namespace nor
	 * prefix are already defined. Otherwise an existing prefix mapping has to
	 * be removed first.
	 * 
	 * @param prefix
	 * @param namespaceURI
	 */
	void create(String prefix, String namespaceURI);

	/**
	 * Retrieves the namespace mapped to given prefix.
	 * 
	 * @param prefix
	 * @param namespaceURI
	 */
	String get(String prefix);

	/**
	 * Deletes specified prefix mapping. TODO: check for usage in DB?
	 * 
	 * @param prefix
	 * @param namespaceURI
	 */
	void remove(String prefix);

	/**
	 * Retrieves the overall namespace-prefix mappings.
	 * 
	 * @return
	 */
	Map<String, String> list();

	/**
	 * Tests whether the given token (prefix or name-space) is registered.
	 * 
	 * @param prefixOrnamespaceURI
	 * @return
	 */
	boolean exists(String prefixOrnamespaceURI);

	/**
	 * Performs a simple string replacement of any known namespace URI into its
	 * compact form (CURIE) using the predefined prefixes.
	 * 
	 * @param input
	 * @return
	 */
	String encodeNamespaces(String input);

	/**
	 * Does the opposite of {@link #encodeNamespaces(String)} by expanding
	 * CURIEs into full URIs.
	 * 
	 * @param input
	 * @return
	 */
	String decodeNamespaces(String input);

	/**
	 * Extracts namespaces of all CURIEs used in text input.
	 * 
	 * @param input
	 * @return Subset of managed namespaces used in input string.
	 */
	Map<String, String> extractNamespaces(String input);

}
