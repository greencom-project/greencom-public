package eu.greencom.xgateway.integrationlayer.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.greencom.xgateway.integrationlayer.api.service.CommunicationAdapter;
import eu.greencom.xgateway.integrationlayer.api.service.DeviceController;
import eu.greencom.xgateway.integrationlayer.api.service.MessageListener;

@Path("/DeviceController")
public class DeviceControllerImpl implements DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceControllerImpl.class.getName());
    // ff
    private static final String RESULT = "result";
    private static final int MNUM5000 = 5000 ;

    private CommunicationAdapter adapter = null;

    private MessageUtil util = new MessageUtil();

    protected void activate(ComponentContext context) { // NOSONAR squid : S1172
                                                        // ff: This is standard
        LOG.debug("Activated {}", getClass().getSimpleName());
    }

    protected void deactivate(ComponentContext context) { // NOSONAR squid :
                                                          // S1172 ff: This is
                                                          // standard
        LOG.debug("Deactivated {}", getClass().getSimpleName());
    }

    // Response timeout
    private int timeout = MNUM5000;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void setCommunicationAdapter(CommunicationAdapter adapter) {
        this.adapter = adapter;
    }

    protected void removeCommunicationAdapter(CommunicationAdapter adapter) {
        this.adapter = null;
        // ff
        LOG.info("Removing CommunicationAdapter in DeviceControllerImpl" + adapter.toString());

    }

    @Override
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{device}/{property}")
    public String get(@PathParam("device") final String deviceId, @PathParam("property") final String propertyId) {

        if (deviceId == null || propertyId == null) {
            return null;
        }

        // Lock object used to synchronize the message handling
        final Map<String, String> result = new HashMap<String, String>();

        // TODO: Coordinator expects this exact string!
        final String requestId = "req1";

        // Setup message handler first
        adapter.setMessageHandler(new MessageListener() {
            @Override
            public void receive(String message) {
                synchronized (result) {
                    result.put(RESULT, util.getResultData(message));
                    result.notifyAll();
                }
            }
        }, requestId);

        // Send request
        adapter.send(util.fillTemplate("request.get.property", requestId, deviceId, propertyId));

        // Wait for result
        synchronized (result) {
            try {
                result.wait(timeout);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
        return result.get(RESULT);
    }

    @Override
    @PUT
    @Path("{device}/{property}")
    @Consumes(MediaType.TEXT_PLAIN)
    public boolean put(final @PathParam("device") String deviceId, final @PathParam("property") String propertyId, String value) {

        if (deviceId == null || propertyId == null || value == null) {
            return false;
        }

        final Map<String, String> result = new HashMap<String, String>();

        // TODO: Coordinator expects this exact string!
        String requestId = "req1";

        adapter.setMessageHandler(new MessageListener() {
            @Override
            public void receive(String message) {
                synchronized (result) {
                    result.put(RESULT, util.getResultData(message));
                    result.notifyAll();
                }
            }
        }, requestId);

        final String message = util.fillTemplate("request.put.property", requestId, deviceId, propertyId, value);

        adapter.send(message);

        synchronized (result) {
            try {
                result.wait(timeout);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }

        // Should have responded with same value
        return value.equals(result.get(RESULT));
    }

}
