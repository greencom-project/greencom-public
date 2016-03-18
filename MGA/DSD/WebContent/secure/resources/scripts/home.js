/**
 * All JS functions specific to the home page
 */

//Checks if there is enough cookie data to reposition the map to a place it was dragged to before
function getCookieData() {
	// Center map on selected element
	var lat = getCookie('mapcenterlat');
	var lng = getCookie('mapcenterlng');
	var zoom = getCookie('mapzoom');
	var id = getCookie('mapObjectId');
//	console.log("map.setView([",lat,",",lng,"],",zoom,")");
	if (id) {
		setTimeout(function() {	
			furDetailSupplier.showDetails(id);
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
	
	// Checks if the weather data should be updated
	var showWeather = getCookie('showWeather');
	var regularUpdateOfWeather;
	if (showWeather == 'true') {
		regularUpdateOfWeather = setInterval(function(){showRecentWeather();}, 61000);
	} else if (showWeather == 'false') {
	    // Take back timer
	    clearInterval(regularUpdateOfWeather);
	}
	
	// Selects the checkboxes, but waits a bit until the checkboxes are created
	if (roleOfUser == "operator") {
		setTimeout(function() {	
			var checkboxes = getCookie("selectedmicrogrids");
			if (checkboxes) {
				globalCheckboxArray = checkboxes.split(",");
							
				for (var i=0; i<globalCheckboxArray.length; i++) {
					document.getElementById("checkboxid_"+globalCheckboxArray[i]).checked=true;
				}
			}
		}, 100);
	}
	
}

// Get the recent Weather conditions and display them
function showRecentWeather() {
	$.ajax({
		url : contextPath+'/NodeServlet?action=recentweather',
		dataType : "json",
		success : function(loadData) {
			parsed_json = loadData[0].recentWeather;
							
			var location = parsed_json['display_location']['city'];
			var temp_c = parsed_json['temp_c'];
			var feelsLike_Celsius = parsed_json['feelslike_c'];
			var weathericon_url = parsed_json['icon_url'];
			var wind_direction = parsed_json['wind_dir'];
			var wind_string = parsed_json['wind_string'];
			var wind_speed = parsed_json['wind_kph'];

			$("#weatherDetailsAreaId").html('Current weather conditions in Fur:<br/>' + temp_c +'&deg;C, felt like '+feelsLike_Celsius+'&deg;C<br/><br/> Wind conditions:<br/> '+wind_string+' wind at '+wind_speed+' km/h from '+wind_direction+' direction<br/><img src="'+weathericon_url+'"></img>');			
		}
	});
}

// Maps the recent load values to the geojson 
function showRecentLoad() {
	// Gets the latest value for ALL sensors and visualizes it	
	$.ajax({
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
			
// ----- Old, relies on recentload service from fake data from the DWH ----
//			// Go through the load data and build a dict that directly connects a sensorId to its load value
//			var sensorIdToLoadValue = {};
//			for (key in loadData.Data) {
//				// { SensorID : LastValue, ...}
//				sensorIdToLoadValue[loadData.Data[key].SensorID] = loadData.Data[key].LastValue;
//			}
//						
//			// Go through map objects and calculate the color value for them
//			// Color objects by the values of the sensors
//			var locationId = '';
//			var colorValue;
//			for (key in geoDataCollection.features) {
//				locationId = geoDataCollection.features[key].id;
//				// Update load value in element table
//				if (document.getElementById('loadValueInElementTable20082')) {
////					var loadRoundedToTwoDigits = Math.floor(sensorIdToLoadValue[mapObjectIDToSensorID[locationId]] * 100) / 100;
//					var loadRoundedToTwoDigits = Math.floor(sensorIdToLoadValue[mapObjectId_to_sensorIds_json[locationId].sensor] * 100) / 100;
//					document.getElementById('loadValueInElementTable' + locationId).innerHTML = loadRoundedToTwoDigits.toString();
//				}
//
//				if (locationId.indexOf('house_') > -1) {			// Color Houses
//	 				if (["house_house1", "house_house2", "house_house3", "house_house4", "house_house5"].indexOf(locationId) > -1) {
//	 					// Color the house by the value of the matching sensor, if it one of the fake houses
////	 					colorValue = sensorIdToLoadValue[mapObjectIDToSensorID[locationId]];
//	 					colorValue = sensorIdToLoadValue[mapObjectId_to_sensorIds_json[locationId].sensor];
//	 				} else {
//	 					// As the load for the lego houses is 0..20, multiply by 5 to make it fit the the color scheme
////	 					colorValue = sensorIdToLoadValue[mapObjectIDToSensorID[locationId]] * 5;
//	 					colorValue = sensorIdToLoadValue[mapObjectId_to_sensorIds_json[locationId].sensor] * 5;
//	 				};
//				} else if (locationId.indexOf('T1U') > -1) {		// Color Radials
//					// Color the radial with the value of the matching sensor
//					// EXCEPT it is the radials with the fake houses attached
////					if ( (locationId == '20165_T1U1')) {
////						var valueSum = 0;
////						var length = 0;
////						for (var i=0; i<geoDataCollection.features[key].properties.Houses.length; i++) {
////							valueSum += sensorIdToLoadValue[mapObjectIDToSensorID[geoDataCollection.features[key].properties.Houses[i]]];
////							length++;
////						}
////						colorValue = valueSum/length;
////					} else {
//						// If this is not one of the two special radials, just assign the color from the fake value
////						colorValue = sensorIdToLoadValue[mapObjectIDToSensorID[locationId]];
//						colorValue = sensorIdToLoadValue[mapObjectId_to_sensorIds_json[locationId].sensor];
////					};
//	 			} else if (Number(locationId)) {					// Color Transformer stations
//					// Calculate this color from the color of the radials
//					var valueSum = 0;
//					var length = 0;
//					for (var i=0; i<geoDataCollection.features[key].properties.SingleLines.length; i++) {
////						if (sensorIdToLoadValue[mapObjectIDToSensorID[geoDataCollection.features[key].properties.SingleLines[i]]]) {
//						if (sensorIdToLoadValue[mapObjectId_to_sensorIds_json[geoDataCollection.features[key].properties.SingleLines[i]].sensor]) {
////							valueSum += sensorIdToLoadValue[mapObjectIDToSensorID[geoDataCollection.features[key].properties.SingleLines[i]]];
//							valueSum += sensorIdToLoadValue[mapObjectId_to_sensorIds_json[geoDataCollection.features[key].properties.SingleLines[i]].sensor];
//							length++;
//						};
//					}
//					colorValue = valueSum/length;
//					
//					// Take transformer value directly from the data, divide by num of radials
//					//colorValue = connectSensorValue[mapObjectIDToSensorID[locationId]] / geoDataCollection.features[key].properties.SingleLines.length;
//				}
//				
//				// Color the map object with its color value
//				svg.select('#t'+locationId)  // Be aware of the t-Prefix!
//			        .transition()
//			        .duration(200)
//			        .attr("stroke", function(d){ return color(colorValue); });
//			}

			// Set new heading with the current time
			var timedHeading =  'Load data for ' + moment().format("YYYY-MM-DD, HH:mm:ss");
			$("#pageHeading")[0].innerHTML = (timedHeading);
		}
		
	});
	
	// Get the recent load of the GC households. 100% = 5000W
	$.ajax({
		url : contextPath+'/NodeServlet?action=instantvalues',
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

var furDetailSupplier = {
	// Gets called if any element on the map is clicked (Transformer station, Radial or house)
	// Needs ID of the object and timeframe of the data
	// Spreads the request to the matching function and calculates the proper timeframe
	showDetails : function(mapObjectId) {
		// Distribute request
		if (Number(mapObjectId)) {							// Show details for a transformer station
			furDetailSupplier.showDetailsTransformer(mapObjectId);
		} else if (mapObjectId.indexOf('T1U') > -1) {		// Show details for a radial
			furDetailSupplier.showDetailsRadial(mapObjectId);
		} else if (mapObjectId.indexOf('house_') > -1) {	// Show details for a house
			furDetailSupplier.showDetailsHouse(mapObjectId);
		}
	},

	// Show Details for a transformer station
	showDetailsTransformer: function (mapObjectID) {
		// Get the proper geodata for the varchrome extension 
		var transformerGeoData = getObjectIdGeoData(mapObjectID.toString());
		// Set the map to the matching view
		map.setView([transformerGeoData.geometry.coordinates[1], transformerGeoData.geometry.coordinates[0]], 15);
		
		var foundRegion = '';
		for (loc in locations) {
			for (var i=0; i<locations[loc].length; i++) {
				if (mapObjectID == locations[loc][i]) {
					foundRegion = loc;
					break;
				}
			}
		}

		var singleLines = '<ul>';
		for (var i=0; i<transformerGeoData.properties.SingleLines.length; i++) {
			var lineID = transformerGeoData.properties.SingleLines[i];
			singleLines += '<li> <a onclick="furDetailSupplier.showDetails(\''+lineID+'\', null)">'+lineID+'</a></li>';
		}
		singleLines += '</ul>';

		var htmlString = '<b>Transformer station ID:</b> <a onclick="furDetailSupplier.centerMap(\''+transformerGeoData.geometry.coordinates[1]+'\',\''+transformerGeoData.geometry.coordinates[0]+'\',15)">' + transformerGeoData.properties.Name + '</a><br/><b>Location:</b> ' + transformerGeoData.properties.Description + '<br/><b>Single radials:</b> '+ singleLines + '<br/><b>Region:</b> ' + foundRegion;
		d3.select('#objectDetailsArea').html(htmlString);
	},

	// Show details for a radial
	showDetailsRadial: function (mapObjectID) {
		// Get the proper geodata for the object
		var radialGeoData = getObjectIdGeoData(mapObjectID.toString());

		// Set the map to the matching view
		var middleCoordinateIndex = Math.round(radialGeoData.geometry.coordinates.length/2);

		map.setView([radialGeoData.geometry.coordinates[middleCoordinateIndex][1], radialGeoData.geometry.coordinates[middleCoordinateIndex][0]], 16);
		
		var singleHouses = '';
		if (roleOfUser == "aggregator") {
			if (radialGeoData.properties.Houses) {
				singleHouses = '<br/><b>Single houses:</b> <ul>';
	
				for (var i=0; i<radialGeoData.properties.Houses.length; i++) {
					var houseID = radialGeoData.properties.Houses[i];
					var houseGeoData = getObjectIdGeoData(houseID.toString());
					singleHouses += '<li> <a onclick="furDetailSupplier.showDetails(\''+houseID+'\', null)">'+houseGeoData.properties.Name+'</a></li>';
				}
				singleHouses += '</ul>';
			}
		}
		
		var htmlString = '<b>Radial ID:</b>  <a onclick="furDetailSupplier.centerMap(\''+radialGeoData.geometry.coordinates[middleCoordinateIndex][1]+'\',\''+radialGeoData.geometry.coordinates[middleCoordinateIndex][0]+'\',16)">' + radialGeoData.properties.Name +'</a>'+ singleHouses;
		d3.select('#objectDetailsArea').html(htmlString);
	},

	// Show details for a house
	showDetailsHouse: function (mapObjectID) {
		// Get the proper geodata for the object
		var houseGeoData = getObjectIdGeoData(mapObjectID.toString());

		// Set the map to the matching view
		map.setView([houseGeoData.geometry.coordinates[1], houseGeoData.geometry.coordinates[0]], 17);

		var htmlString = '<b>House ID:</b>  <a onclick="furDetailSupplier.centerMap(\''+houseGeoData.geometry.coordinates[1]+'\',\''+houseGeoData.geometry.coordinates[0]+'\',17)">' + houseGeoData.id + '</a><br/><b>Installation name:</b> ' + houseGeoData.properties.Name + '<br/><b>Location:</b> ' + houseGeoData.properties.Description;
		d3.select('#objectDetailsArea').html(htmlString);
	},

	// Center the map view
	centerMap: function(lat, long, zoomLevel) {
		map.setView([lat, long], zoomLevel);
	},
	
	// Gets the geodata for an object
	// Relies on geoDataCollection, which is fetched when the map is drawn
//	getObjectIdGeoData: function(mapObjectID) {	
//		for (var i=0; i<geoDataCollection.features.length; i++) {
//			if (geoDataCollection.features[i].id === mapObjectID) {
//				return geoDataCollection.features[i];
//			}
//		}
//	}
};

// Builds a table with the transformer stations and adds event listeners to it, in order to color the radial on hovering
function buildTable(geoDataCollection) {
	var count = 2;
	var tableId = 1;
	var html  = "";
	var tableBody;
	
	for (var key in geoDataCollection.features) {
		
		if (geoDataCollection.features[key].properties.Type == "transformer") {
			var id = geoDataCollection.features[key].id;
			
			html += '<tr id="rowid_'+id+'">';
			html += '	<td>' + id + '</td>'; // id
			html += '	<td><input type="checkbox" id="checkboxid_'+id+'" name="'+id+'" value="'+id+'" onclick="highlightRadial('+id+')"><br></td>'; // Checkbox
			html += '</tr>';
			
			// Split into 6 small tables
			if (count%7 != 0) {
				count++;
			} else {
				tableBody = document.getElementById("gridElementSelectionTableId"+tableId);
				tableBody.innerHTML = html;
				tableId++;
				count++;
				html = "";
			}
		}
	}
	
	// Take care of the last elements. The HTML for them is created, but not in the DOM
	count++;
	tableBody = document.getElementById("gridElementSelectionTableId"+tableId);
	tableBody.innerHTML = html;
	
	// Add event listeners
	for (var key in geoDataCollection.features) { 
		var id = geoDataCollection.features[key].id;
		if (Number(id)) {
			var tablerow = document.getElementById("rowid_"+id);
			tablerow.addEventListener("mouseover", hoveredTableEntry);
			tablerow.addEventListener("mouseout", exitedTableEntry);
			var checkbox = document.getElementById("checkboxid_"+id);
			checkbox.addEventListener("click", clickedTableElementCheckbox);
		}
	}
}

// Gets called when a table element gets hovered over
// Highlights the matching element on the map
function hoveredTableEntry(event) {
	var id = this.getAttribute("id").split("_")[1];   // Get recent event context with this!!!
	var d3Transformer = d3.select('#t'+id);
	d3Transformer.transition().duration(200).style("stroke-width", 18);
	var geoDataForId = getObjectIdGeoData(id);
	for (var i=0; i<geoDataForId.properties.SingleLines.length; i++) {
		var d3Radial = d3.select('#t'+geoDataForId.properties.SingleLines[i]);
		d3Radial.transition().duration(200).style("stroke-width", 10);
	}
}

// Reacts when a checkbox is clicked and colors the microgrid
function highlightRadial(id) {
	var checkbox = document.getElementById("checkboxid_"+id);
	var microgrid = d3.select('#t'+id);
	
	var geoDataForId = getObjectIdGeoData(id.toString());
	for (var i=0; i<geoDataForId.properties.SingleLines.length; i++) {
		var d3Radial = d3.select('#t'+geoDataForId.properties.SingleLines[i]);
		if (checkbox.checked) {
			microgrid.transition().duration(200).style("stroke", "blue");
			d3Radial.transition().duration(200).style("stroke", "blue");
		} else {
			microgrid.transition().duration(200).style("stroke", "");
			d3Radial.transition().duration(200).style("stroke", "");
		}
	}
}

// Takes the bold lines back when leaving the table entry with the cursor
function exitedTableEntry(event) {
	var id = this.getAttribute("id").split("_")[1];   // Get recent event context with this!!!
	var d3Transformer = d3.select('#t'+id);
	d3Transformer.transition().duration(200).style("stroke-width", 12);
	
	var geoDataForId = getObjectIdGeoData(id);
	for (var i=0; i<geoDataForId.properties.SingleLines.length; i++) {
		var d3Radial = d3.select('#t'+geoDataForId.properties.SingleLines[i]);
		d3Radial.transition().duration(200).style("stroke-width", 3);
	}
}

// Gets called when an element gets selected
var globalCheckboxArray = [];
function clickedTableElementCheckbox(event) {
	var id = this.getAttribute("id").split("_")[1];
	if (this.checked) {
		if (globalCheckboxArray.indexOf(id) == -1) {
			globalCheckboxArray.push(id);
		}
	} else {
		globalCheckboxArray.splice(globalCheckboxArray.indexOf(id), 1);
	}
	// Save it in the cookie
	setCookie("selectedmicrogrids", globalCheckboxArray);
}