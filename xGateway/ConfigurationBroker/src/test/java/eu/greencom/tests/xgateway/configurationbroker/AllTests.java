package eu.greencom.tests.xgateway.configurationbroker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestConfigurationBroker.class, TestConfigurationListener.class,
		TestLocalConfigurationBroker.class})
public class AllTests {

}
