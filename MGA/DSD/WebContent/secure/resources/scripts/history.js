/**
 * All JS functions specific to the history page
 */

// Checks if there is enough cookie data to reposition the map to a place it was dragged to before
function getCookieData() {
	// Center map on selected element
	var lat  = getCookie('mapcenterlat');
	var lng  = getCookie('mapcenterlng');
	var zoom = getCookie('mapzoom');
	var id = getCookie('mapObjectId');
	//console.log("map.setView([",lat,",",lng,"],",zoom,")");
	
	if (id) {
		setTimeout(function() {	
			showDetails(id);
		}, 10);
	} else {
		if (lat) {
			if (lng) {
				if (zoom) {
					map.setView([lat,lng],zoom);
				}
			}
		}
	}
}

//Initializes the datepicker object
function initDatepicker() {
	$("#gcDatepicker").datepicker(
		{		
			defaultDate: new Date(),
			dateFormat: "yy-mm-dd",
			minDate: new Date(2014, 0, 1),
			maxDate: "+0d"
		});
	$("#gcDatepicker").datepicker( "setDate", "+0");

	$("#gcDatepicker2").datepicker(
		{
			defaultDate: new Date(),
			dateFormat: "yy-mm-dd",
			minDate: new Date(2014, 0, 1),
			maxDate: "+0d"
		});
	$("#gcDatepicker2").datepicker( "setDate", "+0");
}

//Maps the recent load values to the geojson 
function showRecentLoad() {
	
	// Gets the latest value for ALL sensors and visualizes it	
	$.ajax({
//		url : "http://greencom.fit.fraunhofer.de:3000/recentload",
//		url : contextPath+'/NodeServlet?action=recentload',
//		url : "http://greencom.fit.fraunhofer.de:3000/recentloadactua",
		url : contextPath+'/NodeServlet?action=recentloadactua',
		dataType : "json",
		success : function(loadData) {
//			console.log("recent load from actua data", loadData);
			// This code takes the percent-values from the ACTUA service
			for (key in loadData) {
				for (var i=0; i < loadData[key].length; i++) {
					var id = loadData[key][i].ID;
					var load =loadData[key][i].LoadValue; 
					
					//	Color the map object with its color value
					svg.select('#t'+id)  // Be aware of the t-Prefix!
				        .transition()
				        .duration(200)
				        .attr("stroke", function(d){ return color(load); });
				}
			}
		}
	});
	
	// Get the recent load of the GC households. 100% = 5000W
	$.ajax({
		url : contextPath+'/NodeServlet?action=instantvalues',
//		url: "http://greencom.fit.fraunhofer.de:3000/instantvalues",
		dataType : "json",
		success : function(loadData) {
//			console.log("recent load from dwh data", loadData);
			// This code takes the percent-values from the ACTUA service
			for (key in loadData) {
				var loadItem = JSON.parse(loadData[key]);
				var loadValue = loadItem.LastValue;
				var loadPercent = 0;
				if (loadValue == 0) {
					loadPercent = 0;
				} else {
					loadPercent = loadValue/5000 * 100;
				}
				//	Color the map object with its color value
				svg.select('#t'+key)  // Be aware of the t-Prefix!
			        .transition()
			        .duration(200)
			        .attr("stroke", function(d){ return color(loadPercent); });
				
			}
		}
	});
}

// ****************************************************
// GET AND DISPLAY LOAD DATA
// ****************************************************

//Gets called when a timespan is selected
function showDetails(mapObjectId, timeframe) {	
		
	// Save object ID
	if (!mapObjectId) {
		var mapObjectId = getCookie("mapObjectId");
	}
	
	var timeframeFromCookie = getCookie("timeframe");
	
	// If no timeframe, take today. Else take the given
	if (!(timeframe) || (timeframe == "")) {
		if (!(timeframeFromCookie) || (timeframeFromCookie == "")) {
			timeframe = "today";
		} else {
			timeframe = timeframeFromCookie;
		}
	}
	
	setCookie('timeframe', timeframe);
	setCookie('timeframesource', 'frame');
	
	// This is today. Converted to UTC, as in DWH everything is in UTC
	var from = moment().hours(0).minute(0).seconds(0).utc();
//	var to = moment().hours(23).minutes(59).seconds(59).utc();
	var to = moment().utc();  // Just up to NOW
	
	// Create proper timeframe
	if (timeframe == 'lasthour') {
		from = moment().subtract(1, 'hours'); // from.subtract('hours', 2);  // Subtract one hour more, because of UTC
	} else if (timeframe == 'last6hours') {
		from = moment().subtract(7, 'hours'); // from.subtract('hours', 7);
	} else if (timeframe == 'yesterday') {
		from = from.subtract(1, 'days');
	} else if (timeframe == 'last7days') {
		from = from.subtract(6, 'days');
	} else if (timeframe == 'last30days') {
		from = from.subtract(29, 'days');
	} else if (timeframe == 'all') {
		from = from.subtract(150, 'days');
		// from = moment("Jan 1, 2014");  // Start Jan 1st, 2014
	}
	
	// Set proper text when a date is selected
	var htmlObjectWithDateText = '#datePickerLink' + timeframe;
	$('#datePickerText')[0].value = $(htmlObjectWithDateText)[0].textContent;
	// Get and visualize the data for this object and time. Pass moment-objects to the function
	getAndDisplayDataOfObject(mapObjectId, from, to);
}

//Gets called when dates were picked
function showDetailsSpan() {
	var mapObjectId = getCookie("mapObjectId");
	var from = $("#gcDatepicker").datepicker("getDate");
	var to   = $("#gcDatepicker2").datepicker("getDate");
	from = moment(from).utc();
	to = moment(to).hours(23).minutes(59).seconds(59).utc();
	
	// catch if today is selected and request only the data up to the recent time
	if (to.diff(moment().utc(), 'days') == "0") {
		to = moment().utc();
	}
	
	setCookie('timeframesource', 'span');
	
	// Always show from 00:00:00 until 23:59:59
	// Get and visualize the data for this object and time
	getAndDisplayDataOfObject(mapObjectId, from, to);
}

// Reload of data
// Take the lastly picked mode and the recently put timespan
function reload() {
	if (getCookie('timeframesource') == 'frame') {
		showDetails(null, null);	
	} else if (getCookie('timeframesource') == 'span') {
		showDetailsSpan();
	}
}

// Gets and displays the data for a selected map object for a selected time
// Goes through all available sensors for the selected map object id
// Takes the same time intervals for all desired data
function getAndDisplayDataOfObject(mapObjectId, from, to) {
	var objectTypeName = getCookie('objectTypeName');
	var text = "";
	if (objectTypeName == "radial") {
		text = "the radial";
	} else if (objectTypeName == "transformer") {
		text = "transformer station";
	} else if (objectTypeName == "house") {
		text = "the GreenCom home";
	}
	// Set the heading of the page
	d3.select('#headingHistoryId').html("Historical data for " + text + " " + mapObjectId);
	
	// Considering data visualization
	// For ALL objects, a load data chart is always displayed for the requested time span.
	// This load is displayed together with energy prices. This comes from 2 data sources
	// For ALL objects, the weather data chart is displayed for the requested time span. This comes from 1 data source
	// For houses, optional charts are displayed, based on the sensors registered for that house 
	
	// Try to destroy the old stuff, when new data is starting to be requested
	try {energyDataChart.get('watt_axis').remove();} catch(e) {}
	try {energyDataChart.get('percent_axis').remove();} catch(e) {}
	try {energyDataChart.destroy();} catch(e) {}   // The main energy data goes here: Energy price, load of SmartMeter, HP, and PV
	try {loadDataChart.destroy();} catch(e) {}      // Created for additional information, such as kettles, lamps, etc.
	try {weatherDataChart.destroy();} catch(e) {}   // The weather data chart: Temperature, Humidity, and Windspeed
	try {aggregatedDataChart.destroy();} catch(e) {}
	try {document.getElementById("hereGoTheAdditionalChartsId").innerHTML = "";} catch(e) {}
	
	// Get the data of the objects from the JSON
	var dataForMapObject = mapObjectId_to_sensorIds_json[mapObjectId];
	
	// Generate energy data chart. This is always combined with energy prices.
	energyDataChart = new Highcharts.Chart(energyDataChartOptions);
	if (objectTypeName == "house") {  				// House. Values will be in Watts, so put Watts scale.
		energyDataChart.setTitle({text: "Home load consumption value and energy prices over time"});
		energyDataChart.addAxis(energyDataChartWattsAxis, false);
	} else if (objectTypeName == 'radial') {			// Radial. Grid data from Actua, values will be in percent.
		energyDataChart.setTitle({text: "Radial load and energy prices over time"});
		energyDataChart.addAxis(energyDataChartPercentAxis, false);
	} else if (objectTypeName == 'transformer') {	    // Transformer station. Grid data from Actua, values will be in percent.
		energyDataChart.setTitle({text: "Transformer station load and energy prices over time"});
		energyDataChart.addAxis(energyDataChartPercentAxis, false);
	}
	var chartWidth = energyDataChart.plotWidth;   // Width of all plots. Used to figure out the number of items to display
	energyDataChart.showLoading('Requesting and processing data...');
	getAndVisualizeEnergyPrices(from, to);  // Always show energy prices, start getting them
	// Start weather visualization
	// This always combines several data rows, such as temperature, windspeed, and humidity
	weatherDataChart = new Highcharts.Chart(weatherDataChartOptions);
	weatherDataChart.showLoading('Requesting and processing data...');
	getHistoricalWeatherData(from, to, chartWidth); // Or for the whole requested range
	// Start data visualizations, if there are entries in the JSON
	var count = 0;
	if (dataForMapObject.sensors) {
		for (key in dataForMapObject.sensors) {
			// Go through all sensors
			if (dataForMapObject.sensors[key].sensor != "x") {
				if ((key == "smartmeter") || (key == "HP") || (key == "PV")) {
					// Put HP,  PV, smartmeter all into the same diagram
					getLoadDataOfSensor(dataForMapObject, key, from, to, chartWidth);
				} else if (key == "gridload") {
					// Visualize gridload
					getLoadDataOfSensor(dataForMapObject, key, from, to, chartWidth);
				} else if (dataForMapObject.sensors[key].diagramtype == "load") {
					// Put all other load diagrams into separate charts. This would hold for Stereo, Lamps, PC, Kettle, etc.
					// This is not used for official houses, due to privacy. Only for the Tyndall installations.
					var json = dataForMapObject.sensors[key];        // Store data
					var chartId = "additionalChartsChartId"+count;   // Add a chart to the page
					$("#hereGoTheAdditionalChartsId").append('<div id="additionalChartsRowId'+count+'" class="row myGChidden"> <div id="'+chartId+'"></div> </div>');
					showLoadValues(json, chartId, count, from, to, chartWidth);   // And get and visualize data for that chart
					count++;
				} else if (dataForMapObject.sensors[key].diagramtype == "temperature") {
					// Show a weather diagram
					// not yet implemented, but should be used for temperatue or occupancy
				} else if (dataForMapObject.sensors[key].diagramtype == "occupancy") {
					// Show an occupancy diagram
					// not yet implemented, but should be used for temperatue or occupancy
				}
			} 
		}
	}
}

// Requests the load data.
function getLoadDataOfSensor(dataForMapObject, key, from, to, chartWidth) {
	var objectType = dataForMapObject.type;
	var mapObjectId = dataForMapObject.sensors[key].sensor;
//	// Setting and reloads for specific object types
//	if (objectType == 'house') {					// House
//		energyDataChart.setTitle({text: "Home load consumption value and energy prices over time"});
//	} else if (objectType == 'radial') {			// Radial
//		energyDataChart.setTitle({text: "Radial load and energy prices over time"});
//	} else if (objectType == 'transformer') {	    // Transformer station
//		energyDataChart.setTitle({text: "Transformer station load and energy prices over time"});
//	}
	
	var dateFormatString = "";
	var fromString		 = "";
	var toString		 = "";
	var loadUrl			 = "";
	if (objectType == 'house') {
		// Gets data from the DWH. The DWH will not contain data for the radials and transormer stations
		// This is called for all sensors except transformers and radials 
		dateFormatString = "YYYYMMDDHHmmss";
		fromString		 = from.format(dateFormatString).toString();
		toString		 = to.format(dateFormatString).toString();
//		loadUrl = "http://localhost:3000/sensorvalues/" + dataForMapObject.sensors[key].sensor + "/" + fromString + "/" + toString;
		loadUrl = contextPath + "/NodeServlet?action=sensorvalues&sensorid=" + mapObjectId + "&start=" + fromString + "&end=" + toString;
	} else if ((objectType == 'radial') || (objectType == 'transformer')) {
		// Gets historical data from the PowerFactory at the ACTUA premises
		// This is called for transformer and radial data
		dateFormatString = "YYYY-MM-DDTHH:mm:ss";
		fromString		 = from.format(dateFormatString).toString();
		toString		 = to.format(dateFormatString).toString();
		
//		loadUrl = "http://localhost:3000/sensorvaluesactua/" + dataForMapObject.sensors[key].sensor + "/" + fromString + "/" + toString;
		loadUrl = contextPath + "/NodeServlet?action=sensorvaluesactua&sensorid=" + mapObjectId + "&start=" + fromString + "&end=" + toString;
		
		// Start getting the special aggregated data, if the user is an aggregator
		if (roleOfUser == "aggregator") {
			if (objectType == 'transformer') {
				var geoData = getObjectIdGeoData(mapObjectId);
				if (geoData.properties.hasHouses == "yes") {
					getAggregatedData(mapObjectId, from, to, chartWidth);
				}
			}
		}
	}
	
	// Start getting the data
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(responseData) {
			visualizeLoadDataOfSensor(dataForMapObject, key, responseData, from, to, chartWidth);
		},
		error: function(xhr, ajax, err) {
			console.log("error getting data", xhr, ajax, err);
		}
	});
}

// Gets the data from the MGA data rows in the DWH
function getAggregatedData(mapObjectId, from, to, chartWidth) {
	dateFormatString = "YYYYMMDDHHmmss";
	fromString		 = from.format(dateFormatString).toString();
	toString		 = to.format(dateFormatString).toString();
	
	
	$("#hereGoTheMGAChartsId").append('<div id="mgaDataChartId" class="row"></div>');
	aggregatedDataChart = new Highcharts.Chart(aggregatedDataChartOptions);
	aggregatedDataChart.showLoading("Requesting and processing data...");
	
//	loadUrl = "http://localhost:3000/mgavaluesdwh/" + mapObjectId + "/" + fromString + "/" + toString;
	loadUrl = contextPath + "/NodeServlet?action=mgavaluesdwh&sensorid=" + mapObjectId + "&start=" + fromString + "&end=" + toString;
	// Start getting the data
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(responseData) {
			try {
				aggregatedDataChart.hideLoading();
				var json = JSON.parse(responseData);
				var jsonValues = json.Values;
				var dataArray = [];
				for (key in jsonValues) {
					dataArray.push([Date.parse(jsonValues[key].Date), jsonValues[key].Value]);
				}
				dataArray.sort(function(a,b){return a[0]-b[0]});
				// Add the data to the proper chart
				aggregatedDataChart.series[0].setData(dataArray, true);
			} catch (e) {}
		},
		error: function(xhr, ajax, err) {
			console.log("error getting mga data", xhr, ajax, err);
		}
	});
}

// Gets called with the load data, also orders weather and price data
// Start sending out new requests when the basic load data has arrived
function visualizeLoadDataOfSensor(dataForMapObject, key, responseData, from, to, chartWidth) {	
	energyDataChart.hideLoading();
	// Extract the json data from the right place
	var jsonValues = null;
	var objectType =  dataForMapObject.type;
	var dataRowName = "";
	var axisName = "";
	var valueSuffix = "";
	// Detect the type of object for which the chart should be displayed
	if (objectType == 'house') {   // For DWH results
		dataRowName = dataForMapObject.sensors[key].name;
		axisName = "watt_axis";
		valueSuffix = " W";
		var prelim = JSON.parse(responseData);
		jsonValues = prelim.Values;
		// jsonValues now is an array of objects, always containing Date and Value
	} else if ((objectType == 'radial') || (objectType == 'transformer')) {    // For ACTUA results
		// Keep in mind the UTC conversion
		if (objectType == 'radial') {
			dataRowName = "Load in % for the radial " +  dataForMapObject.sensors[key].sensor;
		} else {
			dataRowName = "Load in % for the transformer station " +  dataForMapObject.sensors[key].sensor;
		}
		axisName = "percent_axis";
		valueSuffix = " %";
		var prelim = JSON.parse(responseData);
		var json = JSON.parse(prelim.val);
		jsonValues = [];
		for (jsonkey in json) {
			jsonValues.push({"Date":jsonkey,"Value":json[jsonkey][0].LoadValue});
		}
	}
		
	
	// Catch empty result sets. As the latest value is always returned, check if the json is <= 1
	if (jsonValues.length >= 1) {
		// Just load as many values as can be displayed by the plot !!!
		var intervalWidth = 1;
		if (jsonValues.length > (chartWidth * 2)) {
			intervalWidth = Math.floor(jsonValues.length / chartWidth);
			// If we take only each intervalWidth'-value, it's enough to fill up the chart
		}

		// Build the arrays of data to be visualized, but catch exceptional values (Value > 100 for radials)
		var loadData = [];
		var stepInInterval = 1;
		var intermediateValue = 0;
		// Go through all the values
		for (var i=0; i<jsonValues.length; i++) {
			var value = jsonValues[i].Value;

			// Save the value directly or cumulate it until the interval width is exaggerated
			if (stepInInterval == intervalWidth) {
				value = (value + intermediateValue) / intervalWidth;
				// Com,plicated way to get the same timestamp from a given UTC time in all browsers...
				var da = jsonValues[i].Date.split(/[^0-9]/);
				var dateUTC = new Date(Date.UTC(da[0],da[1]-1,da[2],da[3],da[4],da[5]));
				var timestamp = Date.parse(dateUTC);
				loadData.push([timestamp, Math.round(value*1000)/1000]); // Round value to 3 decimals
				stepInInterval = 1;
				intermediateValue = 0;
			} else {
				intermediateValue += value;
				stepInInterval++;
			}
		}
		loadData.sort(function(a,b){return a[0]-b[0]});
		addToEnergyChart(loadData, dataRowName, axisName, valueSuffix);
	}
}

function addToEnergyChart(loadData, name, axisName, valueSuffix) {
	// Add a series for any load data row that comes in
	energyDataChart.addSeries({
        type: 'spline',
        lineWidth: 2,
        name: name,
        data: loadData,
        color: '#000000',
        yAxis: axisName,		// Assign data to the matching axis
        tooltip: {
        	valueSuffix: valueSuffix
        },
        marker: {
            enabled: false
        }
    }, true);
}

// Gets the energy prices and visualizes them in the main load diagram 
function getAndVisualizeEnergyPrices(from, to) {
	// Dates for energy and weather plot, depending on the first and last load data that was received
	var dateFormatString = "YYYYMMDDHH";
	var from2 = from.utc().format(dateFormatString);			      // simulate price data from the first
	var to2 = to.clone();
	to2 = to2.add(3, 'hours').utc().format(dateFormatString);    // Also show recent hour
	
	// Get the energy prices
	var priceData = [];
	var loadUrl = contextPath + "/NodeServlet?action=energyprices&start=" + from2 + "&end=" + to2;
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(data) {
			var json = JSON.parse(data);
			for (key in json) {
				var price = json[key].price;
				price = price.replace(",", ".");  // Prices with , or . as decimal sign
				var time = moment(json[key].date).utc().valueOf();
				if (Number(price)) {	// Set unknown values to null
					priceData.push([time, Number(price)]);
				} else {
					priceData.push([time, null]);
				}
			}
			
			energyDataChart.hideLoading();
			energyDataChart.addSeries({
	            step: 'left',
	            lineWidth: 2,
	            name: 'Energy price in EUR per MWh',
	            data:  priceData,
	            color: '#9900dd',
	            yAxis: 'price_axis',		// Assign data to the matching axis
	            tooltip: {
	            	valueSuffix: ' EUR/MWh'
	            },
	            marker: {
	                enabled: false
	            }
	        }, true);
		}
	});
}

//****************************************************
// GET AND DISPLAY LOAD DATA - END
//****************************************************

//****************************************************
//GET AND DISPLAY ADDITIONAL DATA
//****************************************************

// Function generates a chart for each sensor id given to it
function showLoadValues(json, chartId, count, from, to, chartWidth) {	
	// Set the Id where this chart should be rendered to
	loadDataChartOptions.chart.renderTo = chartId;
	// Add data to the chart
	loadDataChartOptions.title.text = json.name;
	// And create it
	loadDataChart = new Highcharts.Chart(loadDataChartOptions);
	// Show loading text
	loadDataChart.showLoading("Requesting and processing data...");
	// And set it visible
	$("#additionalChartsRowId"+count).removeClass('myGChidden');
	
	// Now get the data
	var dateFormatString = "YYYYMMDDHHmmss";
	var fromString		 = from.format(dateFormatString).toString();
	var toString		 = to.format(dateFormatString).toString();
	var sensorId 		 = json.sensor;
	
	// Maybe add some hours here just to be sure
	var loadUrl = contextPath + "/NodeServlet?action=sensorvalues&sensorid=" + sensorId + "&start=" + fromString + "&end=" + toString;
	$.ajax({
		url : loadUrl,
		dataType : "json",
		success : function(json) {
			if (json.Values.length <= 1) {
				loadDataChart.hideLoading();
				loadDataChart.showLoading('Sorry, but there is no data for the selected time period');
			} else {
				if (json.Values.length == 11000) {
					alert("The amount of values requested for the SmartMeter sensor exceeded the server limit. Please try again with a smaller range!");
				}
				// Just load as many values as can be displayed by the plot !!!
				var intervalWidth = 1;
				if (json.Values.length > (chartWidth * 2)) {
					intervalWidth = Math.floor(json.Values.length / chartWidth);
					// If we take only each intervalWidth'-value, it's enough to fill up the chart
				}
				
				// Build the arrays of data to be visualized, but catch exceptional values (Value > 100 for radials)
				var loadData = [];
				var stepInInterval = 1;
				var intermediateValue = 0;
				// Go through all the values
				for (var i=0; i<json.Values.length; i++) {
					var value = json.Values[i].Value;
	
					// Save the value directly or cumulate it until the interval width is exaggerated
					if (stepInInterval == intervalWidth) {
						value = (value + intermediateValue) / intervalWidth;
						loadData.push([Date.parse(json.Values[i].Date), Math.round(value*1000)/1000]); // Round value to 3 decimals
						stepInInterval = 1;
						intermediateValue = 0;
					} else {
						intermediateValue += value;
						stepInInterval++;
					}
				}
				
				// Attach data to the chart
				loadDataChart.series[0].setData(loadData, false);
				// Remove Loading text if there is data to display
				loadDataChart.hideLoading();
				// Redraw the chart
				loadDataChart.redraw();
			}
		},
		error: function(xhr, ajax, err) {
			alert("There was an error transferring the data. Most probably, you requested too much data.");
			loadDataChart.hideLoading();
			// Redraw the chart
			loadDataChart.redraw();
			loadDataChart.showLoading('No data due to an error.');
		}
	});
}
//****************************************************
//GET AND DISPLAY ADDITIONAL DATA - END
//****************************************************

//****************************************************
//GET AND DISPLAY WEATHER DATA
//****************************************************
function getHistoricalWeatherData(from, to, chartWidth) {
	var dateFormatString = "YYYYMMDDHH";
	var fromString		 = from.format(dateFormatString).toString();
	var toString		 = to.format(dateFormatString).toString();
	
	// Maybe add some hours here just to be sure
	var loadUrl = contextPath + "/NodeServlet?action=historicalweather&start=" + fromString + "&end=" + toString;
	$.ajax({
		url : loadUrl,
		dataType : "json",
		success : function(json) {
			visualizeHistoricalWeatherData(json, chartWidth);
		}
	});
}

function visualizeHistoricalWeatherData(json, chartWidth) {
//	console.log('weather', json);
	var tempSeries = [];
	var rainSeries = [];
	var humySeries = [];
	var windSeries = [];
	
	for (var i=0; i<json.length; i++) {
		var timestamp = moment(json[i].date).valueOf();
		var temp = parseFloat(json[i].weather.temp);
//		var rain = parseFloat(json[i].weather.rain);
		var humy = parseFloat(json[i].weather.humy);
		var wind = parseFloat(json[i].weather.wspdm);
		
		if (!isNaN(temp)) {
			if ( (temp > 50) || (temp < -30) ) {
				tempSeries.push([timestamp, null]);
			} else {
				tempSeries.push([timestamp, temp]);
			}
		} else {
			// Empty values MUST BE set to 'null', to allow the line to be drawn through
			tempSeries.push([timestamp, null]);
		}
		
//		if (!isNaN(rain)) {
//			rainSeries.push([timestamp, rain]);
//		} else {
//			// Empty values MUST BE set to 'null', to allow the line to be drawn through
//			rainSeries.push([timestamp, null]);
//		}
		
		if (!isNaN(humy)) {
			if ( (humy > 100) || (humy < 0) ) {
				humySeries.push([timestamp, null]);
			} else {
				humySeries.push([timestamp, humy]);
			}
		} else {
			// Empty values MUST BE set to 'null', to allow the line to be drawn through
			humySeries.push([timestamp, null]);
		}
		
		if (!isNaN(wind)) {
			windSeries.push([timestamp, wind]);
		} else {
			// Empty values MUST BE set to 'null', to allow the line to be drawn through
			windSeries.push([timestamp, null]);
		}
	}
	
	//Push the data into the chart when it is ready
	weatherDataChart.series[0].setData(tempSeries, false);
	weatherDataChart.series[1].setData(windSeries, false);
	weatherDataChart.series[2].setData(humySeries, false);
//	weatherDataChart.series[3].setData(rainSeries, false);
	
	// Catch empty result sets. As the latest value is always returned, check if the json is <= 1
	if ((tempSeries.length <= 1) && (windSeries.length <= 1) && (humySeries.length <= 1)) {
		weatherDataChart.hideLoading();
		weatherDataChart.showLoading('Sorry, but there is no data for the selected time period');
	} else {  // Everything OK
		// Remove Loading text if there is data to display
		weatherDataChart.hideLoading();
		// Redraw the chart
		weatherDataChart.redraw();
	}
}
//****************************************************
//GET AND DISPLAY WEATHER DATA - END
//****************************************************