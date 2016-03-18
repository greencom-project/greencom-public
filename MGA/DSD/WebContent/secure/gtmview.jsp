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
	        <li id="navLiGTMId" class="myNavbarItem active"><a href="#">Grid topology</a></li>
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
    
   <!-- Grid topology section -->
    <div id="homeSectionId">
      <div class="page-header">
        <h2>Grid topology on the island of Fur, Denmark</h2>
      </div>

      <div class="container">

        <div class="row">
          <div class="col-md-12">
            <div id="hereComesTheOverviewMap">
              <div id="map" class="largeMap"></div>
            </div>
            <h5 id="pageHeading"><i>The island of Fur, Denmark</i></h5> <!-- footer -->
          </div>
        </div>

        <div class="row">
          
          <div class="col-md-4">
            <div class="page-header">
              <h3 style="margin-top:0px;">Object details</h3>
            </div>
            <div id="objectDetailsAreaId">No object selected</div>
          </div>

          <div class="col-md-4">
            <div class="page-header">
              <h3>Show</h3>
            </div>
            <form id="elementFormId" onclick="updateElementsToShow()">
              <input type="checkbox" name="element" value="cables_l" checked> Low power cables<br>
              <input type="checkbox" name="element" value="cables_h"> High power cables<br>
              <input type="checkbox" name="element" value="trafos"> Transformer stations<br>
              <input type="checkbox" name="element" value="nodes"> Nodes<br>
              <input type="checkbox" name="element" value="relays_o"> Open relays<br>
              <input type="checkbox" name="element" value="relays_c"> Closed relays<br>
              <input type="checkbox" name="element" value="icons"> ICONS for all<br>
            </form>
          </div>

          <div class="col-md-4">
            <!-- Explanation of the colors -->
            <div class="page-header">
              <h3>Explanation</h3>
            </div>
            <table class="table table-bordered table-condensed">
              <tbody>
                <tr> <td><img src="<%=request.getContextPath()%>/secure/resources/trafo.png"></img></td> <td>Transformer station</td> </tr>
                <tr> <td><img src="<%=request.getContextPath()%>/secure/resources/node.png"></img></td> <td>Node</td> </tr>
                <tr> <td><img src="<%=request.getContextPath()%>/secure/resources/rel_open.png"></img></td> <td>Relay (Open)</td></tr>
                <tr> <td><img src="<%=request.getContextPath()%>/secure/resources/rel_closed.png"></img></td> <td>Relay (Closed)</td> </tr>
                <tr> <td><img src="<%=request.getContextPath()%>/secure/resources/rel_default.png"></img></td> <td>Relay, status unknown</td> </tr>
              </tbody>
            </table>
          </div>

        </div>    

        <div class="row">
          <div class="page-header">
            <h3>Findings</h3>
          </div>
          <ul>
            <li>Transformer stations are special nodes.</li>
            <li>Closed relays transport power</li>
            <li>Open relays do not transport power</li>
            <li>Open relays define the range of the microgrids</li>
            <li>The position of the open relays is the same as on the printed map</li>
          </ul>
        </div>
      </div>
      
        <!-- This map of the island of Fur shows all transformer stations of E-MIDT having access to the low voltage net on the island (Marked with the circles). The lines depict the radials, each connecting a set of houses to the low voltage network.<br/>
        While the transformer stations all have names consisting of five numbers, starting all with the number 2, the names of the radials connected to a transformer station start with the id of the transformer station, followed by '_T1U' and then a number between 1 and 4, numbering the radial.<br/>
        An example could be the transformer station '20448' with the according radials '20448_T1U1' and '20448_T1U2'. -->
      </div>
    </div>
    <!-- Grid topology section - END -->
   
  </div><!-- Bootstrap Container - END -->

  <!-- All JS functions specific to the home page -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/gtmview.js"></script>
  
  <!--   JS Playground -->
  <script type="text/javascript">
  window.onload = function() {	  
	 moveEnded();
  } 
  </script>

</body>
</html>