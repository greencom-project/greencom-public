MGMBrokerGateway sub-component
============================

The MGMBroker component to be installed on the Gateway.
It includes a set of components implementing Gateway-side functionalities for CommunicationLayer, CommandLayer and ControlLayer

- CommunicationLayer: a component send periodically Alive Notification messages to the configured MGM. The messages can be also solicitated by the MGM
- CommandLayer: asks to the configured MGM the configuration parameters and receives ConfigurationUpdate messages from the MGM
- ControlLayer: receives actuation messages from the MGM and translate them to specific commands for devices. 

These layers have been implemented in the task T7.1.

Configuration
-------------

All configurations can be performed through the Apache Felix Configuration Manager web interface:

	http://${IP_ADDRESS}:${PORT}/system/console/configMgr

You need to configure the identifier of JSON-RPC server through ConfigurationAdmin as described in
MGMBrokerAPI/Readme.md

Once components have been started, there is a configuration parameters which should be specified selecting "MGMBroker Gateway Config":

- MGM JSONRPC server ID: the identifier of the JSON-RPC service exposed by the manager. This gateway will send alive messages and ask for configuration updates using this ID.

the parameter is managed by ConfigurationAdmin service (PID: eu.greencom.mgmbroker.gateway)

Contacting the MGM from the Gateway
-----------------------------------

An Example on How To contact the manager is provided by *eu.greencom.mgmbroker.gateway.impl.AliveTask* class

		JsonRpcMessageBroker client= this.jsonRpcClientFactory.createJsonRpcClient(managerID);
		JsonRpcNotification notification=new JsonRpcNotification();
		notification.setMethod(Method.POST);
		notification.getParams().setUri("gc:AliveMessage");
		
		
		client.post(notification);

Receiving data in the Gateway from the MGM
------------------------------------------

A message coming from the manager, is translated into an event. *eu.greencom.mgmbroker.gateway.impl.ControlLayerGW* provides an example of how to receive notification from the manager.
It is registered as a component handling events with the topic "event/jsonrpc/notification/POST".
Looking at the *public void handleEvent(Event e)* method, you can see the code decoding a notification from manager. This notification is used to specify that the manager has successfully stored some data.

*Service registration*

		String[] topics=new String[]{"event/jsonrpc/notification/POST"};
		@SuppressWarnings("rawtypes")
		Hashtable properties=new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		properties.put(EventConstants.EVENT_FILTER, "(uri=gc:DeviceState)");
		
		context.getBundleContext().registerService(EventHandler.class, this, properties);

*Messages decoding in class eu.greencom.mgmbroker.impl.CommandLayer*

		JsonRpcNotification notification=OSGiUtils.toNotification(arg0);
		log.info("Received a request to update local configuration from manager "+notification.getParams().getSource());
		//update local configuration with received data
		try {
			TypeReference<Map<String,Object>> typeRef 
	          = new TypeReference< 
	        		  Map<String,Object> 
	               >() {}; 
	        Map<String,Object> params = mapper.readValue(notification.getParams().getData(), typeRef); 
			configurationAdmin.getConfiguration(CommandLayerParams.getGatewayConfigurationPID()).update(OSGiUtils.mapToDictionary(params));
		} catch (IOException e) {
			log.error("Error updating configuration",e);
		}
		
CommandLayer: remote configuration management
---------------------------------------------

The commandLayer allows a component on the gateway to be remotely configured by the MGM. 
A component willing to expose its configuration parameter to the MGM, must register itself as a ManagedService using a special prefix defined in CommandLayerUtils (MGMBrokerAPI).
The example below shows Dispatcher configuration registration

	private final static String PIDSuffix=".dispatcher";
	Hashtable<String, String> d=new Hashtable<String,String>();
	//register this component with the PREFIX recognized by MGMBroker CommandLayer, so the configuration can be remotely updated by the manager
	d.put(Constants.SERVICE_PID, CommandLayerUtils.GW_CONFIGURATION_PID_PREFIX+PIDSuffix);
	context.getBundleContext().registerService(ManagedService.class, this, d);

This configuration will be sent to the Manager every time it is updated (the CommandLayerGW implementation listens for CM_UPDATED).
If the local configuration is deleted, the CommandLayerGW will try to get a new one from the MGM  (the CommandLayerGW implementation listens and CM_DELETED events).
The MGM stores a copy of every configuration specified with the prefix defined above and sends updated values
to the GW every time the configuration is updated on the MGM side

Contacts
--------

This sub-component is managed by [Ivan Grimaldi](mailto:grimaldi@ismb.it).
Any question/issue can be signalled in the [wp7 mailing list](greencom-wp7@ismb.it). 

 




