package eu.greencom.tests.xgateway.configurationbroker;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;
import eu.greencom.xgateway.configurationbroker.XmlConfigurationBroker;

public class TestConfigurationBroker {

	private final static Logger LOG = Logger.getLogger(TestConfigurationBroker.class.getName());
	
	private ConfigurationBroker broker=null;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		LOG.trace("setUpBeforeClass");		
	}

	@AfterClass
	public static void tearDownAfterClass() {
		LOG.trace("tearDownAfterClass");
	}

	@Before
	public void setUp() {
		LOG.trace("setUp");
		this.broker = new XmlConfigurationBroker();
	}

	@After
	public void tearDown()  {
		LOG.trace("tearDown");
		this.broker = null;
	}
	
	/**
	 * Test to check that the component is in a clean state at startup
	 * */
	@Test
	public void testConstructor() {
		Assert.assertNotNull(this.broker);
		String[] tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(0, tmpComponents.length);
			
		String[] tmpParams = this.broker.getAllConfigKeys("mycomponent");
		Assert.assertNull(tmpParams);
		
		String tmpParam = this.broker.getConfigValue("mycomponent", "mykey");
		Assert.assertNull(tmpParam);
	}
	
	/**
	 * Test to verify that the Broker creates correctly new components name on the fly and provides access correctly
	 * */
	@Test
	public void testSetConfigComponentCreationAndAccess() {
		Assert.assertNotNull(this.broker);
		String[] tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(0, tmpComponents.length);
		
		populateTestBroker(this.broker);
		
		tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(3, tmpComponents.length);
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNotNull(tmpParams1);
		Assert.assertEquals(5, tmpParams1.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"missingKey"));
		Assert.assertEquals("myvalueA1",this.broker.getConfigValue("mycomponent1", "mykeyA"));
		Assert.assertEquals("myvalueB1",this.broker.getConfigValue("mycomponent1", "mykeyB"));
		Assert.assertEquals("myvalueC1",this.broker.getConfigValue("mycomponent1", "mykeyC"));
		Assert.assertEquals("myvalueD1",this.broker.getConfigValue("mycomponent1", "mykeyD"));
		Assert.assertEquals("myvalueE1",this.broker.getConfigValue("mycomponent1", "mykeyE"));
		
		String[] tmpParams2 = this.broker.getAllConfigKeys("mycomponent2");
		Assert.assertNotNull(tmpParams2);
		Assert.assertEquals(5, tmpParams2.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams2,"missingKey"));
		Assert.assertEquals("myvalueA2",this.broker.getConfigValue("mycomponent2", "mykeyA"));
		Assert.assertEquals("myvalueB2",this.broker.getConfigValue("mycomponent2", "mykeyB"));
		Assert.assertEquals("myvalueC2",this.broker.getConfigValue("mycomponent2", "mykeyC"));
		Assert.assertEquals("myvalueD2",this.broker.getConfigValue("mycomponent2", "mykeyD"));
		Assert.assertEquals("myvalueE2",this.broker.getConfigValue("mycomponent2", "mykeyE"));
		
		String[] tmpParams3 = this.broker.getAllConfigKeys("mycomponent3");
		Assert.assertNotNull(tmpParams3);
		Assert.assertEquals(5, tmpParams3.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams3,"missingKey"));
		Assert.assertEquals("myvalueA3",this.broker.getConfigValue("mycomponent3", "mykeyA"));
		Assert.assertEquals("myvalueB3",this.broker.getConfigValue("mycomponent3", "mykeyB"));
		Assert.assertEquals("myvalueC3",this.broker.getConfigValue("mycomponent3", "mykeyC"));
		Assert.assertEquals("myvalueD3",this.broker.getConfigValue("mycomponent3", "mykeyD"));
		Assert.assertEquals("myvalueE3",this.broker.getConfigValue("mycomponent3", "mykeyE"));

		
	}
	
	public static void populateTestBroker(ConfigurationBroker tmpbroker) {
		tmpbroker.setConfigValue("mycomponent1", "mykeyA", "myvalueA1");
		tmpbroker.setConfigValue("mycomponent1", "mykeyB", "myvalueB1");
		tmpbroker.setConfigValue("mycomponent1", "mykeyC", "myvalueC1");
		tmpbroker.setConfigValue("mycomponent1", "mykeyD", "myvalueD1");
		tmpbroker.setConfigValue("mycomponent1", "mykeyE", "myvalueE1");
		
		tmpbroker.setConfigValue("mycomponent2", "mykeyA", "myvalueA2");
		tmpbroker.setConfigValue("mycomponent2", "mykeyB", "myvalueB2");
		tmpbroker.setConfigValue("mycomponent2", "mykeyC", "myvalueC2");
		tmpbroker.setConfigValue("mycomponent2", "mykeyD", "myvalueD2");
		tmpbroker.setConfigValue("mycomponent2", "mykeyE", "myvalueE2");
		
		tmpbroker.setConfigValue("mycomponent3", "mykeyA", "myvalueA3");
		tmpbroker.setConfigValue("mycomponent3", "mykeyB", "myvalueB3");
		tmpbroker.setConfigValue("mycomponent3", "mykeyC", "myvalueC3");
		tmpbroker.setConfigValue("mycomponent3", "mykeyD", "myvalueD3");
		tmpbroker.setConfigValue("mycomponent3", "mykeyE", "myvalueE3");
		
	}
	
	public static boolean isKeyThere(String[] haystack, String needle){
		
		if(haystack==null) {
			return false;
		}
		
		for (String s: haystack) {
			if(needle.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test to check that the ConfigurationBroker is deleted correctly with unset commands.
	 * */
	@Test
	public void testUnSetConfigsParameter() {
		Assert.assertNotNull(this.broker);
		String[] tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(0, tmpComponents.length);
		
		populateTestBroker(this.broker);
		
		tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(3, tmpComponents.length);
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNotNull(tmpParams1);
		Assert.assertEquals(5, tmpParams1.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"missingKey"));
		Assert.assertEquals("myvalueA1",this.broker.getConfigValue("mycomponent1", "mykeyA"));
		Assert.assertEquals("myvalueB1",this.broker.getConfigValue("mycomponent1", "mykeyB"));
		Assert.assertEquals("myvalueC1",this.broker.getConfigValue("mycomponent1", "mykeyC"));
		Assert.assertEquals("myvalueD1",this.broker.getConfigValue("mycomponent1", "mykeyD"));
		Assert.assertEquals("myvalueE1",this.broker.getConfigValue("mycomponent1", "mykeyE"));
		
		//testing removal of 1 full component
		this.broker.unsetConfigComponent("mycomponent1");
		
		tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(2, tmpComponents.length);
		
		String[] tmpParams = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNull(tmpParams);
		Assert.assertNull(this.broker.getConfigValue("mycomponent1", "mykeyA"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent1", "mykeyB"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent1", "mykeyC"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent1", "mykeyD"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent1", "mykeyE"));
		
		tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(2, tmpComponents.length);
		
		
		//now I try to remove a couple of values.
		this.broker.unsetConfigKey("mycomponent2", "mykeyA");
		this.broker.unsetConfigKey("mycomponent2", "mykeyB");
		
		tmpComponents = this.broker.getComponentsNames();
		Assert.assertNotNull(tmpComponents);
		Assert.assertEquals(2, tmpComponents.length);
		
		String[] tmpParams3 = this.broker.getAllConfigKeys("mycomponent2");
		Assert.assertNotNull(tmpParams3);
		Assert.assertEquals(3, tmpParams3.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams3,"missingKey"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams3,"mykeyA"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams3,"mykeyB"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams3,"mykeyC"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams3,"mykeyD"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams3,"mykeyE"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent2", "mykeyA"));
		Assert.assertNull(this.broker.getConfigValue("mycomponent2", "mykeyB"));
		Assert.assertEquals("myvalueC3",this.broker.getConfigValue("mycomponent3", "mykeyC"));
		Assert.assertEquals("myvalueD3",this.broker.getConfigValue("mycomponent3", "mykeyD"));
		Assert.assertEquals("myvalueE3",this.broker.getConfigValue("mycomponent3", "mykeyE"));
	}

}
