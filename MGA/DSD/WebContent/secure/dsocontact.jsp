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
<%--           <li id="navLiCritId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/critical.jsp">Critical loads</a></li> --%>
          <li id="navLiDsoReqId" class="myNavbarItem active"><a href="#">Load Shifting</a></li>
        </ul>
        
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
		<h1>Load shifting request overview</h1>
		
		<div style="height:30px;"></div>
	
		<div class="row">
		  <h2>Pending load shifting requests</h2>
		  <div id="latestDSORequestID">Currently, there are no load shifting requests.</div>
		</div> <!-- end of row -->

    <div style="height:30px;"></div>
    
		<div class="row">
		  <h2>Historical load shifting requests</h2>
<!-- 		  put table of requests here -->
      <div id="oldDSORequestsID">Currently, there are no historical load shifting requests.</div>
		</div>

    <div class="row">
    </div>
    
    <div class="row">
    </div>

	</div>
	<!-- Bootstrap Container - END -->

  <!-- All specific js for the deployment overview -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/dsocontact.js"></script>
  
   <!-- Start the magic -->
  <script type="text/javascript">
  window.onload = function() {
	    moment.locale("de");
	    init();  // in dsocontact.js
	  };
  </script>
  
</body>
</html>