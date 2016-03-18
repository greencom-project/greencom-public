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
	        <li id="navLiDataId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/history.jsp">Historical Data</a></li>
	        <li id="navLiRecentId" class="myNavbarItem active"><a href="#">Forecast Data</a></li>
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
    
    
    <!-- Forecast data section -->
    <div id="dataSectionId">
      <div class="page-header">
        <h2 id="headingForecastId">Forecasted data for the selected map item</h2>
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
            <div id="forecastMap" class="smallMap"></div>
          </div>
        </div>

        <div class="col-md-3">
        
          <div class="input-group">
            <input type="text" class="form-control" id="datePickerText" value="Select timespan">
            <div class="input-group-btn">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
              <ul class="dropdown-menu pull-right">
<!--            <li><a id="datePickerLinknexthour" onclick="showDetails(null, 'nexthour')" href="#">Next hour</a></li> -->
                <li><a id="datePickerLinknext4hours" onclick="showDetails(null, 'next4hours')" href="#">Next 4 hours</a></li>
<!--            <li><a id="datePickerLinknext6hours" onclick="showDetails(null, 'next6hours')" href="#">Next 6 hours</a></li> -->
<!--            <li><a id="datePickerLinknext12hours" onclick="showDetails(null, 'next12hours')" href="#">Next 12 hours</a></li> -->
<!--            <li><a id="datePickerLinknext24hours" onclick="showDetails(null, 'next24hours')" href="#">Next 24 hours</a></li> -->
<!--            <li><a id="datePickerLinknext48hours" onclick="showDetails(null, 'next48hours')" href="#">Next 48 hours</a></li> -->
              </ul>
            </div><!-- /btn-group -->
          </div><!-- /input-group -->
          <div style="height:30px;"></div> <!-- Some spacing -->
          <div style="text-align:right;"><button class="btn btn-large btn-info" onclick="reload();">Reload</button></div>
        </div>

      </div> <!-- End of row-div -->

      <div style="height:30px;"></div> <!-- Some spacing -->

      <div class="row">
        <div id="forecastChartId" style="width:100%;">Sorry, but you have not yet selected a map element.</div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <h3>Solar energy production forecast from yesterday until tomorrow</h3>
          <div id="solarEnergyForecastTableHighchartsId"></div>
          <div id="solarEnergyChartExplanationId">The estimations are made using a PV cell model E20/435 SOLAR PANEL PV lying on the floor facing south with low amount of dirtiness in the surface in a grassland environment ignoring side waste of energy due the electrical circuits.</div>
        </div>    
      </div>
      
    </div>
    <!-- Forecast data section - END -->
    
  </div><!-- Bootstrap Container - END -->

  <!-- Does the map rendering part -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/forecastMap.js"></script>
  <!-- Does the chart visualization part -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/forecastCharts.js"></script>
  
  <!-- Does the map rendering part. Contains start script. -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/forecast.js"></script>
  
  <!-- Start the magic -->
  <script type="text/javascript">
  window.onload = function() {
    // Align page with stored cookie data
    getCookieData();  // in forecast.js
    // Get recent load data and display
    showRecentLoad(); // in forecast.js
    // ... and update map viz every 10 seconds
    setInterval(function(){showRecentLoad()}, 30000);
    // Show solar energy chart
    initVisualization();
  }
  </script>
  
</body>
</html>