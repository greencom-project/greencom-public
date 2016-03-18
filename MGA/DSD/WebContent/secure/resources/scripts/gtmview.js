/**
 * All JS functions specific to the GTM page
 */

//-----------------------------------------------------------------
//**************************** Initialize map *********************
//-----------------------------------------------------------------

var mapOptions = {
		// Limit zooming levels on the map
		minZoom: 15, // 15
		maxZoom: 18 
};

//Plain Leaflet solution
var map = L.map('map', mapOptions).setView([56.8221,9.015], 13);
//Event listeners on map
map.on("viewreset", reset);   // Fired on loading or zooming
map.on("moveend", moveEnded); // Fired on end of map moving

var toolserver = L.tileLayer('http://tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: 'Map data &copy; <href src="www.openstreetmap.org/copyright">OpenStreetMap</href>'}).addTo(map);
//Get free map tiles from here
//http://wiki.openstreetmap.org/wiki/Mapquest#MapQuest-hosted_map_tiles
//http://wiki.openstreetmap.org/wiki/TMS

//Combine them into a menu
var baseLayers = {"OpenStreetMap":toolserver};
//And display them on the map
L.control.layers(baseLayers).addTo(map);

//Put an overlay over the overlayPane that can be filled with svg elements, controllled by d3
//The size of the overlayPane depends on the zoomLevel
//The svg needs a large-enough padding to render also elements 'outside' the group
var svg = d3.select(map.getPanes().overlayPane).append("svg").attr('id', 'theGroup');
//Append a group to it, give it class leaflet-zoom-hide, to make it hide while zooming, as
//doing custom animations while zooming is too hard, even for professionals
//The group covers all drawn elements and can be repositioned efficiently
var g = svg.append("g").attr("class", "leaflet-zoom-hide");

//Use Leaflet to implement a D3 geometric transformation.
//This is a projection function: Geocoord to pixels!
//gets: Geographical x and y coordinates
//returns: point on the map layer
function projectPoint(x, y) {
	var point = map.latLngToLayerPoint(new L.LatLng(y, x));
	this.stream.point(point.x, point.y);
}
//Baue den Transformator mit Hilfe der selbstgeschriebenen Transformationsfunktion projectPoint
var transform = d3.geo.transform({
	point: projectPoint
	// lineStart: projectPoint2,  // Possibility to listen to other events
	// lineEnd: projectPoint3
});
//path is a function delivering a path, executed for each feature of the geoJSON.
//This path is then transformed according  to the transform function, with a custom geo-to-d3-transformation.
var path = d3.geo.path().projection(transform);
var bounds;

//-----------------------------------------------------------------
//*********** Initialize variables for the visual elements ********
//-----------------------------------------------------------------

//Custom icon for the transformer station
var trafoIcon = L.icon({
	iconUrl: './resources/trafo.png',
	iconSize:     [17, 20], // size of the icon
	iconAnchor:   [8, 20], // point of the icon which will correspond to marker's location
	className: 'trafoIcon'
});
//Custom icon for the node
var nodeIcon = L.icon({
	iconUrl: './resources/node.png',
	iconSize:     [17, 20], // size of the icon
	iconAnchor:   [0, 20], // point of the icon which will correspond to marker's location
	className: 'nodeIcon'
});
//Custom icon for the relay, default
var relDIcon = L.icon({
	iconUrl: './resources/rel_default.png',
	iconSize:     [17, 20], // size of the icon
	iconAnchor:   [20, 20], // point of the icon which will correspond to marker's location
	className: 'relDIcon'
});
//Custom icon for the relay, closed
var relCIcon = L.icon({
	iconUrl: './resources/rel_closed.png',
	iconSize:     [17, 20], // size of the icon
	iconAnchor:   [20, 20], // point of the icon which will correspond to marker's location
	className: 'relCIcon'
});
//Custom icon for the relay, open
var relOIcon = L.icon({
	iconUrl: './resources/rel_open.png',
	iconSize:     [17, 20], // size of the icon
	iconAnchor:   [20, 20], // point of the icon which will correspond to marker's location
	className: 'relOIcon'
});

var colora = d3.scale.category10();

//Tooltip for map elements
var tooltip = d3.select("body")
.append("div")
.attr("id", "tooltipID")   
.attr("class", "tooltip");

//-----------------------------------------------------------------
//*********************** Get the data ****************************
//-----------------------------------------------------------------

//Store the geoDataCollection in here. This object shall not be altered!
var geoDataCollection = null;
//The key is: The actual drawing is done by d3, on the basis of geo coordinates. The drawing is NOT done
//(as in the other cases) by plain leaflet!
//d3.json(contextPath+'/secure/resources/data/large_geoJSON.json', function(collection) {
//	// Store geoDataCollection
//	geoDataCollection = collection;
//	// And start the visualization
////	visualizeCollection(geoDataCollection); // Visualize the whole geoJSON
//}); // End of json-load-callback

//-----------------------------------------------------------------
//**************************** Visualize it *********************
//-----------------------------------------------------------------
var feature;
//Takes the stored geoJSON and visualizes it
function visualizeCollection(collection) {
	// This is a way to directly add the geoJSON to Leaflet, but it is a bit slower than via d3!
	// L.geoJson(collection).addTo(map);

	// Appends a path for each newly seen feature
	feature = g.selectAll("path");
	feature = feature.data(collection.features, function(d) {return d.id;}); // Provide key function
	feature.enter()
	.append("path")
	.attr("id", function(d) {return d.id;})
	.attr("type", function(d) {
		if (d.id.indexOf('nod') > -1) {
			return "node";
		} else if (d.id.indexOf('tra') > -1) {
			return "trafo";
		} else if (d.id.indexOf('rel') > -1) {
			if (d.properties.isClosed == "true") {
				return "relay_c";
			} else {
				return "relay_o";
			}
		} else {
			if (d.properties.voltage == "400") {
				return "cable_low";
			} else {
				return "cable_high";
			}
		}
	})
	.attr("fill", "none")               // Don't close the path
	.attr("opacity", "0.8")             // Draw semi-transparent
	.attr("stroke", function(d, i) {
		if (d.id.indexOf('nod') > -1) {
			return "blue";
		} else if (d.id.indexOf('tra') > -1) {
			return "purple";
		} else if (d.id.indexOf('rel') > -1) {
			if (d.properties.isClosed == "true") {
				return "green";
			} else {
				return "red";
			}
		} else {
			if (d.properties.voltage == "400") {
				return d3.rgb(colora(i)).darker(1); // Multicolor solution
			} else {
				return "black"; // Monochrome solution
			}
		}
	})             // Draw immediately
	.attr("stroke-linecap", "round")
	.attr("stroke-linejoin", "round")
	.attr("stroke-width", function(d) {
		if (d.id.indexOf('nod') > -1) {
			return "2px";
		} else if (d.id.indexOf('tra') > -1) {
			return "4px";
		} else if (d.id.indexOf('rel') > -1) {
			return "1px";
		} else {  // Cable
			if (d.properties.voltage == "400") {
				return "2px";
			} else {
				return "3px";
			}
		}
	})
	.on("mouseover", showTooltip)  // For d3 objects
	.on("mouseout", hideTooltip);

	// Remove the features that should not be displayed
	feature.exit().remove();	// Not necessary (yet)

	// First start
	reset();

	updateElementsToShow();
}

//Gets called when the map needs to redraw its contents (on load and zoom)
function reset() {
	// Computes the bounding box to position the svg!
	bounds = path.bounds(geoDataCollection);
	
	var topLeft = bounds[0];
	var bottomRight = bounds[1];

	// Reposition the SVG to cover ALL the features.
	svg.attr("width", bottomRight[0] - topLeft[0])
	.attr("height", bottomRight[1] - topLeft[1])
	.style("left", topLeft[0]-40 + "px")  // Subtract padding
	.style("top", topLeft[1]-40 + "px");  // Subtract padding
	// Move the group inside the svg to the according position
	g.attr("transform", "translate(" + -topLeft[0] + "," + -topLeft[1] + ")");

	// THIS DRAWS ALL PATHS, according to the projection function defined for the path!!!!
	feature.attr("d", path);
}

//Tooltip show action
var showTooltip = function(d) {
	var featureType = "";
	if (d.id.indexOf("tra") > -1) {
		featureType = "Transformer station";
		coordLat = d.geometry.coordinates[1];
		coordLon = d.geometry.coordinates[0];
	} else if (d.id.indexOf("nod") > -1) {
		featureType = "Node";
		coordLat = d.geometry.coordinates[1];
		coordLon = d.geometry.coordinates[0];
	} else if (d.id.indexOf("rel") > -1) {
		if (d.properties.isClosed == "true") {
			featureType = "Closed relay";
		} else {
			featureType = "Open relay";
		}
		coordLat = d.geometry.coordinates[1];
		coordLon = d.geometry.coordinates[0];
	} else if (d.id.indexOf("cab") > -1) {
		featureType = "Cable";
		coordLat = d.geometry.coordinates[0][1];
		coordLon = d.geometry.coordinates[0][0];
	}

	// Update information HTML here
	document.getElementById("objectDetailsAreaId").innerHTML = '<h4>Object</h4>'+featureType+'<h4>Id</h4>'+d.id.toString().slice(4)+'<h4>Lon</h4>'+coordLon+'<h4>Lat</h4>'+coordLat;

	tooltip.transition()        
	.duration(200)      
	.style("opacity", .9);      
	tooltip.html("Object: " + featureType + "<br/> ID: " + d.id.slice(4))  
	.style("left", (d3.event.pageX) + "px")     
	.style("top", (d3.event.pageY - 50) + "px");
};

//Tooltip hide action
var hideTooltip = function(d) {
	tooltip.transition()        
	.duration(500)      
	.style("opacity", 0);
};

//Fired on moving map borders, so on load, zoom, and move
var ajaxRequest;
function moveEnded(event) {
	var bounds = map.getBounds();
	var northeast = bounds.getNorthEast();
	var southwest = bounds.getSouthWest();

	// Pattern NW = (lon1, lat 1), SE = (lon2, lat 2)
	var query = contextPath + "/NodeServlet?action=bbox&lon1=" +southwest.lng+"&lat1="+northeast.lat+"&lon2="+northeast.lng+"&lat2="+southwest.lat;
//	var query = 'http://greencom.fit.fraunhofer.de:3000/bbox/'+southwest.lng+'/'+northeast.lat+'/'+northeast.lng+'/'+southwest.lat;
	$.ajax({
		url : query,
		dataType : "json",
		success : function(collection) {
			// Make a geoJSON from this data
			var geoJSON = {type: "FeatureCollection"};
			geoJSON.features = collection;
			geoDataCollection = geoJSON;
			// And visualize it
			visualizeCollection(geoJSON);
		}
	});
}

//Gets called when the selected elements in the table change
function updateElementsToShow() {
	elementsToShow = [];
	var form = document.getElementById("elementFormId");
	for (var i=0; i<form.elements.length; i++) {
		if (form.elements[i].checked) {
			elementsToShow.push(form.elements[i].value);
		}
	}
	updateMap(elementsToShow);
}

//Updates the map visualization according to the selected map elements
function updateMap(elementsToShow) {
	// Low-voltage cables
	if (elementsToShow.indexOf("cables_l") > -1) {
		g.selectAll("[type=cable_low]").style("display", "");
	} else {
		g.selectAll("[type=cable_low]").style("display", "none");
	}

	// High voltage cables
	if (elementsToShow.indexOf("cables_h") > -1) {
		g.selectAll("[type=cable_high]").style("display", "");
	} else {
		g.selectAll("[type=cable_high]").style("display", "none");
	}

	// Transformer stations
	if (elementsToShow.indexOf("trafos") > -1) {
		g.selectAll("[type=trafo]").style("display", "");
		if (elementsToShow.indexOf("icons") > -1) {
			g.selectAll("[type=trafo]").attr("fill", function(d) {
				L.marker([d.geometry.coordinates[1], d.geometry.coordinates[0]], {icon: trafoIcon})
				.bindPopup('This is a transformer station. Id: ' + d.id.slice(4))
				.addTo(map);
				return "none"; // Stay with no fill
			});
		}
	} else {
		d3.selectAll(".trafoIcon").remove();
		g.selectAll("[type=trafo]").style("display", "none");
	}

	// Nodes
	if (elementsToShow.indexOf("nodes") > -1) {
		g.selectAll("[type=node]").style("display", "");
		if (elementsToShow.indexOf("icons") > -1) {
			g.selectAll("[type=node]").attr("fill", function(d) {
				L.marker([d.geometry.coordinates[1], d.geometry.coordinates[0]], {icon: nodeIcon})
				.bindPopup('This is a node. Id: ' + d.id.slice(4))
				.addTo(map);
				return "none";  // Stay with no fill
			});
		}
	} else {
		d3.selectAll(".nodeIcon").remove();
		g.selectAll("[type=node]").style("display", "none");
	}

	// Open relays
	if (elementsToShow.indexOf("relays_o") > -1) {
		g.selectAll("[type=relay_o]").style("display", "");
		if (elementsToShow.indexOf("icons") > -1) {
			g.selectAll("[type=relay_o]").attr("fill", function(d) {
				L.marker([d.geometry.coordinates[1], d.geometry.coordinates[0]], {icon: relOIcon})
				.bindPopup('This is an open relay. Id: ' + d.id.slice(4))
				.addTo(map);
				return "none";  // Stay with no fill
			});
		}
	} else {
		d3.selectAll(".relOIcon").remove();
		g.selectAll("[type=relay_o]").style("display", "none");
	}

	// Closed relays
	if (elementsToShow.indexOf("relays_c") > -1) {
		g.selectAll("[type=relay_c]").style("display", "");
		if (elementsToShow.indexOf("icons") > -1) {
			g.selectAll("[type=relay_c]").attr("fill", function(d) {
				L.marker([d.geometry.coordinates[1], d.geometry.coordinates[0]], {icon: relCIcon})
				.bindPopup('This is a closed relay. Id: ' + d.id.slice(4))
				.addTo(map);
				return "none";  // Stay with no fill
			});
		}
	} else {
		d3.selectAll(".relCIcon").remove();
		g.selectAll("[type=relay_c]").style("display", "none");
	}
}