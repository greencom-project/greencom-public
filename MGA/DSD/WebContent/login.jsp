<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>GreenCom Login</title>
	
	<link href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	
	<style type="text/css">
		html, body {
	        height: 100%;
	        background: url(<%=request.getContextPath()%>/fur_island.jpg) no-repeat center center fixed;
	        /* The html and body elements cannot have any padding or margin. */
	      }
	      #wrap {
	        min-height: 100%;
	        height: auto !important;
	        height: 100%;
	        /* Negative indent footer by its height */
	        margin: 0 auto -60px;
	        /* Pad bottom by footer height */
	        padding: 0 0 60px;
	      }
	      #wrap > .container {
	        padding: 80px 15px 20px;
	      }
	      #footer {
	        height: 60px;
	        background-color: #f5f5f5;
	      }
	      .container .credit {
	        margin: 18px 0;
	      }
	      #footer > .container {
	        padding-left: 15px;
	        padding-right: 15px;
	      }
	      .whitebox {
         background: white;
         padding: 20px;
         margin-top: 9%;
         border: 2px solid grey;
         border-radius: 10px;
        }
	</style>
</head>

<body>
	<div id="wrap">
	  <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		  <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">GreenCom Decision Support Dashboard</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Login</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
		
	  <div class="container">
			<!-- Put warnings here -->
			<div id="alert_placeholder"> </div>
			
      <!-- Login form -->
      <div class="row">
         <div class="col-md-4 col-md-offset-4 whitebox">
					<form method="POST" action="j_security_check" class="form-signin">
						<div class="form-group">
							<label for="inputNameID">User name</label>
							<input class="form-control" type="text" id="inputNameID" placeholder="User name" name="j_username">
						</div>
						<div class="form-group">
							<label for="inputPassID">Password</label>
							<input class="form-control" type="password" id="inputPassID" placeholder="Password" name="j_password">
						</div>
						<span style="float:right;"><button class="btn btn-default" type="submit">Log in!</button></span>
					</form>
			   </div>
      </div>
 	  </div> <!-- container end -->
 	  
	</div> <!-- wrap end -->
	
	<div id="footer">
    <div class="container">
      <div class="row">
        <div class="col-md-11">
          <p class="credit">
            This project is co-funded by the 
            <a title="European Commission Information Society" href="http://ec.europa.eu/information_society" target="_blank"> European Commission</a>
            under the
            <a title="7th Framework Programme" href="http://cordis.europa.eu/fp7/ict/" target="_blank">7th Framework Programme </a>
          </p>
        </div>
        <div class="col-md-1">
          <img class="pull-right hidden-xs hidden-sm" alt="europe flag small" style="margin-top: 10px;" src="<%=request.getContextPath()%>/resources/europe_flag_small.jpg" height="40" width="60" />
        </div>
      </div>
    </div>
  </div>
    
	<script src="<%=request.getContextPath()%>/resources/jquery/jquery-1.11.1.min.js"></script>
  <script src="<%=request.getContextPath()%>/resources/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>