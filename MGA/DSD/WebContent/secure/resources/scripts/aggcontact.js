/**
 * All things necessary for the grid elements overview page.
 */

// Called on load of the page and fills up the table with the list 
function init() {
	// Start polling first
	pollingForRequests();
	setInterval(pollingForRequests, 10000);
	// Check for selected transformers
	showListOfSelectedTransformers();
}


//Polls for recent DSO requests. Displays all unanswered requests
var pollingForRequests = function() {
	// Get historical load shifting requests
	var loadUrl2 = contextPath + "/NodeServlet?action=getoldaggrequests";
	$.ajax({
		url : loadUrl2,
		datatype: "json",
		success : function(data) {
			var formatString = "YYYY-MM-DD HH:mm:ss";
			var json = JSON.parse(data);
			var html = "";
			
			html += "<table class='table table-condensed table-striped tablesorter-bootstrap'>";
			html += "<thead>";
			html += "<tr>";
			html +=   " <th>#</th>";
			html +=   " <th>Date</th>";
			html +=   " <th>Microgrids</th>";
			html +=   " <th>Time Start</th>";
			html +=   " <th>Time End</th>";
			html +=   " <th>Reduction in kW</th>";
			html +=   " <th>Reward in DKK</th>";
			html +=   " <th>Fine in DKK</th>";
			html +=   " <th>Reaction</th>";
			html += "</tr>";
			html += "</thead>";
			html += "<tbody>";
			
			
			for (var i=0; i<json.length; i++) {
				var jsonObject = json[i];
				var requestTime = moment(jsonObject.date).format(formatString);
				var startTime = moment(jsonObject.request.timeStart).format(formatString);
				var endTime = moment(jsonObject.request.timeEnd).format(formatString);
				var reaction = "";
				var num = json.length - i;
				
				if (jsonObject.status == "accepted") {
					reaction = '<span class="label label-success">Accepted</span>';
				} else { // rejected
					reaction = '<span class="label label-danger">Rejected</span>';
				}
				
				html += "<tr>"
				html += "<td>" + num + "</td>";
				html += "<td>" + requestTime + "</td>";
				html += "<td>" + jsonObject.request.microgrids + "</td>";
				html += "<td>" + startTime + "</td>";
				html += "<td>" + endTime + "</td>";
				html += "<td>" + jsonObject.request.reduction + "</td>";
				html += "<td>" + jsonObject.request.reward + "</td>";
				html += "<td>" + jsonObject.request.fine + "</td>";
				html += "<td>" + reaction + "</tr>";
				
			}
			
			html += "</tbody>";
			document.getElementById("oldDSORequestsID").innerHTML=html;
		}
	});
};

//Initializes the datepicker object
function initLoadShiftDatepicker() {	
	// Examples from: http://eonasdan.github.io/bootstrap-datetimepicker/
	$('#datetimepicker1').datetimepicker({
		language: 'en',
		showToday: true
	}).data("DateTimePicker").setMinDate(new Date());
	$('#datetimepicker2').datetimepicker({
		language: 'en',
		showToday: true
	}).data("DateTimePicker");
	
	// Allow only meaningful timespans
	$("#datetimepicker1").on("dp.change",function (e) {
        $('#datetimepicker2').data("DateTimePicker").setMinDate(e.date);
     });
	$("#datetimepicker2").on("dp.change",function (e) {
        $('#datetimepicker1').data("DateTimePicker").setMaxDate(e.date);
     });
}

// Gets the instant values of the sensors in the home, displays and calls the getter method for the cumulated values
function showListOfSelectedTransformers() {
	// Get the recent Weather conditions and display them
	var transf = getCookie("selectedmicrogrids");
	var transformerArray = transf.split(",");
	
	var html = "";
	for (var i=0; i<transformerArray.length; i++) {
		var id = transformerArray[i];
		if (!(id == "")) {
			html += '<tr id="rowid_'+id+'">';
			html += '	<td>' + id + '</td>'; // id
			html += '	<td><input type="checkbox" id="checkboxid_'+id+'" name="'+id+'" value="'+id+'" checked/></td>'; // Checkbox
			html += '</tr>';
		}
	}
	tableBody = document.getElementById("gridElementSelectionTableId");
	tableBody.innerHTML = html;
}

// Takes all settings and combines them into HTMl and JSON
var reductionJSON = {};
function checkLoadReductionSettings() {
	var checkboxTable = document.getElementById("gridElementSelectionTableId");
	var checkboxRows = checkboxTable.getElementsByTagName("tr");
	var checkboxArray = [];
	for (var i=0; i<checkboxRows.length; i++) {
		var checkbox = checkboxRows[i].getElementsByTagName("input")[0];
		if (checkbox.checked) {
			checkboxArray.push(checkbox.value.toString());
		}
	}
	
	var timeStart = moment($('#datetimepicker1').data("DateTimePicker").getDate());
	var timeEnd = moment($('#datetimepicker2').data("DateTimePicker").getDate());
	var reduction = document.getElementById("loadReductionInputId").value;
	var reward = document.getElementById("rewardInputId").value;
	var fine = document.getElementById("fineInputId").value;

	if (checkboxArray.length == 0) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("You did not select a microgrid");
	} else if (timeStart == null) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("The start time is missing");
	} else if (timeEnd == null) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("The end time is missing");
	} else if (!Number(reduction) && (reduction != "0")) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("The reduction value is missing!");
	} else if (!Number(reward) && (reward != "0")) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("The amount of the reward is missing!");
	} else if (!Number(fine) && (fine != "0")) {
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:none;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:none;");
		alert("The amount of the fine is missing!");
	} else {
		// Write HTML
		document.getElementById("radialChoiceId").innerHTML = checkboxArray;
		document.getElementById("startChoiceId").innerHTML = timeStart.format("YYYY/MM/DD HH:mm");
		document.getElementById("endChoiceId").innerHTML = timeEnd.format("YYYY/MM/DD HH:mm");
		document.getElementById("reductionChoiceId").innerHTML = reduction;
		document.getElementById("rewardChoiceId").innerHTML = reward;
		document.getElementById("fineChoiceId").innerHTML = fine;
		// Switch it visible
		document.getElementById("yourChoiceRowId").setAttribute("style", "display:block;");
		document.getElementById("submitButtonRowId").setAttribute("style", "display:block;");
		
		var houseIds = getHouseIdsFromMicrogrids(checkboxArray);
		// And prepare a JSON object that can be sent to the aggregator
		reductionJSON = {
			"microgrids":checkboxArray,
			"houseIDs":  houseIds,
			"timeStart": timeStart.toISOString(),  // ONLY ISO-Strings to the DB
			"timeEnd":   timeEnd.toISOString(),    // ONLY ISO-Strings to the DB
			"reduction": reduction,
			"reward":    reward,
			"fine":      fine
		};
	}
}

// Sends the reductionJson to the Database
function sendOutRequest() {
	var time = new Date();
	var sendOutJson = {
			"action" : "addaggrequest",
			"time" : time.toISOString(),
			"reductionData" : JSON.stringify(reductionJSON)
	};
	// Send to DB-storage-script
	var loadUrl = contextPath + "/NodeServlet";
	$.ajax({
		type: "POST",
		url : loadUrl,
		data: sendOutJson,
		datatype: "json",
		success : function(data) {
			var json = JSON.parse(data);
			if (json.action == "insertedIntoDatabase") {
				alert("Your request has been sent to the GreenCom aggregator successfully!");
			}
		}
	});
}