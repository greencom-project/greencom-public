//
// All things necessary for the forecast page
// 

// Start the visualization with requesting the most recent value for each transformer station
function initVisualization() {
	// Get data for the solar energy chart and visualize it
    showSolarEnergyChart();
}

//Checks if there is enough cookie data to reposition the map to a place it was dragged to before
function getCookieData() {
	// Center map on selected element
	var lat = getCookie('mapcenterlat');
	var lng = getCookie('mapcenterlng');
	var zoom = getCookie('mapzoom');
	var id = getCookie('mapObjectId');
	// console.log("map.setView([",lat,",",lng,"],",zoom,")");
	
	if (id) {
		setTimeout(function() {	
			showDetails(id);
		}, 10);
	} else {
		if (lat) {
			if (lng) {
				if (zoom) {
//					console.log("map.setView([",lat,",",lng,"],",zoom,")");
					map.setView([lat,lng],zoom);
				}
			}
		}
	}
}

//Renders the solar energy production chart every time the page is opened
function showSolarEnergyChart() {
	// Set start and end date for the solar data. Show from yesterday until tomorrow
	var dateStart = moment().hours(0).minute(0).seconds(0).subtract(1, 'days').format("YYYYMMDDHH");
	var dateEnd = moment().hours(23).minutes(59).seconds(59).add(1, 'days').format("YYYYMMDDHH");
	
	var callUrl = contextPath + "/NodeServlet?action=solarenergy&start=" + dateStart + "&end=" + dateEnd;
	$.ajax({
		url : callUrl,
		dataType : "json",
		success : function(dataArray) {
			// Build the data array
			var solarPowerData = [];
			var energyPowerData = [];
			for (var i=0; i<dataArray.length; i++) {
				var obj = dataArray[i];
				// Get the ISO datestring from the Database, parse it to a 1970-timestamp
				var isoDate = Date.parse(obj.date);
				// Convert it to a date (in local TZ)
				var dateObject = new Date(isoDate);
				// And take out the timezone offset
				var timestamp = new Date(dateObject.getTime() + dateObject.getTimezoneOffset()*60000);

				solarPowerData.push([timestamp.valueOf(), Math.round((obj.energyInfo.solarRadiation*1000)/1000)]);
				energyPowerData.push([timestamp.valueOf(), Math.round((obj.energyInfo.pvPower*1000)/1000)]);
			}

			// Visualize
			solarEnergyChart.series[0].setData(solarPowerData, false);
			solarEnergyChart.series[1].setData(energyPowerData, false);
			
			// Redraw the chart
			solarEnergyChart.redraw();
		}
	});
}

//Maps the recent load values to the geojson 
function showRecentLoad() {
	// Gets the latest value for the grid sensors and visualizes it. from actua
	$.ajax({
//		url : "http://greencom.fit.fraunhofer.de:3000/recentload",
//		url : contextPath+'/NodeServlet?action=recentload',
//		url : "http://greencom.fit.fraunhofer.de:3000/recentloadactua",
		url : contextPath+'/NodeServlet?action=recentloadactua',
		dataType : "json",
		success : function(loadData) {
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
	
	// Get the recent load of the GC households. 100% = 5000W. from dwh
	$.ajax({
		url : contextPath+'/NodeServlet?action=instantvalues',
//		url: "http://greencom.fit.fraunhofer.de:3000/instantvalues",
		dataType : "json",
		success : function(loadData) {
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

//Gets called when a timespan is selected
function showDetails(mapObjectId, timeframe) {	
		
	// Save object ID
	if (!mapObjectId) {
		mapObjectId = getCookie("mapObjectId");
	}
	
	var timeframeFromCookie = getCookie("timeframePrediction");
	
	// If no timeframe, take today. Else take the given
	if (!(timeframe) || (timeframe == "")) {
		if (!(timeframeFromCookie) || (timeframeFromCookie == "")) {
			timeframe = "next4hours";
		} else {
			timeframe = timeframeFromCookie;
		}
	}
	
	setCookie('timeframePrediction', timeframe);
	
	// This is today. Converted to UTC, as in DWH everything is in UTC
	var from = moment().utc();
	var to = moment().utc();
	
	// Create proper timeframe
	if (timeframe == 'nexthour') {
		to = to.add(1, 'hours').utc();
	} else if (timeframe == 'next4hours') {
		to = to.add(4, 'hours');
	} else if (timeframe == 'next6hours') {
		to = to.add(6, 'hours');
	} else if (timeframe == 'next12hours') {
		to = to.add(12, 'hours');
	} else if (timeframe == 'next24hours') {
		to = to.add(24, 'hours');
	} else if (timeframe == 'next48hours') {
		to = to.add(48, 'hours');
	}
	
	// Set proper text when a date is selected
	var htmlObjectWithDateText = '#datePickerLink' + timeframe;
	$('#datePickerText')[0].value = $(htmlObjectWithDateText)[0].textContent;
		
	// Get and visualize the data for this object and time. Pass moment-objects to the function
	getAndDisplayDataOfObject(mapObjectId, from, to);
}

function getAndDisplayDataOfObject(mapObjectId, from, to) {
	// Get the data of the objects from the JSON
	var dataForMapObject = mapObjectId_to_sensorIds_json[mapObjectId];
	
	var objectType = dataForMapObject.type;
	var text = "";
	if (objectType == "radial") {
		text = "the radial";
	} else if (objectType == "transformer") {
		text = "transformer station";
	} else if (objectType == "house") {
		text = "the GreenCom home";
	}
	// Set the heading of the page
	d3.select('#headingForecastId').html("Forecasted load data for "+text+" "+mapObjectId);
	
	// -------------------------------------------------------------------
	// Gets the predicted load for a map object and visualizes it
	// -------------------------------------------------------------------
	
	var time = new Date();
	var loadUrl = "";
	if (objectType == 'house') { // get from DWH
		// As there is no prediction data from Actua for the houses, take the last weeks data from the DWH
//		var from = new moment().utc().subtract(7, "days");
//		var to = new moment().utc().subtract(7, "days").add(4, "hours");
//		dateFormatString = "YYYYMMDDHHmmss";
//		fromString		 = from.format(dateFormatString).toString();
//		toString		 = to.format(dateFormatString).toString();
		// Take predictions for other objects
		loadUrl = contextPath + "/NodeServlet?action=getloadpredictionactua&sensorid=" +  dataForMapObject.predictionsensor + "&time=" + time.toISOString();
//		loadUrl ="http://localhost:3000/getloadpredictionactua/" + dataForMapObject.predictionsensor + "/" + time.toISOString();
		forecastChart.setTitle({text: "Energy consumption forecast for a GreenCom home"});
	} else if ((objectType == 'radial') || (objectType == 'transformer')) {  // get from ACTUA
		loadUrl = contextPath + "/NodeServlet?action=getloadpredictionactua&sensorid=" + mapObjectId + "&time=" + time.toISOString();
//		loadUrl ="http://localhost:3000/getloadpredictionactua/" + mapObjectId + "/" + time.toISOString();
		forecastChart.setTitle({text: "Energy consumption forecast for a GreenCom grid element"});
	}
	
	// Send out...
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(data) {
			var json = JSON.parse(data);
			visualizePredictionData(JSON.parse(json.val));
		}
	});
}

//Need this function to step out of other functions
function goThereAndDoNothing() {
	var lalala = 'zero';
}

// Does the conversion from JSON to timed Array and puts it to the graph
function visualizePredictionData(json) {

	if ((json == "") || (json == null)) {
		alert("There was a server error and no data was delivered. Try again or select a different object.");
		goThereAndDoNothing();
	}
	
	// Go through result
	var predictionData = [];
	for (var key in json) {
//		var date = new Date(key);
//		var timestamp = date.getTime();
//		var value = json[key]["0"].LoadValue;
//		predictionData.push([timestamp, value]);
		
		var date = new moment(key).utc();
		var timestamp = date.valueOf();
		var value = json[key]["0"].LoadValue;
		value = Math.floor(value * 100) / 100;;
		predictionData.push([timestamp, value]);
	}
	predictionData.sort(function(a,b){return a[0]-b[0]});
	
	var dateFormatString = "YYYYMMDDHH";
	var from  = moment(predictionData[0][0]).utc().format(dateFormatString);					// simulate price data from the first
	var to    = moment(predictionData[predictionData.length-1][0]).add(3, 'hours').utc().format(dateFormatString);  // Also show recent hour
	
	// Get the energy prices
	var priceData = [];
	var loadUrl = contextPath + "/NodeServlet?action=energyprices&start=" + from + "&end=" + to;
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
			visualizeLoadAndPriceData(predictionData, priceData);
		}
	});	
}

//Takes the two data rows and updates the visualization
function visualizeLoadAndPriceData(predictionData, priceData) {
	//Push the data into the chart when it is ready
	forecastChart.series[0].setData(predictionData, false);
	forecastChart.series[1].setData(priceData, false);

	// Remove Loading text if there is data to display
	forecastChart.hideLoading();

	// Redraw the chart
	forecastChart.redraw();
}

//Reload of data
//Take the lastly picked mode and the recently put timespan
function reload() {
	showDetails(null, null);	
}