package eu.greencom.xgateway.configurationbroker.internals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import eu.greencom.xgateway.configurationbroker.internals.jaxb.Component;
import eu.greencom.xgateway.configurationbroker.internals.jaxb.Configuration;
import eu.greencom.xgateway.configurationbroker.internals.jaxb.Param;
/**
 * Support class dealing with load/save operations to file.
 * 
 * @author riccardo.tomasi
 *
 */
public abstract class XMLConfigurationWriterLoader {
	
	private final static Logger LOG = Logger.getLogger(XMLConfigurationWriterLoader.class.getName());
	
	//RR
	private XMLConfigurationWriterLoader(){
		
	}
	
	private static void init(String filepath){
		Map<String, ComponentRepository> c=new HashMap<String,ComponentRepository>();
		ComponentRepository cr=new ComponentRepository("common");
		cr.setConfigValue("project.name", "GreenCom");
		c.put("common", cr);
		XMLConfigurationWriterLoader.dump(filepath, c);	
	}
	

	/**
	 * Dumps configuration to XML file
	 * 
	 * @param filename The file for dumping the configuration info
	 * @param componentsMap the configuration to be dumped
	 * 
	 * */
	public static void dump(String filepath, Map<String, ComponentRepository> componentsMap) {
		
		Configuration x = XMLConfigurationWriterLoader.decode(componentsMap);
		
		LOG.debug("Configuration to be dumped:\n" + x.toString());
		
		Path p=Paths.get(filepath);
		
		try {
			Files.createDirectory(p.getParent());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			LOG.error(XMLConfigurationWriterLoader.class.getSimpleName() + ": " + e1, e1 );
		}
		
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Configuration.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			BufferedWriter w=Files.newBufferedWriter(p, Charset.forName("UTF-8"));
//			w.append("<!-- DOES NOT MODIFY THIS FILE MANUALLY -->\n"); //NOSONAR squid: CommentedOutCodeLine
//			w.append("<!-- \n"); //NOSONAR squid: CommentedOutCodeLine
//			w.append("     This file is managed by the ConfigurationBroker component.\n"); //NOSONAR squid: CommentedOutCodeLine
//			w.append("	   A manually modification can cause serious malfunctioning \n"); //NOSONAR squid: CommentedOutCodeLine
//			w.append("-->\n"); //NOSONAR squid: CommentedOutCodeLine
			jaxbMarshaller.marshal(x, w);
		} catch (Exception e) {
			LOG.error(e);
		}
	
	}

	private static Configuration decode(Map<String, ComponentRepository> componentsMap) {
		
		Configuration ret = new Configuration();
		
		ret.component = new Component[componentsMap.size()];
		
		int i=0;
		
		/*RR(Stringcomponent_name:components_map.keySet())*/
		for(ComponentRepository repo: componentsMap.values()){	
			String compName = repo.getComponentName();
			//RR ["+component_name+"]")
			LOG.debug("dumping configuration for component ["+compName+"]");
		 	Component c = new Component();
		    c.name = compName;
		 	/*RRc.name=component_name*/
		 
		/*	RRComponentRepository repo = components_map.get(component_name)*/
			
		 	String[] keys = repo.getAllConfigKeys();
			
			c.param = new Param[keys.length];
			int j=0;
			for (String k: keys) {
				String value = repo.getConfigValue(k);
				c.param[j++] = new Param(k,value);
			}
			
			ret.component[i++]=c;
		}
		return ret; 
		
	}

	/**
	 * Loads full configuration from XML file and returns it as Map<String, ComponentRepository>
	 * 
	 * @param filename The file for dumping the configuration info
	 * @param components_map the configuration to be dumped
	 * 
	 * */
	public static Map<String, ComponentRepository> load(String filepath) {
		Path p=Paths.get(filepath);
		//File file = new File(filename); //NOSONAR squid: CommentedOutCodeLine
		
		JAXBContext jaxbContext;
		
		if(Files.notExists(p,LinkOption.NOFOLLOW_LINKS)){
			LOG.info("File does not exist: going to create a basic config file");
			XMLConfigurationWriterLoader.init(filepath);		
		}
		
		Configuration conf = null;
		try {
			
			jaxbContext = JAXBContext.newInstance(Configuration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			conf = (Configuration) jaxbUnmarshaller.unmarshal(Files.newBufferedReader(p, Charset.forName("UTF-8")));
			
		} catch (Exception e) {
			LOG.error(e);
		}

		return XMLConfigurationWriterLoader.encode(conf);
	}

	private static Map<String, ComponentRepository> encode(Configuration conf) {
		if(conf==null){
			return null;
		}
		Map<String, ComponentRepository> ret = new HashMap<String,ComponentRepository>();
		
		if(conf.component==null){
			return ret;
		}
		for (Component comp: conf.component){
			ComponentRepository repo = new ComponentRepository(comp.name);
			
			for (Param p: comp.param) {
				repo.setConfigValue(p.key, p.value);
			}
			
			ret.put(comp.name, repo);
		}
		
		return ret;
	}


}
