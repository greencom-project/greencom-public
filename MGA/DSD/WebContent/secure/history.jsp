<!--
  This example shows the island of Fur, relying on Leaflet and d3
  Full Power and all Trafos
  With tooltips

  Important for starting:
  - Start XAMPP
  - Call document with http://localhost/projects/mapbox_fur3.html
  - NOT with C:\\file\...
 -->

<!DOCTYPE html>
<html lang="en">

<head>
  <%@ include file="headcontent.jsp" %>
</head>

<body>
  <!-- Bootstrap Title bar -->
	<nav class="navbar navbar-inverse navbar-default navbar-fixed-top" role="navigation">
	  <div class="container-fluid">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
	        <span class="sr-only">Toggle navigation</span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	      </button>
	      <a class="navbar-brand" href="<%=request.getContextPath()%>/secure/home.jsp">
	        <% if (request.isUserInRole("operator")) { %>
	         GreenComDSO
	        <% } else if (request.isUserInRole("aggregator")) { %>
	         GreenComAggregator
	        <% } %>
	      </a>
	    </div>
	
	    <!-- Collect the nav links, forms, and other content for toggling -->
	    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
	      <ul class="nav navbar-nav">
	        <li id="navLiHomeId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/home.jsp">Home/Recent</a></li>
	        <li id="navLiDataId" class="myNavbarItem active"><a href="#">Historical Data</a></li>
	        <li id="navLiRecentId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/forecast.jsp">Forecast Data</a></li>
	        <% if (request.isUserInRole("operator")) { %>
             <li id="navLiElemId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/grid.jsp">Grid Elements</a></li>
          <% } %>
	        <% if (request.isUserInRole("aggregator")) { %>
	           <li id="navLiDeployId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/deploy.jsp">Installations</a></li>
	        <% } %>   
	        <% if (request.isUserInRole("operator")) { %>
             <li id="navLiGTMId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/gtmview.jsp">Grid topology</a></li>
          <% } %>
<%-- 	        <li id="navLiCritId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/critical.jsp">Critical loads</a></li> --%>
	        <% if (request.isUserInRole("operator")) { %>
             <li id="navLiAggReqId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/aggcontact.jsp">Load Shifting</a></li>
          <% } else if (request.isUserInRole("aggregator")) { %>
              <li id="navLiDsoReqId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/dsocontact.jsp">Load Shifting</a></li>
          <% } %>
	      </ul>
	      
	      <ul class="nav navbar-nav navbar-right">
<%-- 	        <li id="navLiSetId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/settings.jsp">Settings</a></li> --%>
	        <li><a href="<%=request.getContextPath()%>/logout.jsp">Logout</a></li>
	      </ul>
	    </div><!-- /.navbar-collapse -->
	  </div><!-- /.container-fluid -->
	</nav>
	<!-- Bootstrap Title bar -  END -->
  
  <!-- Bootstrap Container - All content goes in here -->
  <div class="container">
    
    <!-- Historical data section -->
    <div id="dataSectionId">
      <div class="page-header">
        <h2 id="headingHistoryId">Historical data for the selected map item</h2>
      </div>

      <div class="row">  <!-- Hide initially, show first when detailed data is requested -->
        <div class="col-md-3">
          <h4>Explanation</h4>
          <table class="table table-bordered table-condensed">
            <tbody>
              <tr> <td bgcolor="#009966"></td> <td>Optimal load</td> <td>0-80%</td> </tr>
              <tr> <td bgcolor="#ffcd00"></td> <td>Moderate load</td> <td>80-100%</td> </tr>
              <tr> <td bgcolor="#ff9900"></td> <td>High load</td> <td>100-120%</td> </tr>
              <tr> <td bgcolor="#cc0033"></td> <td>Very high load</td> <td>120-140%</td> </tr>
              <tr> <td bgcolor="#990099"></td> <td>Critical load</td> <td>&gt; 140%</td> </tr>
            </tbody>
          </table>
        </div>

        <div class="col-md-6">
          <!-- Put the small map here -->
          <div id="hereComesTheDataMap">
            <div id="historyMap" class="smallMap"></div>
          </div>
        </div>

        <div class="col-md-3">
          <table class="table table-condensed"> <!-- Time span selector -->
            <tbody>
              <tr>
                <td>Span</td>
                <td>
                  <div class="input-group">
                    <input type="text" class="form-control" id="datePickerText" value="Select timespan">
                    <div class="input-group-btn">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                      <ul class="dropdown-menu pull-right">
                        <li><a id="datePickerLinklasthour" onclick="showDetails(null, 'lasthour')" href="#">Last hour</a></li>
                        <li><a id="datePickerLinklast6hours" onclick="showDetails(null, 'last6hours')" href="#">Last 6 hours</a></li>
                        <li><a id="datePickerLinktoday" onclick="showDetails(null, 'today')" href="#">Today</a></li>
                        <li><a id="datePickerLinkyesterday" onclick="showDetails(null, 'yesterday')" href="#">Yesterday</a></li>
                        <li><a id="datePickerLinklast7days" onclick="showDetails(null, 'last7days')" href="#">Last 7 days</a></li>
                        <li><a id="datePickerLinklast30days" onclick="showDetails(null, 'last30days')" href="#">Last 30 days</a></li>
                        <li class="divider"></li>
                        <li><a id="datePickerLinkall" onclick="showDetails(null, 'all')" href="#">All (Takes a while)</a></li>
                      </ul>
                    </div><!-- /btn-group -->
                  </div><!-- /input-group -->
                </td>
              </tr>
              <tr><td colspan=2 style="text-align:center;"> or individually </td></tr>
              <tr><td>From</td> <td><input type="text" id="gcDatepicker" class="form-control"></td></tr>
              <tr><td>To</td> <td><input type="text" id="gcDatepicker2" class="form-control"></td></tr>
              <tr><td></td><td style="text-align:right;"><button class="btn btn-large btn-success" onclick="showDetailsSpan()">Show Range</button></td></tr>
            </tbody>
          </table> <!-- Time span selector - END -->
        </div>

      </div> <!-- End of row-div -->

      <div style="height:30px;"></div> <!-- Some spacing -->

      <div class="row">
        <!-- This is filled with the table where the energy data goes in -->
        <div id="furDetailsDataTableHighcharts" style="width:100%;">Sorry, but you have not yet selected a map element.</div>
        <div style="text-align:right;"><button class="btn btn-large btn-info" onclick="reload();">Reload</button></div>
      </div>
      
<!--       <div id="showCumulatedHouseValuesId" class="row myGChidden"> -->
<!--         This is filled with the table where the weather data goes in -->
<!--         <div id="smartMeterDetailsHighcharts"></div> -->
<!--       </div> -->
      
      <span id="hereGoTheMGAChartsId"></span>
      <span id="hereGoTheAdditionalChartsId"></span>

      <div class="row">
        <!-- This is filled with the table where the weather data goes in -->
        <div id="weatherDetailsDataTableHighcharts"></div>
      </div>
      
    </div>
    <!-- Historical data section - END -->
   
  </div><!-- Bootstrap Container - END -->
  
  <!-- Does the map rendering part -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/historyMap.js"></script>
  <!-- Does the chart visualization part -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/historyCharts.js"></script>
  <!-- All JS functions specific to the home page -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/history.js"></script>

	<!--   JS Playground -->
	<script type="text/javascript">
		// Start some functions in history.js
	  window.onload = function() {
			// Align page with stored cookie data
			getCookieData();  // in history.js
			// Initialize the datepicker elements
		  initDatepicker(); // in history.js
			// Get recent load data and display
			showRecentLoad(); // in history.js
			// ... and update map viz every 10 seconds
		  setInterval(function(){showRecentLoad()}, 30000);
		};
	</script>

</body>
</html>