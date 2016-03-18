/**
 * All things necessary for the grid elements overview page.
 */

// Called on load of the page and fills up the table with the latest values
function fillUpTable() {
	getInstantValues();
}

// Gets the instant values of the sensors in the home, displays and calls the getter method for the cumulated values
function getInstantValues() {
	// Get the recent Weather conditions and display them
	$.ajax({
		url : contextPath+'/NodeServlet?action=instantvalues',
		dataType : "json",
		success : function(data) {
			for (var key in data) {
				if (!(data[key]=="404")) {
					var data2 = JSON.parse(data[key]);
					document.getElementById(key+"_instantid").innerHTML = data2["LastValue"];
					var date = moment(data2["LastUpdateDate"]).format("YYYY-MM-DD HH:mm");	
					document.getElementById(key+"_timeid").innerHTML = date;
				}
			}
			getCumulatedValues();
		}
	});
}

// Gets the cumulated values and displays them in the table
function getCumulatedValues() {
	// Get the recent Weather conditions and display them
	$.ajax({
		url : contextPath+'/NodeServlet?action=cumulativevalues',
		dataType : "json",
		success : function(data) {
			for (var key in data) {
				if (!(data[key]=="404" )) {
					var data2 = JSON.parse(data[key]);
					document.getElementById(key+"_cumulativeid").innerHTML = data2.LastValue;
				}
			}
		}
	});
}

// Ensures that the buttons do what they are supposed to
function showGridElementOnMap(objectId) {
	  setCookie('mapObjectId', objectId);
	  setCookie('objectTypeName', 'house');
	  // Redirect to home.jsp
	  window.location.href = contextPath + "/secure/home.jsp";
}

//Ensures that the buttons do what they are supposed to
function showHistoryForGridElement(objectId) {
	  setCookie('mapObjectId', objectId);
	  setCookie('objectTypeName', 'house');
	  // Redirect to home.jsp
	  window.location.href = contextPath + "/secure/history.jsp";
}

//function getISMBOverview() {
//	// Get the recent Weather conditions and display them
//	$.ajax({
//		url : "http://gcomtrack.azurewebsites.net/Installations",
//		success : function(data) {
//			console.log(data);
//		}
//	});
//}