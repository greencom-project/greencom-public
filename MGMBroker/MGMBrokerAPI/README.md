MGMBrokerAPI sub-component
============================

A component defining APIs and exposing CommunicationLayer, ConmmandLayer and ControlLayer services over LinkSmart network.

It implements:

* A JSON-RPC server and client which enables messages exchange over the LinkSmart Data Backbone
* A LinkSmart CommunicationSecurityManager using symmetric keys to provide Authenticity, Confidentiality and Integrity of exchanged messages on JSON-RPC endpoint. 

These components and layers have been implemented in the task T7.1.

JSON-RPC server configuration
---------------

Configure the JSON-RPC server identifier through the Felix Web Console:

	http://${IP_ADDRESS}:${PORT}/system/console/configMgr
	
The server identifier must be unique in the network.

Once configured, JSON-RPC LinkSmart endpoints will be created

The client can be used referencing a service implementing JsonRpcClientFactory interface (which is implemented and exposed as Declarative Service by this component)

Using JSONRPC client
--------------------

This example shows how to send a notification to a JSON-RPC endpoint with ID "MyServer"

	JsonRpcNotification notification = prepareNotification(properties);
	this.clientFactory.createJsonRpcClient("MyServer").post(notification);		

The example below shows how to use the JsonRpcClientFactory to send a request and synchronously receive a response.

	JsonRpcMessageBroker client= this.jsonRpcClientFactory.createJsonRpcClient("SERVER_ID");
	JsonRpcRequest request=new JsonRpcRequest();
	request.setMethod(Method.GET);
	request.getParams().setUri("gc:SolicitateAliveMessage");
	Date d=new Date();
	JsonRpcResponse response=client.post(request);

Receiving data on the server
---------------------------

First of all you need to register a component as EventHandler for:

	event/jsonrpc/${message_type}/${operation}
	
where:

- message_type is one of "notification","request","response
- operation is one of "GET","POST","PUT",DELETE"

Wildcards are allowed, see OSGi Event Admin specifications for more details.

the "handleEvent" method of your component will receive messages matching the topic as events. In this example a request is expected and a response is sent back.

public void handleEvent(Event e){

	JsonRpcRequest request=OSGiUtils.toRequest(arg0);
		
	JsonRpcResponse response=null;
	response = prepareResponse(myproperties);		
	
	//IMPORTAND to bind request/response
	response.setId(request.getId());
	
	this.clientFactory.createJsonRpcClient(request.getParams().getSource()).post(response);

Contacts
---------------
This sub-component is managed by [Ivan Grimaldi](mailto:grimaldi@ismb.it).
Any question/issue can be signalled in the [wp7 mailing list](greencom-wp7@ismb.it). 

 




