<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="apple-mobile-web-app-capable" content="yes">

<title>
  <% if (request.isUserInRole("operator")) { %>
    GC DSO Dashboard
  <% } else if (request.isUserInRole("aggregator")) { %>
    GC Aggregator Dashboard
  <% } %>
</title>

<!--  Calculate role -->
<% String roleOfUser = ""; %>
<% if (request.isUserInRole("operator")) { %>
<% roleOfUser = "operator"; %>
<% } else if (request.isUserInRole("aggregator")) { %>
<% roleOfUser = "aggregator"; %>
<% } %>

<!-- Store the contextPath as a Java variable, which can be retrieved by JSP -->
<% String contextPath = request.getContextPath().toString(); %>

<!-- Make the contextPath variable available in JavaScript BEFORE the other JS is loaded -->
<script>var contextPath = "<%= contextPath %>";</script>
<!-- Make the users role available in JS. Detect the role of the user and store it in here -->
<script>var roleOfUser = "<%= roleOfUser %>";</script>
  
<!-- IMPORT JS -->
  <!-- Import Leaflet and d3 -->
  <script src="<%=request.getContextPath()%>/resources/leaflet/leaflet.js"></script>
  <script src="<%=request.getContextPath()%>/resources/d3/d3.v3.min.js"></script>
  <!-- JS to get bootstrap running -->
  <script src="<%=request.getContextPath()%>/resources/jquery/jquery-1.11.1.min.js"></script>
  <script src="<%=request.getContextPath()%>/resources/bootstrap/js/bootstrap.min.js"></script>
  <!-- Moment.js for time -->
  <script src="<%=request.getContextPath()%>/resources/moment/moment-with-locales.min.js"></script>
  <!-- jQuery UI for datepicker -->
  <script src="<%=request.getContextPath()%>/resources/jqueryui/js/jquery-ui-1.10.4.custom.min.js"></script>
  <!-- Bootstrap DateTimePicker -->
  <script src="<%=request.getContextPath()%>/resources/bootdatetime/bootstrap-datetimepicker.min.js"></script> <!-- Needs moment-with-locales.min.js!!! -->
  <!-- Highcharts.js for graphs -->
  <script src="<%=request.getContextPath()%>/resources/highcharts/highcharts.js"></script>
  <!-- Sorting tables -->
  <script src="<%=request.getContextPath()%>/resources/tablesorter/js/jquery.tablesorter.min.js"></script>
  <script src="<%=request.getContextPath()%>/resources/tablesorter/js/jquery.tablesorter.widgets.min.js"></script>
  <!-- General JS Helper functions, such as cookies or AJAX connections -->
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/furPowerLines.js"></script>
  <script src="<%=request.getContextPath()%>/secure/resources/scripts/jsHelpers.js"></script>
<!-- IMPORT JS - END -->

<!-- IMPORT CSS -->
  <!-- Bootstrap -->
  <% if (request.isUserInRole("operator")) { %>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap_cerulean.min.css"/>
  <% } else if (request.isUserInRole("aggregator")) { %>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap_united.min.css"/>
  <% } %>
  <%--    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/bootstrap/css/bootstrap.min.css"/> --%>
  <!-- jQuery UI for datepicker -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/jqueryui/css/ui-lightness/jquery-ui-1.10.4.custom.min.css"/>
  <!-- Bootstrap DateTimePicker -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/bootdatetime/bootstrap-datetimepicker.min.css"/>
  <!-- Leaflet for maps -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/leaflet/leaflet.css">
  <!-- Tablesorter styles -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/tablesorter/css/theme.bootstrap.css">
  <!-- Own styles -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/secure/resources/styles/gcstyles.css">
<!-- IMPORT CSS - END -->