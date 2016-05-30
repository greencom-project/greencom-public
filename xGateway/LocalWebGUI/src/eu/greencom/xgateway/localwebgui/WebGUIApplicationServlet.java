package eu.greencom.xgateway.localwebgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebGUIApplicationServlet extends HttpServlet{

	/**
	 *  Farmin Farzin @ ISMB
	 *  
	 *  Registering the http service at //*/index.html
	 */
	private static final long serialVersionUID = 5928134375579225240L;
	private static final Logger LOG = LoggerFactory.getLogger(WebGUIApplicationServlet.class.getName());
	final private WebGUIImpl father;

	public WebGUIApplicationServlet(WebGUIImpl father) {
		LOG.debug("WebGUIApplicationServlet constructor");
		this.father = father;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	
	/**
	 * doGET is called for any web request matching the filter as registered in the WebGUI component.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LOG.debug("get httpservlet");
		String uri = req.getRequestURI();
		URL myfile = getClass().getResource(uri);

		if ("/".equals(uri)) {
			uri = "/index.html";
		}

		// this check is already handled by the servlet configuration - but just
		// for additional safety we also check the prefix
		if (!uri.startsWith("/")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}


		// we check the the dinamic page to be served actually exists
		if (myfile == null) {
			// file not existing
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		BufferedReader filereader = null;
		filereader = this.getFile(uri);

		if (filereader == null || !filereader.ready()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "requested resource [uri=" + uri + "] does not exist");
			return;
		}

		// prepare for file output
		res.setContentType("text/html");
		ServletOutputStream webpage = res.getOutputStream();

		// discharging the filereader buffer in the webpage buffer
		while (filereader.ready()) {
			String line = filereader.readLine();
			webpage.println(line);
		}

		webpage.flush();
		LOG.debug("get httpservlet finished successfully");

	}
	
	
	private BufferedReader getFile(String filename) throws FileNotFoundException {
		InputStream filestream = null;

		if (!this.father.isGetConf_use_external_webgui_pages()) {
			filestream = getClass().getResourceAsStream(filename);
		} else {
			String myfile = this.father.getConf_external_webgui_pages_path() + filename;
			filestream = new FileInputStream(new File(myfile));

		}

		BufferedReader ret = new BufferedReader(new InputStreamReader(filestream));
		return ret;
	}
}
