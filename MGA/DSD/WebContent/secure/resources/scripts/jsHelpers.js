/**
 * JS helpers which are necessary for all js files
 */

//******************************************************************************
//                             COOKIES
//******************************************************************************
function setCookie(cname, cvalue) {
    var d = new Date();
    var exdays = 7;
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
    }
    return "";
}

function deleteCookie(name) {
	document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
}

function deleteAllCookies() {
    var cookies = document.cookie.split(";");

    for (var i = 0; i < cookies.length; i++) {
    	var cookie = cookies[i];
    	var eqPos = cookie.indexOf("=");
    	var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    	document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
    }
}

//******************************************************************************
//                   COOKIES - END
//******************************************************************************

//Own color function, based on a scale 0...100
var color = function(value) {
	value = parseFloat(value);
	if (value < 50){
		return '#009966';  // Green
	} else if (value < 62.5) {
		return '#ffcd00';  // Yellow
	} else if (value < 75) {
		return '#ff9900';  // Orange
	} else if (value < 87.5) {
		return '#cc0033';  // Red
	} else if (value > 87.5) {
		return '#990099';  // Violet
	} else {
		return '#000066';  // Green, if we have no value
	}
};

//******************************************************************************
//         GET GEODATA - START
//******************************************************************************

// Goes through an array of microgrid IDs and returns the house IDs in these microgrids
function getHouseIdsFromMicrogrids(mgArray) {
	var houseIdsInMGs = [];
	for (key in globalGeoDataCollection.features) {
		if (globalGeoDataCollection.features[key].properties.Type == "house") {
			var microgrid = globalGeoDataCollection.features[key].properties.OnRadial[0].slice(0,5);
			if (mgArray.indexOf(microgrid) != -1) {
				houseIdsInMGs.push(globalGeoDataCollection.features[key].id);
			}
		}
	} 
	return houseIdsInMGs;
}

function getObjectIdGeoData(mapObjectID) {
	for (var i=0; i<globalGeoDataCollection.features.length; i++) {
		if (globalGeoDataCollection.features[i].id === mapObjectID) {
			return globalGeoDataCollection.features[i];
		}
	}
}

//******************************************************************************
//           GET GEODATA - END
//******************************************************************************

//******************************************************************************
//            POLL FOR OPEN DSO REQUESTS - START
//******************************************************************************

var helperPollingForRequests = function() {
	//	Get latest request from the DB. WORKS, commented out for later use
	var loadUrl = contextPath + "/NodeServlet?action=getopenaggrequests";
	$.ajax({
		url : loadUrl,
		datatype: "json",
		success : function(data) {
			var json = JSON.parse(data);
			if (json.length > 0) {
				$("#navLiDsoReqId").find("a").html("Load Shifting ("+json.length+")");
			} else {
				$("#navLiDsoReqId").find("a").html("Load Shifting");
			}
		}
	});
};

// If this is the aggregator interface, poll for open requests 
if (roleOfUser == "aggregator") {
	helperPollingForRequests();
	setInterval(helperPollingForRequests, 10000);
}

//******************************************************************************
//            POLL FOR OPEN DSO REQUESTS - END
//******************************************************************************

//Maps the sensors to the map objects. Attribute sensor marks the primary sensor.
var mapObjectId_to_sensorIds_json = {
//Transformer stations
"20082": {
    "id": "20082",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20082",
            "type": "load"
        }
    }
},
"20145": {
    "id": "20145",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20145",
            "type": "load"
        }
    }
},
"20149": {
    "id": "20149",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20149",
            "type": "load"
        }
    }
},
"20165": {
    "id": "20165",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20165",
            "type": "load"
        }
    }
},
"20290": {
    "id": "20290",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20290",
            "type": "load"
        }
    }
},
"20291": {
    "id": "20291",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20291",
            "type": "load"
        }
    }
},
"20374": {
    "id": "20374",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20374",
            "type": "load"
        }
    }
},
"20398": {
    "id": "20398",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20398",
            "type": "load"
        }
    }
},
"20411": {
    "id": "20411",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20411",
            "type": "load"
        }
    }
},
"20412": {
    "id": "20412",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20412",
            "type": "load"
        }
    }
},
"20448": {
    "id": "20448",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20448",
            "type": "load"
        }
    }
},
"20451": {
    "id": "20451",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20451",
            "type": "load"
        }
    }
},
"20479": {
    "id": "20479",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20479",
            "type": "load"
        }
    }
},
"20480": {
    "id": "20480",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20480",
            "type": "load"
        }
    }
},
"20555": {
    "id": "20555",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20555",
            "type": "load"
        }
    }
},
"20683": {
    "id": "20683",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20683",
            "type": "load"
        }
    }
},
"20686": {
    "id": "20686",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20686",
            "type": "load"
        }
    }
},
"20710": {
    "id": "20710",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20710",
            "type": "load"
        }
    }
},
"20729": {
    "id": "20729",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20729",
            "type": "load"
        }
    }
},
"20741": {
    "id": "20741",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20741",
            "type": "load"
        }
    }
},
"20760": {
    "id": "20760",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20760",
            "type": "load"
        }
    }
},
"20779": {
    "id": "20779",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20779",
            "type": "load"
        }
    }
},
"20789": {
    "id": "20789",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20789",
            "type": "load"
        }
    }
},
"20870": {
    "id": "20870",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20870",
            "type": "load"
        }
    }
},
"20914": {
    "id": "20914",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20914",
            "type": "load"
        }
    }
},
"20943": {
    "id": "20943",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "20943",
            "type": "load"
        }
    }
},
"21222": {
    "id": "21222",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21222",
            "type": "load"
        }
    }
},
"21226": {
    "id": "21226",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21226",
            "type": "load"
        }
    }
},
"21228": {
    "id": "21228",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21228",
            "type": "load"
        }
    }
},
"21229": {
    "id": "21229",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21229",
            "type": "load"
        }
    }
},
"21248": {
    "id": "21248",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21248",
            "type": "load"
        }
    }
},
"21258": {
    "id": "21258",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21258",
            "type": "load"
        }
    }
},
"21319": {
    "id": "21319",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21319",
            "type": "load"
        }
    }
},
"21383": {
    "id": "21383",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21383",
            "type": "load"
        }
    }
},
"21386": {
    "id": "21386",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21386",
            "type": "load"
        }
    }
},
"21393": {
    "id": "21393",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21393",
            "type": "load"
        }
    }
},
"21419": {
    "id": "21419",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21419",
            "type": "load"
        }
    }
},
"21533": {
    "id": "21533",
    "type": "transformer",
    "sensors": {
        "gridload": {
            "sensor": "21533",
            "type": "load"
        }
    }
},
// Radials
"20082_T1U1": {
    "id": "20082_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20082_T1U1",
            "type": "load"
        }
    }
},
"20082_T1U2": {
    "id": "20082_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20082_T1U2",
            "type": "load"
        }
    }
},
"20082_T1U3": {
    "id": "20082_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20082_T1U3",
            "type": "load"
        }
    }
},
"20145_T1U1": {
    "id": "20145_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20145_T1U1",
            "type": "load"
        }
    }
},
"20145_T1U2": {
    "id": "20145_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20145_T1U2",
            "type": "load"
        }
    }
},
"20145_T1U3": {
    "id": "20145_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20145_T1U3",
            "type": "load"
        }
    }
},
"20149_T1U1": {
    "id": "20149_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20149_T1U1",
            "type": "load"
        }
    }
},
"20165_T1U1": {
    "id": "20165_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20165_T1U1",
            "type": "load"
        }
    }
},
"20165_T1U2": {
    "id": "20165_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20165_T1U2",
            "type": "load"
        }
    }
},
"20290_T1U1": {
    "id": "20290_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20290_T1U1",
            "type": "load"
        }
    }
},
"20290_T1U2": {
    "id": "20290_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20290_T1U2",
            "type": "load"
        }
    }
},
"20290_T1U3": {
    "id": "20290_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20290_T1U3",
            "type": "load"
        }
    }
},
"20290_T1U4": {
    "id": "20290_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20290_T1U4",
            "type": "load"
        }
    }
},
"20291_T1U1": {
    "id": "20291_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20291_T1U1",
            "type": "load"
        }
    }
},
"20291_T1U2": {
    "id": "20291_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20291_T1U2",
            "type": "load"
        }
    }
},
"20374_T1U1": {
    "id": "20374_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20374_T1U1",
            "type": "load"
        }
    }
},
"20374_T1U2": {
    "id": "20374_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20374_T1U2",
            "type": "load"
        }
    }
},
"20398_T1U1": {
    "id": "20398_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20398_T1U1",
            "type": "load"
        }
    }
},
"20411_T1U1": {
    "id": "20411_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20411_T1U1",
            "type": "load"
        }
    }
},
"20412_T1U1": {
    "id": "20412_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20412_T1U1",
            "type": "load"
        }
    }
},
"20412_T1U2": {
    "id": "20412_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20412_T1U2",
            "type": "load"
        }
    }
},
"20412_T1U3": {
    "id": "20412_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20412_T1U3",
            "type": "load"
        }
    }
},
"20448_T1U1": {
    "id": "20448_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20448_T1U1",
            "type": "load"
        }
    }
},
"20448_T1U2": {
    "id": "20448_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20448_T1U2",
            "type": "load"
        }
    }
},
"20451_T1U1": {
    "id": "20451_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20451_T1U1",
            "type": "load"
        }
    }
},
"20451_T1U2": {
    "id": "20451_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20451_T1U2",
            "type": "load"
        }
    }
},
"20479_T1U1": {
    "id": "20479_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20479_T1U1",
            "type": "load"
        }
    }
},
"20479_T1U2": {
    "id": "20479_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20479_T1U2",
            "type": "load"
        }
    }
},
"20479_T1U3": {
    "id": "20479_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20479_T1U3",
            "type": "load"
        }
    }
},
"20480_T1U1": {
    "id": "20480_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20480_T1U1",
            "type": "load"
        }
    }
},
"20480_T1U2": {
    "id": "20480_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20480_T1U2",
            "type": "load"
        }
    }
},
"20480_T1U3": {
    "id": "20480_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20480_T1U3",
            "type": "load"
        }
    }
},
"20480_T1U4": {
    "id": "20480_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20480_T1U4",
            "type": "load"
        }
    }
},
"20555_T1U1": {
    "id": "20555_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20555_T1U1",
            "type": "load"
        }
    }
},
"20555_T1U2": {
    "id": "20555_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20555_T1U2",
            "type": "load"
        }
    }
},
"20555_T1U3": {
    "id": "20555_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20555_T1U3",
            "type": "load"
        }
    }
},
"20555_T1U4": {
    "id": "20555_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20555_T1U4",
            "type": "load"
        }
    }
},
"20683_T1U1": {
    "id": "20683_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20683_T1U1",
            "type": "load"
        }
    }
},
"20683_T1U2": {
    "id": "20683_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20683_T1U2",
            "type": "load"
        }
    }
},
"20683_T1U3": {
    "id": "20683_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20683_T1U3",
            "type": "load"
        }
    }
},
"20683_T1U4": {
    "id": "20683_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20683_T1U4",
            "type": "load"
        }
    }
},
"20686_T1U1": {
    "id": "20686_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20686_T1U1",
            "type": "load"
        }
    }
},
"20686_T1U2": {
    "id": "20686_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20686_T1U2",
            "type": "load"
        }
    }
},
"20686_T1U3": {
    "id": "20686_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20686_T1U3",
            "type": "load"
        }
    }
},
"20710_T1U1": {
    "id": "20710_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20710_T1U1",
            "type": "load"
        }
    }
},
"20710_T1U2": {
    "id": "20710_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20710_T1U2",
            "type": "load"
        }
    }
},
"20729_T1U1": {
    "id": "20729_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20729_T1U1",
            "type": "load"
        }
    }
},
"20729_T1U2": {
    "id": "20729_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20729_T1U2",
            "type": "load"
        }
    }
},
"20729_T1U3": {
    "id": "20729_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20729_T1U3",
            "type": "load"
        }
    }
},
"20729_T1U4": {
    "id": "20729_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20729_T1U4",
            "type": "load"
        }
    }
},
"20741_T1U1": {
    "id": "20741_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20741_T1U1",
            "type": "load"
        }
    }
},
"20741_T1U2": {
    "id": "20741_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20741_T1U2",
            "type": "load"
        }
    }
},
"20741_T1U3": {
    "id": "20741_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20741_T1U3",
            "type": "load"
        }
    }
},
"20741_T1U4": {
    "id": "20741_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20741_T1U4",
            "type": "load"
        }
    }
},
"20760_T1U1": {
    "id": "20760_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20760_T1U1",
            "type": "load"
        }
    }
},
"20760_T1U2": {
    "id": "20760_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20760_T1U2",
            "type": "load"
        }
    }
},
"20760_T1U3": {
    "id": "20760_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20760_T1U3",
            "type": "load"
        }
    }
},
"20779_T1U1": {
    "id": "20779_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20779_T1U1",
            "type": "load"
        }
    }
},
"20789_T1U1": {
    "id": "20789_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20789_T1U1",
            "type": "load"
        }
    }
},
"20789_T1U2": {
    "id": "20789_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20789_T1U2",
            "type": "load"
        }
    }
},
"20789_T1U3": {
    "id": "20789_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20789_T1U3",
            "type": "load"
        }
    }
},
"20870_T1U1": {
    "id": "20870_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20870_T1U1",
            "type": "load"
        }
    }
},
"20914_T1U1": {
    "id": "20914_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20914_T1U1",
            "type": "load"
        }
    }
},
"20914_T1U2": {
    "id": "20914_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20914_T1U2",
            "type": "load"
        }
    }
},
"20914_T1U3": {
    "id": "20914_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20914_T1U3",
            "type": "load"
        }
    }
},
"20943_T1U1": {
    "id": "20943_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20943_T1U1",
            "type": "load"
        }
    }
},
"20943_T1U2": {
    "id": "20943_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "20943_T1U2",
            "type": "load"
        }
    }
},
"21222_T1U1": {
    "id": "21222_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21222_T1U1",
            "type": "load"
        }
    }
},
"21222_T1U2": {
    "id": "21222_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21222_T1U2",
            "type": "load"
        }
    }
},
"21226_T1U1": {
    "id": "21226_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21226_T1U1",
            "type": "load"
        }
    }
},
"21226_T1U2": {
    "id": "21226_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21226_T1U2",
            "type": "load"
        }
    }
},
"21228_T1U1": {
    "id": "21228_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21228_T1U1",
            "type": "load"
        }
    }
},
"21228_T1U2": {
    "id": "21228_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21228_T1U2",
            "type": "load"
        }
    }
},
"21229_T1U1": {
    "id": "21229_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21229_T1U1",
            "type": "load"
        }
    }
},
"21229_T1U2": {
    "id": "21229_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21229_T1U2",
            "type": "load"
        }
    }
},
"21248_T1U1": {
    "id": "21248_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21248_T1U1",
            "type": "load"
        }
    }
},
"21248_T1U2": {
    "id": "21248_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21248_T1U2",
            "type": "load"
        }
    }
},
"21248_T1U3": {
    "id": "21248_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21248_T1U3",
            "type": "load"
        }
    }
},
"21258_T1U1": {
    "id": "21258_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21258_T1U1",
            "type": "load"
        }
    }
},
"21319_T1U1": {
    "id": "21319_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21319_T1U1",
            "type": "load"
        }
    }
},
"21319_T1U2": {
    "id": "21319_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21319_T1U2",
            "type": "load"
        }
    }
},
"21319_T1U3": {
    "id": "21319_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21319_T1U3",
            "type": "load"
        }
    }
},
"21383_T1U1": {
    "id": "21383_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21383_T1U1",
            "type": "load"
        }
    }
},
"21383_T1U2": {
    "id": "21383_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21383_T1U2",
            "type": "load"
        }
    }
},
"21383_T1U3": {
    "id": "21383_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21383_T1U3",
            "type": "load"
        }
    }
},
"21383_T1U4": {
    "id": "21383_T1U4",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21383_T1U4",
            "type": "load"
        }
    }
},
"21386_T1U1": {
    "id": "21386_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21386_T1U1",
            "type": "load"
        }
    }
},
"21386_T1U2": {
    "id": "21386_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21386_T1U2",
            "type": "load"
        }
    }
},
"21393_T1U1": {
    "id": "21393_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21393_T1U1",
            "type": "load"
        }
    }
},
"21393_T1U2": {
    "id": "21393_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21393_T1U2",
            "type": "load"
        }
    }
},
"21393_T1U3": {
    "id": "21393_T1U3",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21393_T1U3",
            "type": "load"
        }
    }
},
"21419_T1U1": {
    "id": "21419_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21419_T1U1",
            "type": "load"
        }
    }
},
"21419_T1U2": {
    "id": "21419_T1U2",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21419_T1U2",
            "type": "load"
        }
    }
},
"21533_T1U1": {
    "id": "21533_T1U1",
    "type": "radial",
    "sensors": {
        "gridload": {
            "sensor": "21533_T1U1",
            "type": "load"
        }
    }
},
// Houses
// In sensor IDs starting with 00 the 00 was replaced by 2E to get the instant data row of the sensor. 2F would be the cumulative data row. 
"house_house1" : {
	"id" :"house_house1",
	"type" : "house",
	"predictionsensor" : "20448_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020E48",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B02193C",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B02189C",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			}
//			,
//			"kettle" : {
//				"sensor": "2E15BC001D000DA5",
//				"diagramtype": "load",
//				"name": "Load of the electric kettle in W",
//				"unit": "W"
//			},
//			"stereo" : {
//				"sensor": "2E15BC001D000DD9",
//				"diagramtype": "load",
//				"name": "Load of the stereo equipment in W",
//				"unit": "W"
//			},
//			"pc" : {
//				"sensor": "2E15BC001D000A05",
//				"diagramtype": "load",
//				"name": "Load of a small table lamp in W",
//				"unit": "W"
//			}
		}
	},
"house_house2" : {
	"id" :"house_house2",
	"type" : "house",
	"predictionsensor" : "20145_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020154",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B02185A",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218D0",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house3" : {
	"id" :"house_house3",
	"type" : "house",
	"predictionsensor" : "20149_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020245",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021906",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021530",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house4" : {
	"id" :"house_house4",
	"type" : "house",
	"predictionsensor" : "20165_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020169",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021859",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218F1",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house5" : {
	"id" :"house_house5",
	"type" : "house",
	"predictionsensor" : "20290_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020AF3",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0218A9",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218BC",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house6" : {
	"id" :"house_house6",
	"type" : "house",
	"predictionsensor" : "20291_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B020F71",  // But: Customer has removed these
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0188F",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0211BE",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house7" : {
	"id" :"house_house7",
	"type" : "house",
	"predictionsensor" : "20374_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B02016D",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021315",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218C8",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house8" : {
	"id" :"house_house8",
	"type" : "house",
	"predictionsensor" : "20398_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B02122A",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021898",   // Status unknown
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218F6",   // Status unknown
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house9" : {
	"id" :"house_house9",
	"type" : "house",
	"predictionsensor" : "20412_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218B4",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021902",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B02131D",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house10" : {
	"id" :"house_house10",
	"type" : "house",
	"predictionsensor" : "20451_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218FE",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B02130F",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0213D2",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house11" : {
	"id" :"house_house11",
	"type" : "house",
	"predictionsensor" : "20479_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218B9",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0212C4",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218CA",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house12" : {
	"id" :"house_house12",
	"type" : "house",
	"predictionsensor" : "20480_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B021805",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021349",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0211CB",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house13" : {
	"id" :"house_house13",
	"type" : "house",
	"predictionsensor" : "20683_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B021386",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021334",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B0218AC",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house14" : {
	"id" :"house_house14",
	"type" : "house",
	"predictionsensor" : "20686_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0211CE",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0212BD",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021312",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
// House 15 resigned from the GC project
//"house_house15" : {
//	"id" :"house_house15",
//	"type" : "house",
//	"predictionsensor" : "20686_T1U3",
//	"sensors" : {
//			"smartmeter" : {
//				"sensor": "x",
//				"diagramtype": "load",
//				"name": "Instant load value of the home's SmartMeter",
//				"unit": "W"
//			},
//			"PV" : {
//				"sensor": "2E15BC001B02129D",
//				"diagramtype": "load",
//				"name": "Generation of the solar panel in W",
//				"unit": "W"
//			},
//			"HP" : {
//				"sensor": "2E15BC001B021237",
//				"diagramtype": "load",
//				"name": "Load of the heat pump in W",
//				"unit": "W"
//			},
//		}
//	},
"house_house16" : {
	"id" :"house_house16",
	"type" : "house",
	"predictionsensor" : "20729_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B02184B",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B02129C",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021917",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house17" : {
	"id" :"house_house17",
	"type" : "house",
	"predictionsensor" : "20760_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B021278",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021938",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021369",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house18" : { // The contract participant has died
	"id" :"house_house18",
	"type" : "house",
	"predictionsensor" : "20914_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218BD",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021914",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021925",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house19" : {
	"id" :"house_house19",
	"type" : "house",
	"predictionsensor" : "20779_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218F3",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021404",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021943",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house20" : {
	"id" :"house_house20",
	"type" : "house",
	"predictionsensor" : "20943_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B0218C4",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B021930",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B02190E",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house21" : {  // Defective kit
	"id" :"house_house21",
	"type" : "house",
	"predictionsensor" : "20943_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "x",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0213BD",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "2E15BC001B021906",
				"diagramtype": "load",
				"name": "Load of the heat pump in W",
				"unit": "W"
			},
		}
	},
"house_house22" : {
	"id" :"house_house22",
	"type" : "house",
	"predictionsensor" : "21222_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "2E15BC001B021884",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "2E15BC001B0213BD",
				"diagramtype": "load",
				"name": "Generation of the solar panel in W",
				"unit": "W"
			},
			"HP" : {
				"sensor": "x",
			},
		}
	},
"house_house23" : {  // Status unclear
	"id" :"house_house23",
	"type" : "house",
	"predictionsensor" : "21226_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "x",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "x",
			},
			"HP" : {
				"sensor": "x",
			},
		}
	},
"house_house24" : {  // Status unclear
	"id" :"house_house24",
	"type" : "house",
	"predictionsensor" : "21229_T1U2",
	"sensors" : {
			"smartmeter" : {
				"sensor": "x",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "x",
			},
			"HP" : {
				"sensor": "x",
			},
		}
	},
"house_house25" : {  // Status unclear
	"id" :"house_house25",
	"type" : "house",
	"predictionsensor" : "21258_T1U1",
	"sensors" : {
			"smartmeter" : {
				"sensor": "x",
				"diagramtype": "load",
				"name": "Instant load value of the home's SmartMeter",
				"unit": "W"
			},
			"PV" : {
				"sensor": "x",
			},
			"HP" : {
				"sensor": "x",
			},
		}
	},
// TYNDALL
"house_house99" : {
	"id" :"house_house99",
	"type" : "house",
	"predictionsensor" : "21383_T1U1",
	"sensors" : {
		"smartmeter" : {
			"sensor": "2E15BC001D000D14",
			"diagramtype": "load",
			"name": "Instant load value of the home's SmartMeter",
			"unit": "W"
		},
		"washing" : {
			"sensor": "2E15BC001D000D2D",
			"diagramtype": "load",
			"name": "Load of the washing machine in the ITS room in W",
			"unit": "W"
		},
		"tumbler" : {
			"sensor": "2E15BC001D000F87",
			"diagramtype": "load",
			"name": "Load of the tumble dryer in the ITS room in W",
			"unit": "W"
		},
		"temp_its_room" : {
			"sensor": "32137A0000009971",
			"diagramtype": "weather",
			"name": "Temperature in the ITS room in �C",
			"unit": "C"
		},
	}
	}
}; // END OF JSON

function getMapObjectIdForSensorId(sensorId) {
	for (key in mapObjectId_to_sensorIds_json) {
		if (mapObjectId_to_sensorIds_json[key].sensor == sensorId) {
			return mapObjectId_to_sensorIds_json[key].id;
		}
	}
}