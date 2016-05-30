package eu.greencom.xgateway.localwebapi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

	@Override
	public void start(BundleContext arg0) throws Exception {
		LOG.info("local web api bundle started");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		LOG.info("lcoal web api bundle stopped");
	}

}

