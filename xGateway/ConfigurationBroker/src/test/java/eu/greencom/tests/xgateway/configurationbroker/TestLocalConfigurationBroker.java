package eu.greencom.tests.xgateway.configurationbroker;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;
import eu.greencom.xgateway.api.configurationbroker.LocalConfigurationBroker;
import eu.greencom.xgateway.configurationbroker.XmlConfigurationBroker;

public class TestLocalConfigurationBroker {
	
	private final static Logger LOG = Logger.getLogger(TestLocalConfigurationBroker.class.getName());
	
	private LocalConfigurationBroker broker=null;
	
	@BeforeClass
	public static void setUpBeforeClass(){
		LOG.trace("setUpBeforeClass");		
	}
   //RR throws Exception 
	@AfterClass
	public static void tearDownAfterClass()  {
		LOG.trace("tearDownAfterClass");
	}

	@Before
	public void setUp() {
		LOG.trace("setUp");
		this.broker = new XmlConfigurationBroker();
	}

	@After
	public void tearDown() {
		LOG.trace("tearDown");
		this.broker = null;
	}
	
	/**
	 * Test to verify that the Broker provides access correctly to all stored components
	 * */
	@Test
	public void testSetConfigComponentCreation() {
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNull(tmpParams1);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"missingKey"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyA"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyB"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyC"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyD"));
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyE"));
	
		TestConfigurationBroker.populateTestBroker((ConfigurationBroker) this.broker);
		
		tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNotNull(tmpParams1);
		Assert.assertEquals(5, tmpParams1.length);
		Assert.assertFalse(TestConfigurationBroker.isKeyThere(tmpParams1,"missingKey"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyA"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyB"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyC"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyD"));
		Assert.assertTrue(TestConfigurationBroker.isKeyThere(tmpParams1,"mykeyE"));
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




}
