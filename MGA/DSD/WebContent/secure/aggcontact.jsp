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
          <% if (request.isUserInRole("aggregator")) { %>
             <li id="navLiDeployId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/deploy.jsp">Installations</a></li>
          <% } %> 
          <li id="navLiGTMId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/gtmview.jsp">Grid topology</a></li>
<%--           <li id="navLiCritId" class="myNavbarItem"><a href="<%=request.getContextPath()%>/secure/critical.jsp">Critical loads</a></li> --%>
          <li id="navLiAggReqId" class="myNavbarItem active"><a href="#">Load Shifting</a></li>
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
		<h1>Configure a load shifting request</h1>
		Configure here a request for load shifting to the Aggregator.<br/>
		
		<div style="height:30px;"></div>
		
		<h2>Configure your request</h2>
    
		<div class="row">

			<div class="col-md-3">
				<table class="table table-bordered table-condensed table-striped">
					<thead>
					  <tr>
              <th colspan=2>...select the desired microgrids</th>
            </tr>
						<tr>
							<th>Microgrid</th>
							<th>Select</th>
						</tr>
					</thead>
					<tbody id="gridElementSelectionTableId">
					</tbody>
				</table>
			</div>

			<div class="col-md-3">
				<table class="table table-bordered table-condensed table-striped">
					<thead>
            <tr>
              <th colspan=2>...select a timespan</th>
            </tr>
          </thead>
					<tbody>
						<tr>
							<td>From</td>
							<td>
								<div class="input-group date" id="datetimepicker1">
									<input type="text" class="form-control" data-date-format="YYYY/MM/DD HH:mm"/> <span
										class="input-group-addon"> <span
										class="glyphicon-calendar glyphicon"></span>
									</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>To</td>
							<td>
                <div class="input-group date" id="datetimepicker2">
                  <input type="text" class="form-control" data-date-format="YYYY/MM/DD HH:mm"/>
                  <span class="input-group-addon">
                  <span class="glyphicon-calendar glyphicon"></span>
                  </span>
                </div>
              </td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<div class="col-md-3">
			 <table class="table table-bordered table-condensed table-striped">
          <thead>
            <tr>
              <th>...select a load reduction</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style="text-align:right">
                <input type="text" id="loadReductionInputId" placeholder="Type here"> kW
              </td>
            </tr>
          </tbody>
        </table>
			</div>
			
			<div class="col-md-3">
       <table class="table table-bordered table-condensed table-striped">
          <thead>
            <tr>
              <th>...select a reward</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style="text-align:right">
                <input type="text" id="rewardInputId" placeholder="Type here"> DKK
              </td>
            </tr>
          </tbody>
        </table>
        
        <table class="table table-bordered table-condensed table-striped">
          <thead>
            <tr>
              <th>...and a fine.</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style="text-align:right">
                <input type="text" id="fineInputId" placeholder="Type here"> DKK
              </td>
            </tr>
          </tbody>
        </table>
      </div>
			
		</div> <!-- end of row -->
		
		<div class="row">
		  <div class="col-md-12" style="text-align:right;">
		    <button type="submit"class="btn btn-primary" onclick="checkLoadReductionSettings()">Check</button>
		  </div>
		</div>

    <div class="row" id="yourChoiceRowId" style="display:none;">
      <div class="col-md-12">
        <h2>Your choice</h2>
        You selected the radials <b><span id="radialChoiceId"></span></b>
        between <b><span id="startChoiceId"></span></b> and <b><span id="endChoiceId"></span></b>
        with a reduction value of <b><span id="reductionChoiceId"></span></b> kW.<br/>
        You will grant a reward of <b><span id="rewardChoiceId"></span> DKK</b> for fulfilling the request and demand a fine of <b><span id="fineChoiceId"></span> DKK</b> for accepting, but not fulfilling the request.
        <br/><br/><br/>
        If you are OK with this selection, press Submit. If not, adjust your choice and check again.<br/>
        Clicking on "Submit" will send the request to the aggregator. You will be informed when the request has been transferred successfully.
      </div>
      
    </div>
    
    <div class="row" id="submitButtonRowId" style="display:none;">
      <div class="col-md-12" style="text-align:right;">
        <button type="alert" class="btn btn-success" onclick="sendOutRequest();">Submit</button>
      </div>
    </div>
    
    <div class="row">
      <h2>Historical load shifting requests</h2>
<!--      put table of requests here -->
      <div id="oldDSORequestsID">Currently, there are no historical load reduction requests.</div>
    </div>

	</div>
	<!-- Bootstrap Container - END -->

  <!-- All specific js for the deployment overview -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/aggcontact.js"></script>
  
   <!-- Start the magic -->
  <script type="text/javascript">
  window.onload = function() {
	    moment.locale("de");
	    // Init the visualizations
	    init();  // in aggcontact.js
	    // Init datepicker
	    initLoadShiftDatepicker();  // in aggcontact.js
	  };
  </script>
  
</body>
</html>