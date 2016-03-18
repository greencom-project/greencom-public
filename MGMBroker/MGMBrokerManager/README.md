MGMBrokerManager sub-component
==============================

The MGMBroker component to be installed on the Microgrid Manager.
It includes a set of components implementing MGM-side functionalities for CommunicationLayer, CommandLayer and ControlLayer

- CommunicationLayer: A component receiving Alive Notification from gateways and maintaining the list of available gateways.
	It also supports the solicitation of alive message to a specific gateway
- CommandLayer: manages the gateways remote configuration, responding to request of configuration from gateways and dispatching configuration
	changes to gateways
- ControlLayer: Exposes java interfaces in MGM runtime to perform actuation on devices. When one method from the interface is called, it is translated to a JSON-RPC message and sent to the Gateway 

These layers have been implemented in the task T7.1 and documented in deliverable D6.1.2.

The MGMBrokerManager also includes a System Health Monitor component developed for task T6.3 and documented in deliverable D8.4.
It includes:

- A periodic task which Checks for installations status and sends a mail to notify the Deployment responsibles
- A GUI that can be used to check the status of the installation
- A JSONP endpoint which make the System Health Monitor functionalities available for 3rd party applications

Configuration
-------------

All configurations can be performed through the Apache Felix Configuration Manager web interface:

	http://${IP_ADDRESS}:${PORT}/system/console/configMgr

You need to configure the identifier of JSON-RPC server through ConfigurationAdmin as described in
MGMBrokerAPI/Readme.md

Then you can configure System Health monitor parameters (by selecting "MGMBroker Manager System Health Monitor" in the web interface)
Configurable parameters are:

- Recipient address of email notifications
- IDs of gateways and installations: this parameter configures the list of gateways to be monitored by the system. 
Can be configured with the webconsole configuration manager: http://${IP_ADDRESS}:${PORT}/system/console/configMgr
The value is a list of Gateways, each gateway has this format:

	${GATEWAY_JSONRPC_SERVER_ID}:${DATAWAREHOUSE_INSTALLATION_ID}

	The gateways list uses the pipe ("|") as a separator.
	The example below configures gateways from house 1 to 5:

	GW1:85f4d922-d876-47cf-bdc2-547924640bb5|GW2:e6306a39-5b26-41e4-ae2f-a3df8a92ef18|GW3:e3355201-1ecd-4342-9570-b310b32d50b5|GW4:18d91568-c30f-4f32-ad8b-3ef2f59b1665|GW5:6d087111-c426-4c46-81f1-3333583d340d|GW6:f830cceb-d330-4090-9521-a4b9cfaf5db1




Contacting the Gateway from MGM
-------------------------------

An Example on How To use GatewayAPI is provided by *eu.greencom.mgmbroker.manager.impl.CommandLayerMGRImpl* class.

		log.info("Configuration have been modified, notifying available gateways");
		JsonRpcNotification notification = prepareNotification(properties);
		if(notification==null)
		{
			return;
		}
		//notify all available gateways about the new configuration parameters
		for(String gw:communicationLayerMGR.getGateways().keySet())
		{
			log.debug("Notifying gateway "+gw+" with new configuration");
			this.clientFactory.createJsonRpcClient(gw).post(notification);		
		}

Receiving data from the Gateways in MGM
---------------------------------------

A message coming from the manager, is translated into an event. *eu.greencom.mgmbroker.manager.impl.CommandLayerMGRImpl* provides an example of how to receive notification from the gateway.
It is registered as a component handling events with the topic "event/jsonrpc/request/GET".
Looking at the *public void handleEvent(Event e)* method, you can see the code decoding a notification from gateway.

         notification=OSGiUtils.toNotification(event);
			
		 try {
			//parse the received configuration object
			 ConfigurationObject configurationObject=mapper.readValue(notification.getParams().getData(),ConfigurationObject.class);
			 
			 ...

CommandLayer: remote configuration management
---------------------------------------------

The MGM CommandLayer components is able to:
- redceive configurations to be remotely handled through JSONRPC norifications: these configuration will be stored in a local copy
 if a previous configuration was not foundor the configuration received contains properties not available in the local copy
- send configuration values to the gateways when the local copy of a configuration is changed
- send configuration values to a particular gateway when its local copy of the configuration have been deleted



Contacts
--------

This sub-component is managed by [Ivan Grimaldi](mailto:grimaldi@ismb.it).
Any question/issue can be signalled in the [wp7 mailing list](greencom-wp7@ismb.it). 

 




