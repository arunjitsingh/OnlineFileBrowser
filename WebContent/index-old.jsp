<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="ajs.web.Session"
%>

<%
if (/*Conditions the user is logged in*/
		Session.isInitialized(session)
		|| Session.get(session, Session.ID_KEY) != null) {
	Session.initialize(session);
} else {
	response.sendRedirect("login.jsp");
}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File!It</title>
  <link rel="stylesheet" href="resources/css/global.css" media="all" type="text/css" />
  <link rel="stylesheet" href="resources/css/index.css" media="all" type="text/css" />
  <script src="lib/jquery.js"></script>
  <script src="lib/oo.js"></script>
  <script src="lib/FI-cat.js"></script>
  <script src="resources/scripts/index-old.js"></script>
</head>
<body onload="loadApplication()">
    <a href="logout.jsp">Log out</a>
    <p>
    /OnlineFileBrowser/browser/<input type="text" id="resource" />
    <button id="GET_btn">GET</button>
    <button id="POST_btn">POST</button>
    <button id="DELETE_btn">DELETE</button>
    <button id="DL_btn">Download</button>
    </p>
    <form action="/OnlineFileBrowser/data-transfer/users/arunjitsingh/uploads"
          method="post" accept-charset="utf-8"
        enctype="multipart/form-data">
        <p><input type="file" name="file"><input type="submit" value="Upload"></p>
    </form>
    <div id="dlsection"></div>
 </body>
</html>