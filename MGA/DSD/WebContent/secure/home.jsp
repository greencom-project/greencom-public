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
	        <li id="navLiHomeId" class="myNavbarItem active"><a href="#">Home/Recent</a></li>
	        <li id="navLiDataId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/history.jsp">Historical Data</a></li>
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
    
    <!-- Home section -->
    <div id="homeSectionId">
      <div class="page-header">
        <h2>Current load situation on the island of Fur, Denmark</h2>
      </div>

      <div class="container">

				<div class="row">

					<div class="col-md-8">
						<div id="hereComesTheOverviewMap">
							<div id="mapLoad" class="largeMap"></div>
						</div>
						<h5 id="pageHeading">
							<i>The island of Fur, Denmark</i>
						</h5>
						<!-- footer -->
					</div>

					<div class="col-md-4">

						<div class="page-header" style="margin-top: 0px;">
							<h3 style="margin-top: 0px;">Object details</h3>
						</div>
						<div id="objectDetailsArea">No object selected</div>

						<div class="page-header">
							<h3>Current conditions</h3>
						</div>
						<div id="weatherDetailsAreaId"></div>

						<!-- Explanation of the colors -->
						<div class="page-header">
							<h3>Explanation</h3>
						</div>
						<table class="table table-bordered table-condensed">
							<tbody>
								<tr>
									<td bgcolor="#009966"></td>
									<td>Optimal load</td>
									<td>0-80%</td>
								</tr>
								<tr>
									<td bgcolor="#ffcd00"></td>
									<td>Moderate load</td>
									<td>80-100%</td>
								</tr>
								<tr>
									<td bgcolor="#ff9900"></td>
									<td>High load</td>
									<td>100-120%</td>
								</tr>
								<tr>
									<td bgcolor="#cc0033"></td>
									<td>Very high load</td>
									<td>120-140%</td>
								</tr>
								<tr>
									<td bgcolor="#990099"></td>
									<td>Critical load</td>
									<td>&gt; 140%</td>
								</tr>
							</tbody>
						</table>

					</div>
				</div>
				
				<% if (request.isUserInRole("operator")) { %>
					<div class="row">
					  <div class="col-md-2">
	            <table class="table table-bordered table-condensed table-striped">
	              <thead>
	                <tr>
	                  <th>Microgrid</th>
	                  <th>Select</th>
	                </tr>
	              </thead>
	              <tbody id="gridElementSelectionTableId1">
	              </tbody>
	            </table>
	          </div>
	          
	          <div class="col-md-2">
	            <table class="table table-bordered table-condensed table-striped">
	              <tbody id="gridElementSelectionTableId2"></tbody>
	            </table>
	          </div>
	          
	          <div class="col-md-2">
	            <table class="table table-bordered table-condensed table-striped">
	              <tbody id="gridElementSelectionTableId3"></tbody>
	            </table>
	          </div>
	            
	          <div class="col-md-2">
	            <table class="table table-bordered table-condensed table-striped">
	              <tbody id="gridElementSelectionTableId4"></tbody>
	            </table>
	          </div>
	            
	          <div class="col-md-2">
	            <table class="table table-bordered table-condensed table-striped">
	              <tbody id="gridElementSelectionTableId5"></tbody>
	            </table>
	          </div>
            
						<div class="col-md-2">
							<table class="table table-bordered table-condensed table-striped">
								<tbody id="gridElementSelectionTableId6"></tbody>
							</table>
						</div>
					<% } %>

				</div>

				<!-- This map of the island of Fur shows all transformer stations of E-MIDT having access to the low voltage net on the island (Marked with the circles). The lines depict the radials, each connecting a set of houses to the low voltage network.<br/>
        While the transformer stations all have names consisting of five numbers, starting all with the number 2, the names of the radials connected to a transformer station start with the id of the transformer station, followed by '_T1U' and then a number between 1 and 4, numbering the radial.<br/>
        An example could be the transformer station '20448' with the according radials '20448_T1U1' and '20448_T1U2'. -->

			</div>
    </div>
    <!-- Home section - END -->
  
  </div><!-- Bootstrap Container - END -->

  <!-- Does the map rendering part. Contains start script -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/homeMap.js"></script>
  <!-- All JS functions specific to the home page -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/home.js"></script>
  
  <!--   JS Playground -->
  <script type="text/javascript">
  window.onload = function() {	  
	  // Align page with stored cookie data
	  getCookieData();  // in home.js
	  // Get the recent weather conditiona and show them
	  showRecentWeather();  // in home.js
	  // Get recent load data and display
	  showRecentLoad(); // in home.js
	  // ... and update map viz every 10 seconds
	  setInterval(function(){showRecentLoad();}, 30000);
  };
  </script>
  
</body>
</html>