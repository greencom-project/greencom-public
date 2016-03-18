// -----------------------------------------------------------------
// Use this JS to fill the map and define the interactions on it
// -----------------------------------------------------------------

var mapOptions = {
	// Limit zooming levels on the map
	minZoom: 4,
	maxZoom: 18
};

// Plain Leaflet solution
var map = L.map('historyMap', mapOptions).setView([56.8221,9.015], 13);
//Event listeners on map
map.on("viewreset", reset);      // Fired on zooming, but not on panning
map.on("dragend", dragend);		 // Fired on the end of the dragging, update position of the map

//// Get free map tiles from here
//var osm_tiles = L.tileLayer('http://tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: 'Map data &copy; <href src="www.openstreetmap.org/copyright">OpenStreetMap</href>'}).addTo(map);
//// Alternatives
//// http://wiki.openstreetmap.org/wiki/Mapquest#MapQuest-hosted_map_tiles or http://wiki.openstreetmap.org/wiki/TMS
//// Combine them into a menu
//var baseLayers = {"OpenStreetMap": osm_tiles};
//// And display them on the map
//L.control.layers(baseLayers).addTo(map);

// Or leave the selection away and put the one layer directly to the map, without selecting
L.tileLayer('http://tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: 'Map data &copy; <href src="www.openstreetmap.org/copyright">OpenStreetMap</href>'}).addTo(map);

//Put an overlay over the overlayPane that can be filled with svg elements, controllled by d3
//The size of the overlayPane depends on the zoomLevel
//The svg needs a large-enough padding to render also elements 'outside' the group
var svg = d3.select(map.getPanes().overlayPane).append("svg").attr('id', 'theGroup');
//Append a group to it, give it class leaflet-zoom-hide, to make it hide while zooming, as
//doing custom animations while zooming is too hard, even for professionals
//The group covers all drawn elements and can be repositioned efficiently
var g = svg.append("g").attr("class", "leaflet-zoom-hide");

//Use Leaflet to implement a D3 geometric transformation.
//This is a projection function: Geo coordinates to pixels!
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
var path = d3.geo.path()
	.projection(transform);
var bounds;

// -----------------------------------------------------------------
// ***********      Initialize the visual elements          ********
// -----------------------------------------------------------------

//Custom icon for the house
var houseIcon = L.icon({
	iconUrl: 'resources/pin.png',
	iconSize:     [20, 24], // size of the icon
	iconAnchor:   [10, 24]  // point of the icon which will correspond to marker's location
});

//Tooltip for transformer stations and radials
var tooltip = d3.select("body")
	.append("div")
	.attr("id", "tooltipID")   
	.attr("class", "tooltip");

//Tooltip for houses
var tooltipHouse = d3.select("body")
	.append("div")
	.attr("id", "tooltipHouseID")   
	.attr("class", "tooltip");

// -----------------------------------------------------------------
// *********************** Get the data ****************************
// -----------------------------------------------------------------

//Store the geodata collection here
var geoDataCollection = null;
//The key is: The actual drawing is done by d3, on the basis of geo coordinates. The drawing is NOT done
//(as in the other cases) by plain leaflet!
d3.json('resources/data/furPowerLines.json', function(collection) {	
	// Store geodata collection
	geoDataCollection = collection;
	// And start the visualization
	visualizeCollection(geoDataCollection);
}); // End of json-load-callback


//-----------------------------------------------------------------
//**************************** Visualize it *********************
//-----------------------------------------------------------------
var feature = g.selectAll("path");
function visualizeCollection(collection) {
	// Append a path for each newly seen feature
	feature = feature.data(collection.features, function(d) {return d.id;}); // Provide key function
	
	feature.enter()
	.append("path")
	.attr("id", function(d) {return "t"+d.id;})
	.attr("fill", "none")               // Don"t close the path
	.attr("opacity", "0.8")             // Draw semi-transparent
	.attr("stroke", "blue")             // Draw immediately
	.attr("stroke-linecap", "round")
	.attr("stroke-linejoin", "round")
	.attr("stroke-width", function(d) {
		if (Number(d.id)) {                       // This is a transformer station
			// return d.properties.Weight * 4 + "px"; // No real information, so leave it out
			return '12px';
		} else if (d.id.indexOf('T1U') > -1) {    // This is a radial
			return '3px';               
		} else if (d.id.indexOf('house_') > -1) {  // This is a house
			if (roleOfUser == "aggregator") {
				var toBeDoneOnClick = function(){
					// Catch setting cookie data also when clicking ion the image
					setCookieData(d);
					centerMap(d.id);
				};
				var toBeDoneOnMouseover = function(e){
					tooltipHouse.transition()        
					.duration(200)
					.style("opacity", ".9");
					tooltipHouse.html("Name: " + d.properties.Name + "<br/> Location: " + d.properties.Description) 
					.style("left", (e.originalEvent.pageX) + "px")
					.style("top", (e.originalEvent.pageY - 50) + "px");
				};
				var toBeDoneOnMouseout = function(){
					tooltipHouse.transition()        
					.duration(500)      
					.style("opacity", 0);
				};
				L.marker([d.geometry.coordinates[1], d.geometry.coordinates[0]], {icon: houseIcon})
				.addTo(map)
				.on('click', function(e){ toBeDoneOnClick(); })  // e is a LEAFLET EVENT!!
				.on("mouseover", function(e) { toBeDoneOnMouseover(e); })  // Tooltip on houses
				.on("mouseout", function(e) { toBeDoneOnMouseout(); });
				return '10px';
			} else {return '0px';}
		}
	})
	.on("mouseover", function(d) {   // Tooltips for paths (= stations and radials)
		tooltip.transition()        
		.duration(200)      
		.style("opacity", .9);      
		tooltip.html("Name: " + d.properties.Name + "<br/> Location: " + d.properties.Description)  
		.style("left", (d3.event.pageX) + "px")     
		.style("top", (d3.event.pageY - 50) + "px");
	})                  
	.on("mouseout", function(d) {       
		tooltip.transition()        
		.duration(500)      
		.style("opacity", 0);
	})
	.on("click", function(d) {
		// Store object information to cookie
		setCookieData(d);
		// No matter what was clicked, show details for it
		centerMap(d.id);
	});

	// First start
	reset();
}

// Reposition the SVG to cover ALL the features.
function reset() {
	// Recompute the box to reposition the svg!
	bounds = path.bounds(geoDataCollection);

	var topLeft = bounds[0];
	var bottomRight = bounds[1];

	//Resize svg on zooming, as the map resizes
	svg.attr("width", bottomRight[0] - topLeft[0])
		.attr("height", bottomRight[1] - topLeft[1])
		.style("left", topLeft[0]-40 + "px")  // Subtract padding
		.style("top", topLeft[1]-40 + "px");  // Subtract padding
	// Move the group inside the svg to the according position
	g   .attr("transform", "translate(" + -topLeft[0] + "," + -topLeft[1] + ")");

	// For each feature of the collection, call path
	feature.attr("d", path);

	// Update cookie information
	setCookieData();
}

//Adjusts the map visualization on click
function centerMap(mapObjectId) {	
	// Get geodata
	var data = getObjectIdGeoData(mapObjectId.toString());
	if (Number(mapObjectId)) {							// For a transformer station
		coordinates = data.geometry.coordinates;
	} else if (mapObjectId.indexOf('T1U') > -1) {		// For a radial
		var middleCoordinateIndex = Math.round(data.geometry.coordinates.length/2);
		coordinates = data.geometry.coordinates[middleCoordinateIndex];
	} else if (mapObjectId.indexOf('house_') > -1) {	// For a house
		coordinates = data.geometry.coordinates;
	}	

	// Set the map to the matching coordinate
	map.setView([coordinates[1], coordinates[0]], 15);
}

//Fired at the end of dragging, store map information for further reuse
function dragend() {
	setCookieData();
}

//Update cookie information
function setCookieData(d) {
	if (d) {
		setCookie('mapObjectId', d.id);
		setCookie('mapcenterlat', d.geometry.coordinates[1]);
		setCookie('mapcenterlng', d.geometry.coordinates[0]);
		setCookie('objectTypeName', d.properties.Type);
	} else {
		setCookie("mapcenterlat", map.getCenter().lat);
		setCookie("mapcenterlng", map.getCenter().lng);
		setCookie("mapzoom", map.getZoom());
	}
}
