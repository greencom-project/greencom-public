package eu.greencom.xgateway.localwebapi.rest;



import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.ext.servlet.ServerServlet;

public class RestApplicationServlet extends ServerServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected Application createApplication(Context context) {
		return new GCApplication();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
}
