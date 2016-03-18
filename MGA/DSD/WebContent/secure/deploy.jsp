<!--
  This shows an overview over the installations and houses on Fur
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
          <li id="navLiDeployId" class="myNavbarItem active"><a href="#">Installations</a></li>
          <% if (request.isUserInRole("operator")) { %>
             <li id="navLiGTMId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/gtmview.jsp">Grid topology</a></li>
          <% } %>
<%--           <li id="navLiCritId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/critical.jsp">Critical loads</a></li> --%>
          <% if (request.isUserInRole("operator")) { %>
             <li id="navLiAggReqId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/aggcontact.jsp">Load Shifting</a></li>
          <% } else if (request.isUserInRole("aggregator")) { %>
              <li id="navLiDsoReqId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/dsocontact.jsp">Load Shifting</a></li>
          <% } %>        </ul>
        
        <ul class="nav navbar-nav navbar-right">
<%--           <li id="navLiSetId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/settings.jsp">Settings</a></li> --%>
          <li><a href="<%=request.getContextPath()%>/logout.jsp">Logout</a></li>
        </ul>
      </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
  </nav>
  <!-- Bootstrap Title bar -  END -->

  <!-- Bootstrap Container - All content goes in here -->
  <div class="container">
   <h1>GreenCom deployments overview</h1>
   This table shows an overview over all SmartMeter values of the houses with GreenCom deployments on Fur.<br/>
   For a more detailed overview, click the button below to open a page in a new tab informing about the values of all installed sensors in the GreenCom houses and their respective status. Loading this page may take a few seconds.<br/>
   <a href="http://gcomtrack.azurewebsites.net/Installations" target="_blank"><button class="btn btn-success">Sensor installations status</button></a>
   <div style="height:50px;"></div>
   <table class="table table-condensed table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Home</th>
            <th>Location</th>
            <th>Instant value (W)</th>
            <th>Cumulated value (Wh)</th>
            <th>Timestamp</th>
            <th>Show on map</th>
            <th>Show history</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>house_house1</td>
            <td>Home 1</td>
            <td>Konsborgvej 1</td>
            <td id="house_house1_instantid">n/a</td>
            <td id="house_house1_cumulativeid">n/a</td>
            <td id="house_house1_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house1');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house1');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house2</td>
            <td>Home 2</td>
            <td>Hindkjaervej 3</td>
            <td id="house_house2_instantid">n/a</td>
            <td id="house_house2_cumulativeid">n/a</td>
            <td id="house_house2_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house2');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house2');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house3</td>
            <td>Home 3</td>
            <td>Koldkildevej 1</td>
            <td id="house_house3_instantid">n/a</td>
            <td id="house_house3_cumulativeid">n/a</td>
            <td id="house_house3_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house3');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house3');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house4</td>
            <td>Home 4</td>
            <td>Koldkildevej 3</td>
            <td id="house_house4_instantid">n/a</td>
            <td id="house_house4_cumulativeid">n/a</td>
            <td id="house_house4_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house4');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house4');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house5</td>
            <td>Home 5</td>
            <td>Vojelvej 3</td>
            <td id="house_house5_instantid">n/a</td>
            <td id="house_house5_cumulativeid">n/a</td>
            <td id="house_house5_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house5');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house5');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house6</td>
            <td>Home 6</td>
            <td>Koldkildevej 10</td>
            <td id="house_house6_instantid">n/a</td>
            <td id="house_house6_cumulativeid">n/a</td>
            <td id="house_house6_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house6');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house6');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house7</td>
            <td>Home 7</td>
            <td>Anshedevej 1</td>
            <td id="house_house7_instantid">n/a</td>
            <td id="house_house7_cumulativeid">n/a</td>
            <td id="house_house7_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house7');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house7');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house8</td>
            <td>Home 8</td>
            <td>Anders Sorensensvej 2</td>
            <td id="house_house8_instantid">n/a</td>
            <td id="house_house8_cumulativeid">n/a</td>
            <td id="house_house8_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house8');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house8');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house9</td>
            <td>Home 9</td>
            <td>Bjerregards Bakke 22</td>
            <td id="house_house9_instantid">n/a</td>
            <td id="house_house9_cumulativeid">n/a</td>
            <td id="house_house9_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house9');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house9');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house10</td>
            <td>Home 10</td>
            <td>Bjerregards Bakke 37</td>
            <td id="house_house10_instantid">n/a</td>
            <td id="house_house10_cumulativeid">n/a</td>
            <td id="house_house10_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house10');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house10');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house11</td>
            <td>Home 11</td>
            <td>Dalagervej 6</td>
            <td id="house_house11_instantid">n/a</td>
            <td id="house_house11_cumulativeid">n/a</td>
            <td id="house_house11_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house11');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house11');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house12</td>
            <td>Home 12</td>
            <td>Sondergardevej 72</td>
            <td id="house_house12_instantid">n/a</td>
            <td id="house_house12_cumulativeid">n/a</td>
            <td id="house_house12_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house12');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house12');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house13</td>
            <td>Home 13</td>
            <td>Bjerregards Bakke 25</td>
            <td id="house_house13_instantid">n/a</td>
            <td id="house_house13_cumulativeid">n/a</td>
            <td id="house_house13_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house13');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house13');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house14</td>
            <td>Home 14</td>
            <td>Bjerregards Bakke 43</td>
            <td id="house_house14_instantid">n/a</td>
            <td id="house_house14_cumulativeid">n/a</td>
            <td id="house_house14_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house14');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house14');">Show history</button></td>
          </tr>
<!--           <tr> -->
<!--             <td>house_house15</td> -->
<!--             <td>Home 15</td> -->
<!--             <td>Bjerregards Bakke 5B</td> -->
<!--             <td id="house_house15_instantid">n/a</td> -->
<!--             <td id="house_house15_cumulativeid">n/a</td> -->
<!--             <td id="house_house15_timeid">n/a</td> -->
<!--             <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house15');">Show on map</button></td> -->
<!--             <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house15');">Show history</button></td> -->
<!--           </tr> -->
          <tr>
            <td>house_house16</td>
            <td>Home 16</td>
            <td>Bjerregards Bakke 17</td>
            <td id="house_house16_instantid">n/a</td>
            <td id="house_house16_cumulativeid">n/a</td>
            <td id="house_house16_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house16');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house16');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house17</td>
            <td>Home 17</td>
            <td>Bjerregards Bakke 16</td>
            <td id="house_house17_instantid">n/a</td>
            <td id="house_house17_cumulativeid">n/a</td>
            <td id="house_house17_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house17');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house17');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house18</td>
            <td>Home 18</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house18_instantid">n/a</td>
            <td id="house_house18_cumulativeid">n/a</td>
            <td id="house_house18_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house18');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house18');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house19</td>
            <td>Home 19</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house19_instantid">n/a</td>
            <td id="house_house19_cumulativeid">n/a</td>
            <td id="house_house19_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house19');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house19');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house20</td>
            <td>Home 20</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house20_instantid">n/a</td>
            <td id="house_house20_cumulativeid">n/a</td>
            <td id="house_house20_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house20');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house20');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house21</td>
            <td>Home 21</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house21_instantid">n/a</td>
            <td id="house_house21_cumulativeid">n/a</td>
            <td id="house_house21_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house21');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house21');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house22</td>
            <td>Home 22</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house22_instantid">n/a</td>
            <td id="house_house22_cumulativeid">n/a</td>
            <td id="house_house22_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house22');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house22');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house23</td>
            <td>Home 23</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house23_instantid">n/a</td>
            <td id="house_house23_cumulativeid">n/a</td>
            <td id="house_house23_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house23');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house23');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house24</td>
            <td>Home 24</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house24_instantid">n/a</td>
            <td id="house_house24_cumulativeid">n/a</td>
            <td id="house_house24_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house24');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house24');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house25</td>
            <td>Home 25</td>
            <td>Bjerregards Bakke 4</td>
            <td id="house_house25_instantid">n/a</td>
            <td id="house_house25_cumulativeid">n/a</td>
            <td id="house_house25_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house25');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house25');">Show history</button></td>
          </tr>
          <tr>
            <td>house_house99</td>
            <td>TYNDALL facilities</td>
            <td>Cork, Ireland</td>
            <td id="house_house99_instantid">n/a</td>
            <td id="house_house99_cumulativeid">n/a</td>
            <td id="house_house99_timeid">n/a</td>
            <td><button class="btn btn-danger" onclick="showGridElementOnMap('house_house99');">Show on map</button></td>
            <td><button class="btn btn-confirm" onclick="showHistoryForGridElement('house_house99');">Show history</button></td>
          </tr>
        </tbody>
      </table>

  </div><!-- Bootstrap Container - END -->

  <!-- All specific js for the deployment overview -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/deploy.js"></script>
  
   <!-- Start the magic -->
  <script type="text/javascript">
  window.onload = function() {
	    // Init the visualizations
	    fillUpTable();
	    // Get ISMB deployment data
// 	    getISMBOverview();
	  }
  </script>
  
</body>
</html>