package eu.greencom.xgateway.hpintegrationconsumer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;

import eu.greencom.xgateway.hpintegration.HPIntegrationService;

public class WebConsolePage extends AbstractWebConsolePlugin{
	
	private static final Logger LOG = Logger.getLogger(WebConsolePage.class.getName());
	private static final long serialVersionUID = 5231612586564345655L;
	private static final String LABEL = "HP-webconsole";
	private static final String TITLE = "HP Integration Layer";
	private EventAdmin eventAdmin;
	HPIntegrationService hpservice;
	
	public WebConsolePage() {
		super();
	}
	
	protected void activate(ComponentContext context) {
		activate(context.getBundleContext());
	}

	protected void deactivate(ComponentContext context) {
		deactivate();
		LOG.info("Deactivate in SerialWebConsolePlugin " + context.toString());
	}

	@Override
	public String getLabel() {
		return LABEL;
	}
	
	@Override
	public String getCategory() {
		return "GreenCom";
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}

	@Override
	protected void renderContent(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	
	protected void bindHPintegration(HPIntegrationService hpService) {
		this.hpservice = hpService;
	}

	protected void unbindHPintegration(HPIntegrationService hpService) {
		this.hpservice = null;
	}
	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

}
