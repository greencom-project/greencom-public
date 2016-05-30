package eu.greencom.xgateway.localwebapi.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class APIWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 802189089414455154L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(WebSocketServerImpl.class);	
	}

}
