package eu.greencom.tests.xgateway.configurationbroker;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.greencom.xgateway.api.configurationbroker.ConfigurationBroker;
import eu.greencom.xgateway.api.configurationbroker.ConfigurationListener;
import eu.greencom.xgateway.api.configurationbroker.LocalConfigurationBroker;
import eu.greencom.xgateway.configurationbroker.XmlConfigurationBroker;

public class TestConfigurationListener implements ConfigurationListener{
	
	private ConfigurationBroker broker=null;
	private LocalConfigurationBroker lbroker=null;

	private String lastNotitfiedKey=null;

	private String lastNotitfiedValue=null;

	private int nNotifications;
	
	private final static Logger LOG = Logger.getLogger(TestConfigurationListener.class.getName());
	
	@BeforeClass
	public static void setUpBeforeClass()  {
		LOG.trace("setUpBeforeClass");		
	}

	@AfterClass
	public static void tearDownAfterClass()  {
		LOG.trace("tearDownAfterClass");
	}

	@Before
	public void setUp(){
		LOG.trace("setUp");
		this.lbroker = (LocalConfigurationBroker) (this.broker = new XmlConfigurationBroker());
		this.lastNotitfiedKey=null;
		this.lastNotitfiedValue=null;
		this.nNotifications=0;
	}

	@After
	public void tearDown()  {
		LOG.trace("tearDown");
		this.broker = null;
	}
	
	/**
	 * Test to verify that the component notifies correctly to 0 subscriber
	 * */
	@Test
	public void testSubscriberZero() {
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNull(tmpParams1);
	
		TestConfigurationBroker.populateTestBroker((ConfigurationBroker) this.broker);
		
		Assert.assertNull(this.lastNotitfiedKey);
		Assert.assertNull(this.lastNotitfiedValue);
		Assert.assertEquals(0, this.nNotifications);
		
	}
	
	/**
	 * Test to verify that the component notifies correctly to 1 subscriber
	 * */
	@Test
	public void testSubscriberSingle() {
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNull(tmpParams1);
		
		this.lbroker.subscribe(this, "mycomponent1");
	
		TestConfigurationBroker.populateTestBroker((ConfigurationBroker) this.broker);
		
		Assert.assertEquals("mykeyE", this.lastNotitfiedKey);
		Assert.assertEquals("myvalueE1", this.lastNotitfiedValue);
		Assert.assertEquals(5, this.nNotifications);
		
	}
	
	/**
	 * Test to verify that the component notifies correctly to 3 subscriber
	 * */
	@Test
	public void testSubscriberThree() {
		
		String[] tmpParams1 = this.broker.getAllConfigKeys("mycomponent1");
		Assert.assertNull(tmpParams1);
		
		this.lbroker.subscribe(this, "mycomponent1");
		this.lbroker.subscribe(this, "mycomponent2");
		this.lbroker.subscribe(this, "mycomponent3");
	
		TestConfigurationBroker.populateTestBroker((ConfigurationBroker) this.broker);
		
		Assert.assertEquals("mykeyE", this.lastNotitfiedKey);
		Assert.assertEquals("myvalueE3", this.lastNotitfiedValue);
		Assert.assertEquals(15, this.nNotifications);
		
	}

	@Override
	public void notify(String componentName, String key, String value) {
		this.lastNotitfiedKey=key;
		this.lastNotitfiedValue=value;
		this.nNotifications++;
	}
	
	//NOSONAR squid S1134 //FIXME maybe we can add a test to check subscribe with unset functions ?
}
