<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Logout</title>
</head>

<body>
<%
session.invalidate();
response.sendRedirect("secure/home.jsp");  // Client side redirect
// RequestDispatcher rd = request.getRequestDispatcher("secure/startpage.jsp");  // Server side redirect
// rd.forward(request, response); // Server side redirect
%>

You are now logged out!

<!-- <a href="secure/secure1.jsp">Log in again</a> -->
</body>
</html>