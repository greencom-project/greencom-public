package eu.greencom.mgm.metainformationstore.api.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple RDF-Graph model. Map of resources mapped by their CURIE key.
 * 
 * @see http://jena.apache.org/documentation/io/rdf-json.html.
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class Graph extends HashMap<String, ResourceGraph> {

	private static final long serialVersionUID = 8827055788530891294L;

	// TODO: add namespace en/decoding encode(Map) decode(Map)

	public String toTurtle(){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, ResourceGraph> graphEntry: entrySet()){
			String subject = graphEntry.getKey();					
			// Subject URI
			sb.append("<").append(subject).append("> ");
			int pCount = 0;
			ResourceGraph rGraph = get(subject);
			for (Map.Entry<String, Set<PredicateValue>> entry: rGraph.entrySet()){				
				String predicate = entry.getKey();				
				// Delimit predicate
				if(pCount > 0) {
					sb.append("; ");
				}
				sb.append("<").append(predicate).append("> ");
				++pCount;
				int vCount = 0;
				for (PredicateValue value: rGraph.get(predicate)){
					// Delimit value
					if(vCount > 0) {
						sb.append(", ");
					}
					sb.append(value.toTurtle());
					++vCount;
				}
			}
			// Close statement
			sb.append(". ");
		}
		return sb.toString();	
	}
}
