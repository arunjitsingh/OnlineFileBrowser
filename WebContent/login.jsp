<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="ajs.web.User"
    import="ajs.web.Session"
    %>
<%
boolean failed = false, firstTry = true;
	try {
		if (/*Conditions for an existing session*/
		Session.get(session, Session.ID_KEY) != null) {
			Session.initialize(session);
			response.sendRedirect("index.jsp");
		}
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (username != null && password != null) { // user tried to log-in
			firstTry = false;
			Integer id = null;
			if ((id = User.exists(username, password)) != null) {
				Session.initialize(session, id);
				response.sendRedirect("index.jsp");
			} else {
				failed = true;
			}
		}
	} catch (Exception e) {
		  e.printStackTrace();
		  //response.sendRedirect("500.html");
	}
%>

<!DOCTYPE html>
<html>
<head>
    <title>Log in</title>
    <link rel="stylesheet" href="resources/css/global.css" media="all" type="text/css" />
    <link rel="stylesheet" href="resources/css/login.css" media="all" type="text/css" />
    <script src="lib/jquery.js" type="text/javascript" charset="utf-8"></script>
    <script src="resources/scripts/login.js" type="text/javascript" charset="utf-8"></script>
</head>
<body onload="loginLoaded();<%if (failed) {
				out.print("invalidateForm()");
			}%>">
    <div id="login">
        <span id="login_title" class="login_static">log in</span>
        <form id="login_form" action="login.jsp" method="POST" accept-charset="utf-8">
            <div id="inputs">
                <p><input name="username" id="username" type="text" 
                    placeholder="Username" required></p>
                    
                <p><input name="password" id="password" type="password" 
                    placeholder="Password" required></p>
            </div>
        </form>
        <span id="login_footer" class="login_static"><a href="">Request Access</a></span>
        </div>
</body>
</html>