function init() {
	// Start polling for open requests
	pollingForRequests();
	setInterval(pollingForRequests, 10000);
}

// Polls for recent DSO requests. Displays all unanswered requests
var pollingForRequests = function() {
	//	Get latest request from the DB. WORKS, commented out for later use
	var loadUrl = contextPath + "/NodeServlet?action=getopenaggrequests";
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(data) {
			
			var formatString = "YYYY-MM-DD HH:mm:ss";
			var json = JSON.parse(data);
			var html = "";
						
			html += "<table class='table table-condensed table-striped tablesorter-bootstrap'>";
			html += "<thead>";
			html += "<tr>";
//			html +=   " <th>#</th>";
			html +=   " <th>Date</th>";
			html +=   " <th>Contracted houses</th>";
			html +=   " <th>Time Start</th>";
			html +=   " <th>Time End</th>";
			html +=   " <th>Reduction in kW</th>";
			html +=   " <th>Reward in DKK</th>";
			html +=   " <th>Fine in DKK</th>";
			html +=   " <th>MGR possible?</th>";
			html +=   " <th>Possible amount</th>";
			html +=   " <th>Accept</th>";
			html +=   " <th>Reject</th>";
			html += "</tr>";
			html += "</thead>";
			html += "<tbody>";
			
			for (var i=0; i<json.length; i++) {
				var jsonObject = json[i];
				var requestTime = moment(jsonObject.date).format(formatString);
				var startTime = moment(jsonObject.request.timeStart).format(formatString);
				var endTime = moment(jsonObject.request.timeEnd).format(formatString);
				var houseIdString = jsonObject.request.houseIDs.toString();
				var num = i + 1;
								
				html += "<tr>";
//				html += "<td>" + num + "</td>";
				html += "<td>" + requestTime + "</td>";
				html += "<td>" + houseIdString.replace(/,/g,", ") + "</td>";
				html += "<td>" + startTime + "</td>";
				html += "<td>" + endTime + "</td>";
				html += "<td>" + jsonObject.request.reduction + "</td>";
				html += "<td>" + jsonObject.request.reward + "</td>";
				html += "<td>" + jsonObject.request.fine + "</td>";
				html += "<td>" + jsonObject.mgrpossible + "</td>";
				html += "<td>" + jsonObject.mgramount + "</td>";
				html += "<td><button class='btn btn-success' onclick='dsoRequestResponse(\"accepted\", \""+jsonObject.date+"\")'>Accept</button></td>";
				html += "<td><button class='btn btn-danger' onclick='dsoRequestResponse(\"rejected\", \""+jsonObject.date+"\")'>Reject</button></td></tr>";
				html += "<tr>";
				html += "<td colspan='11'>" + jsonObject.mgrcalculations + "</tr>";
				html += "</tr>";
			}
			
			html += "</tbody>";
			document.getElementById("latestDSORequestID").innerHTML=html;
		}
	});
	
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
			html +=   " <th>Contracted houses</th>";
			html +=   " <th>Time Start</th>";
			html +=   " <th>Time End</th>";
			html +=   " <th>Reduction in kW</th>";
			html +=   " <th>Reward in DKK</th>";
			html +=   " <th>Fine in DKK</th>";
			html +=   " <th>MGR possible?</th>";
			html +=   " <th>Possible amount</th>";
			html +=   " <th>Reaction</th>";
			html += "</tr>";
			html += "</thead>";
			html += "<tbody>";
			
			for (var i=0; i<json.length; i++) {
				var jsonObject = json[i];
				var requestTime = moment(jsonObject.date).format(formatString);
				var startTime = moment(jsonObject.request.timeStart).format(formatString);
				var endTime = moment(jsonObject.request.timeEnd).format(formatString);
				var houseIdString = jsonObject.request.houseIDs.toString();
				var possibleAmount = "";
				if (jsonObject.mgramount != null) {
					possibleAmount = jsonObject.mgramount;
				}
				var reaction = "";
				var num = json.length - i;
				
				if (jsonObject.status == "accepted") {
					reaction = '<span class="label label-success">Accepted</span>';
				} else { // rejected
					reaction = '<span class="label label-danger">Rejected</span>';
				}
				
				html += "<tr><td>" + num + "</td>";
				html += "<td>" + requestTime + "</td>";
				html += "<td>" + houseIdString.replace(/,/g,", ") + "</td>";
				html += "<td>" + startTime + "</td>";
				html += "<td>" + endTime + "</td>";
				html += "<td>" + jsonObject.request.reduction + "</td>";
				html += "<td>" + jsonObject.request.reward + "</td>";
				html += "<td>" + jsonObject.request.fine + "</td>";
				html += "<td>" + jsonObject.mgrpossible + "</td>";
				html += "<td>" + possibleAmount + "</td>";
				html += "<td>" + reaction + "</tr>";
				html += "<tr>";
				html += "<td colspan='10'>" + jsonObject.mgrcalculations + "</tr>";
				html += "</tr>";	
			}
			
			html += "</tbody>";
			document.getElementById("oldDSORequestsID").innerHTML=html;
		}
	});
};

// Handles the Aggregators' reaction on the DSO request. Sets a read flag on the request, such that it appears in the historical view
function dsoRequestResponse(decisionPositive, time) {
	var loadUrl = "";
	if (decisionPositive == "accepted") {
		loadUrl = contextPath + "/NodeServlet?action=confirmdsorequest&time=" + time + "&decision=accepted";
	} else {
		loadUrl = contextPath + "/NodeServlet?action=confirmdsorequest&time=" + time + "&decision=rejected";
	}
	
	// Send out the confirmation
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(data) {
			var json = JSON.parse(data);
			if (json.updatedExisting == true) {
				// Update interface
				pollingForRequests();
				helperPollingForRequests();
				alert("Your decision has been saved!");
			}
		}
	});
}
