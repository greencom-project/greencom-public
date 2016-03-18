/**
 * All things necessary for the grid elements overview page.
 */

function fillUpTable() {
	// Gets all static data to build up the table
	$.ajax({
		url : contextPath+"/secure/resources/data/furPowerLines.json",
		dataType : "json",
		success : function(data) {
			buildElementTable(data);
		}
	});
}

// Builds the table that is shown when clicking on the grid element overview
function buildElementTable(data) {	
	var tableBody = document.getElementById('gridElementTableBodyId');
	
	var html = '';
	// Build table from static data
	for (var i = 0; i < data.features.length; i++) {
				
		var elemId = data.features[i].id;
		var elemType = '';
		if (Number(elemId)) { // This is a transformer station
			elemType = 'Transformer station';
			html += '<tr>';
			html += '	<td>' + elemId + '</td>'; // id
			html += '	<td>' + elemType + '</td>'; // type
			html += '	<td>' + data.features[i].properties.Description + '</td>'; // location
			html += '	<td id="loadValueInElementTable'+elemId+'">tba</td>'; // load
			html += '	<td><button class="btn btn-danger" onclick="showGridElementOnMap(\'' + elemId + '\');">Show on map</button></td>'; // 
			html += '	<td><button class="btn btn-confirm" onclick="showHistoryForGridElement(\''+elemId+'\');">Show history</button></td>';
			html += '</tr>';
		} else if (elemId.indexOf('T1U') > -1) { // This is a radial
			elemType = 'Radial';
			html += '<tr>';
			html += '	<td>' + elemId + '</td>'; // id
			html += '	<td>' + elemType + '</td>'; // type
			html += '	<td>' + data.features[i].properties.Description + '</td>'; // location
			html += '	<td id="loadValueInElementTable'+elemId+'">tba</td>'; // load
			html += '	<td><button class="btn btn-danger" onclick="showGridElementOnMap(\'' + elemId + '\');">Show on map</button></td>'; // 
			html += '	<td><button class="btn btn-confirm" onclick="showHistoryForGridElement(\''+elemId+'\');">Show history</button></td>';
			html += '</tr>';
		} else if (elemId.indexOf('house_') > -1) { // This is a house
			elemType = 'House';
			// But do not show house data in the DSO view
		}
	}
	tableBody.innerHTML = html;

	// Connect sorting function to table
	$(function () {
		$("#elementTableId").tablesorter({
			theme: 'bootstrap',
			headerTemplate: '{content} {icon}',
			headers: {
				0 : {sorter: "text"},
				1 : {sorter: "text"},
				2 : {sorter: "text"},
				3 : {sorter: "digit"},
				4 : {sorter: false},
				5 : {sorter: false}
			},
			sortList: [[0, 0]], // Initialize column 0 in ascending order
			widgets: ['zebra','columns','uitheme']
		});
	});
	
	updateLoadValues();
	// ... and update load values every 30 seconds
	setInterval(updateLoadValues, 30000);
}

// When the table is build get the load values and insert them into the table
function updateLoadValues() {
	// Gets the latest value for ALL sensors
	var callUrl = contextPath+'/NodeServlet?action=recentloadactua';

	$.ajax({
		url : callUrl,
		dataType : "json",
		success : function(loadData) {
			console.log(loadData);
			// If the table exists
			if (document.getElementById('loadValueInElementTable20082')) {
				// This code takes the percent-values from the ACTUA service
				for (key in loadData) {
					for (var i=0; i < loadData[key].length; i++) {
						var id = loadData[key][i].ID;
						var load =loadData[key][i].LoadValue;
						console.log(id);
						try {
						document.getElementById('loadValueInElementTable' + id).innerHTML = load.toString();
						} catch (e) {}
					}
				}
			}
		}
	});
}

function showGridElementOnMap(objectId) {
	  setCookie('mapObjectId', objectId);
	  setCookieForObjectType(objectId);
	  // Redirect to home.jsp
	  window.location.href = contextPath + "/secure/home.jsp";
}

function showHistoryForGridElement(objectId) {
	  setCookie('mapObjectId', objectId);
	  setCookieForObjectType(objectId);
	  // Redirect to home.jsp
	  window.location.href = contextPath + "/secure/history.jsp";
}

function setCookieForObjectType(mapObjectId) {
	if (Number(mapObjectId)) {							// For a transformer station
		setCookie('objectTypeName', 'transformer station');
	} else if (mapObjectId.indexOf('T1U') > -1) {		// For a radial
		setCookie('objectTypeName', 'radial');
	} else if (mapObjectId.indexOf('house_') > -1) {	// For a house
		setCookie('objectTypeName', 'house');
	}	
}