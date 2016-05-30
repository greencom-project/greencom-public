# Gateway local Web GUI

This component includes the local GUI for GreenCom software which uses LocalWebAPI component to receive the data of the sensors and show it with different widgets to user.
Different widgets has been provided for GUI including:

## Widgets

- **battery** : shows the SOC of the battery by calling the API with the percentage and the color depends one the value (red, orange, blue, green) 
- **Clock** : providing the clock on the navbar
- **Weather** : provides the current and forecast of the weather using Yahoo service - Based on the tomorrow forecast, it will give user a recommendation for consumption
- **TextValue** : shows the value of the sensors by calling the API and based on the value, explains the situation to user (The recommendation part is hard coded in the dash-text-measure.js based on each different sensor)
- **GC-Condition** : Shows the whole situation to user and recommends something (The recommendation part is hard coded in the dash-greencom-conditon.js)
- **Historic Chart** : chart which demonstrate the graph for historic data that receives from Mongo (by calling API), with the data picker widget inside of it.
- **Device Switch** : switch for controlling the devices by posting to API

Most of the parameters related to each widget can be set in the HTML tag creating the widget.

## Structure

- The main technologies for GUI are AngularJS, JQuery and Bootstrap.
- Local GUI consist of the 3 views of home, graphs and control (all in the views folder). All of this views compile in the index.html file and for the performance matter, by changing the view, while page will not be re-loaded. (this has been done by angular-route)
- All of the libraries and CSS files are mentioned only in index file
- There is one custom CSS files including all CSS classes in the lib/css

## Libraries

 ALL libraries for widgets that has been used are:
 - "bootstrap": "^3.3.6",
 - "angular": "^1.5.5",
 - "font-awesome": "^4.6.1",
 - "weather-icons": "^2.0.10",
 - "angular-bootstrap" : "^1.3.2",
 - "restangular": "^1.5.2",
 - "lodash": "^4.11.2" ,
 - "angular-bootstrap-switch": "^0.5.0",
 - "bootstrap-switch" : "3.3.2",
 - "d3": "^3.5.17",
 - "angular-nvd3": "^1.0.7",
 - "angular-animate": "^1.5.5",
 - "angular-ui-clock": "^0.5.1",
 - "angular-route": "^1.5.5"





