/* This sketch demonstrates JSON-RPC messages exchange between coordinator and gateway */
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

void setup() {
  // initialize serial:
  Serial.begin(19200);
  // reserve 200 bytes for the inputString:
  inputString.reserve(400);
}

void loop() {
  // print the string when a newline arrives:
  if (stringComplete) {    
    processInput(inputString);
    // clear the string:
    inputString = "";
    stringComplete = false;
    delay(500);
    Serial.println("{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{\"gc:externalIdentifier\":\"2E15BC001B020E48\",\"gc:observationResultTime\":\":\"2014-10-20T01:31:4\",\"rdf:value\":\"0000002308\"}}}");    
    delay(500);
    Serial.println("{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{\"gc:externalIdentifier\":\"2E15BC001B020E48\",\"gc:observationResultTime\":\":\"2014-10-20T01:31:4\",\"rdf:value\":\"0000002308\"}}}");    
    delay(500);
    Serial.println("{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{\"gc:externalIdentifier\":\"2E15BC001B020E48\",\"gc:observationResultTime\":\":\"2014-10-20T01:31:4\",\"rdf:value\":\"0000002308\"}}}");    
    delay(500);
    Serial.println("{\"jsonrpc\":\"2.0\",\"method\":\"POST\",\"params\":{\"uri\":\"gc:Observation\",\"data\":{\"gc:externalIdentifier\":\"2E15BC001B020E48\",\"gc:observationResultTime\":\":\"2014-10-20T01:31:4\",\"rdf:value\":\"0000002308\"}}}");    
}

}

void processInput(String in){
  String out = in;
  //{"jsonrpc":"2.0","method":"POST","id":"id1","params":{"uri":"gc:GatewaySession","data":{"gc:referenceTime":"2014-10-20T11:34:00Z"}}}
  //{"jsonrpc":"2.0","method":"POST","id":"id9","params":{"uri":"gc:GatewaySession","data":{"gc:referenceTime":"2014-10-20T08:17:28Z"}}} 
  if(in.indexOf("gc:GatewaySession") != -1){
    // 3 char ID
    String id = in.substring(39,42);
    out = "{\"jsonrpc\":\"2.0\",\"id\":\""+id+"\",\"result\":{\"uri\":\"temp:sessionId1\"}}";
  }
  // Send list of available transducers
  // {"jsonrpc":"2.0","method":"GET","id":"id9","params":{"uri":"gc:Transducer"}}
  else if(in.indexOf("gc:Transducer") != -1){
    String id = in.substring(38,41); 
    out = "{\"jsonrpc\":\"2.0\",\"id\":\""+id+"\",\"result\":[{\"rdf:type\":\"gc:CummulativePowerConsumptionSensor\",\"gc:hasUnit\": \"gc:WattHour\",\"gc:externalIdentifier\" :\"mac:addr\"},{\"rdf:type\":\"gc:Switch\",\"gc:externalIdentifier\" :\"mac:addr1\"}]}";
  }
  Serial.println(out);  
}

void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
  
}
