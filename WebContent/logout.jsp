<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="ajs.web.Session"
    %>
<%
try {
    Session.kill(session);
} catch(IllegalStateException ise) {
	System.out.println("Something in logout");
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Logged out</title>
    <link rel="stylesheet" href="resources/css/global.css" media="all" type="text/css" />
    <link rel="stylesheet" href="resources/css/logout.css" media="all" type="text/css" />
</head>
<body>
    <div id="logout">
        <span id="logout_text">YOU ARE NOT LOGGED IN</span>
        <a href="login.jsp"><span class="button" id="login_button">LOGIN</span></a>
    </div>
</body>
</html>