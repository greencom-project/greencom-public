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
	        <li id="navLiCritId" class="myNavbarItem active"><a href="#">Critical loads</a></li>
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
    
    <!-- Overloads section -->
    <div id="overloadSectionId">
      <div class="page-header" id="overload">
        <h2>Critical loads</h2>
      </div>

      This shows a table of the radial overloads, sorted by time.<br/>
      The overloads can be visualized on the map, showing the whole map to get a feeling of the overall situation. It also shows one hour before and after the critical timespan, to get a feeling of the overall load history.<br/>
      There is also the possibility to download the respective data in csv-format, to import it into other load analyzing tools.
      <br/><br/><br/>
      <table class="table table-condensed">
        <thead>
          <tr>
            <th>Time</th>
            <th>Overload</th>
            <th>Duration</th>
            <th>Location</th>
            <th>Show</th>
            <th>Data</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>March, 6th, 2014 09:35</td>
            <td>150%</td>
            <td>35 mins</td>
            <td>Ulstedvej # Faerkeroddevej, 21386_T1U1</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('21386_T1U1');">Show on map</button></td>
            <td><a href="data/DataFurC.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>March, 6th, 2014 07:45</td>
            <td>150%</td>
            <td>85 mins</td>
            <td>Ulstedvej, 20710_T1U2</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('20710_T1U2');">Show on map</button></td>
            <td><a href="data/DataFurE.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>March, 5th, 2014 18:13</td>
            <td>145%</td>
            <td>210 mins</td>
            <td>Engelstkovvej, 20760_T1U1</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('20760_T1U1');">Show on map</button></td>
            <td><a href="data/DataFurS.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>March, 5th, 2014 08:30</td>
            <td>140%</td>
            <td>60 mins</td>
            <td>Koldkildevej, 21229_T1U2</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('21229_T1U2');">Show on map</button></td>
            <td><a href="data/DataFurW.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>March, 4th, 2014 18:00</td>
            <td>140%</td>
            <td>100 mins</td>
            <td>Dalagervej, 21226_T1U2</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('21226_T1U2');">Show on map</button></td>
            <td><a href="data/DataFurW.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>February, 21st, 2014 12:36</td>
            <td>120%</td>
            <td>80 mins</td>
            <td>Rakildevej, 20686_T1U3</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('20686_T1U3');">Show on map</button></td>
            <td><a href="data/DataFurW.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
          <tr>
            <td>Jan, 7th, 2014 07:10</td>
            <td>130%</td>
            <td>120 mins</td>
            <td>Blegagervej, 20448_T1U2</td>
            <td><button class="btn btn-danger" onclick="showCriticalLoadOnMap('20448_T1U2');">Show on map</button></td>
            <td><a href="data/DataFurW.tsv"><button class="btn btn-confirm">Download</button></a></td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- Overloads section - END -->

  </div><!-- Bootstrap Container - END -->
 
  <script>
  function showCriticalLoadOnMap(objectId) {
	  setCookie('mapObjectId', objectId);
	  // Redirect to home.jsp
    window.location.href = "<%=request.getContextPath()%>/secure/home.jsp";
  }
  </script>
</body>
</html>