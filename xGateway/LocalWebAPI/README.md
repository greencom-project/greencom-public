# Gateway local API
- Local API on the xGateway Module which can use fake event or even the real events.
- Fake event component which sends fake events every interval of time and fill the Mongo with the CSV data for last day.
- Websocket server which gives the possibility to subscribe to a sensors events (This can be also work in real case with small modification in IntegrationLayer)

## Rest API
**/api/sensors** : shows the devices connected to gateway (only works now with fake events)
- example of response for ``localhost:8080/api/sensors``

```
{"code":200,"result":["sensor_meter_mains","sensor_tv","sensor_battery_soc","sensor_meter_pv","sensor_temp_outdoor","sensor_washing_machine","sensor_light","sensor_temp_indoor"]}
```

**/api/sensor/{sensor_name}** : shows the last value of the related sensor (only works now with fake events)
- example of response for ``localhost:8080/api/sensors/{name of the sensor}``

```
{"code":200,"result":{"sensor_ts":1462269951211,"sensor_value":1000.0,"sensor_name":"sensor_meter_mains"}}
```

**/api/sensor/historic/{sensor_name}** : shows historic data of the current sensor
- example of response for ``http://localhost:9090/api/sensors/sensor_battery_soc/historic?from=2016-05-19T07:22:22Z&to=2016-05-19T14:22:22Z``

```
{"code":200,"result":[{"value":1000.0,"timestamp":1462269914308,"sent":false,"timeSeriesID":"","deviceID":"sensor_meter_mains"},{"value":1000.0,"timestamp":1462269915033,"sent":false,"timeSeriesID":"","deviceID":"sensor_meter_mains"},{"value":1000.0,"timestamp":1462269948213,"sent":false,"timeSeriesID":"","deviceID":"sensor_meter_mains"},{"value":1000.0,"timestamp":1462269950421,"sent":false,"timeSeriesID":"","deviceID":"sensor_meter_mains"}]}
```

## WebSocket
- To subscribe to one sensor events : `` {command : "subscribe" , topic : "eu/greencom/sensordata/{sensor_name}"}``
- To subscribe to all sensors events : `` {command : "subscribe" , topic : "eu/greencom/sensordata/*"}``