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
  <script src="lib/jquery.js"></script><script src="lib/jquery.rest.js"></script>
  <script src="lib/oo.js"></script>
  <script src="lib/date.js"></script>
  <script src="lib/FI-cat.js"></script>
  <script src="resources/scripts/index.js"></script>
</head>
<body onload="loadApplication()">
    <div id="toolbar">
        <span class="image-button button" id="logout_btn" title="Log out">
          <img src="resources/images/EXIT.png" alt="Log out">
        </span>
        <span class="image-button button" id="home_btn" title="Home directory">
          <img src="resources/images/HOME.png" alt="Home">
        </span>
        <span class="image-button button" id="groups_btn" title="Shared directories">
          <img src="resources/images/GROUPS.png" alt="Groups">
        </span>
        <span class="image-button button" id="upload_btn" title="Upload to this directory">
          <img src="resources/images/UPLOAD.png" alt="Upload">
        </span>
        <span class="image-button button" id="create_btn" title="Create a new directory">
          <img src="resources/images/DIRNEW.png" alt="New Directory">
        </span>
        <span class="image-button button" id="trash_btn" title="Trash this">
          <img src="resources/images/TRASH.png" alt="Delete">
        </span>
    </div>     
    <div id="main">
      <div id="browser">
    
      </div>
    </div>
    <div id="new-dir" class="popup">
      <span class="button close">X</span>
      <span class="currentSelection"></span>
      <input type="text" id="new-dir-name">
      <div id="new-dir-ok" class="button popup-ok"><img src="resources/images/GO-BIG.png"></div>
    </div>
    <div id="details" class="popup">
      <span class="button close">X</span>
      <span class="file-name"></span>
      <span class="file-size"></span>
      <span class="file-date"></span>
      <div id="file-download" class="button popup-ok"><img src="resources/images/DOWNLOAD.png"></div>
    </div>
    <div id="upload-file" class="popup">
      <span class="button close">X</span>
      <span class="currentSelection"></span>
      <iframe id="upload-frame"></iframe>
    </div>
    <div id="dlsection"></div>
    <div id="overlay"></div>
    <div id="homedata" style="display:none"></div>
 </body>
</html>