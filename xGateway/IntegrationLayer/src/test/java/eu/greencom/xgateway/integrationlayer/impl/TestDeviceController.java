// Same as implementation classes - access to protected and package methods
package eu.greencom.xgateway.integrationlayer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;

import eu.greencom.xgateway.integrationlayer.api.service.CommunicationAdapter;
import eu.greencom.xgateway.integrationlayer.api.service.MessageListener;

public class TestDeviceController {

    DeviceControllerImpl controller;
    CommunicationAdapter adapter;
    MessageUtil util = new MessageUtil();

    @Before
    public void initTest() {
        controller = new DeviceControllerImpl();

        adapter = new CommunicationAdapter() {
            MessageListener handler;
            @Override
            public void setMessageHandler(MessageListener listener, String id) {
                handler = listener;
            }
            @Override
            public void send(String message) {
                if (!message.contains("method")){
                // Handler is interested in responses, omit requests                
                    handler.receive(message);
                }
            }
            @Override
            public void removeMessageListener(MessageListener listener) {
            	//Ignore
            }
            @Override
            public void addMessageListener(MessageListener listener) {
            	//Ignore
            }
        };
        controller.setCommunicationAdapter(adapter);
    }

    @Test
    public void testPutMessageHandling() {
        // Schedule response to PUT request
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                adapter.send(util.fillTemplate("response.put.property", "myRequest", "myValue"));
            }
        }, 500);
        // Send blocking PUT request
        assertTrue(controller.put("myDevice", "myProperty", "myValue"));
    }

    @Test
    public void testPutMessageHandlingFailure() {
        // Do not send any response
        // Send blocking PUT request
        controller.setTimeout(100);
        assertTrue(!controller.put("myDevice", "myProperty", "myValue"));
    }

    @Test
    public void testGetMessageHandling() {
        // Schedule response to GET request
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                adapter.send(util.fillTemplate("response.get.property", "myRequest", "myValue"));
            }
        }, 500);
        // Send blocking GET request
        assertEquals("myValue", controller.get("myDevice", "myProperty"));
    }

    @Test
    public void testGetMessageHandlingFailure() {
        // Do not send any response
        // Send blocking PUT request
        controller.setTimeout(100);
        assertTrue(controller.get("myDevice", "myProperty") == null);
    }

}