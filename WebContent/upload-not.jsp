<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="ajs.web.Session"
    import="ajs.files.*"
    import="org.apache.commons.fileupload.*"
    import="org.apache.commons.fileupload.servlet.*"
    import="java.io.*"
    %>    
<%

// the requested path
String uri = request.getParameter("resource-id");
String path = (/*Conditions for valid URI*/
    Session.canUpdateResource(request.getSession(), uri))
  ? FileController.pathFromURI(uri)
  : null;
if (/* set the failure conditions*/
    path == null) {
} else if (ServletFileUpload.isMultipartContent(request)) {
  // It's a file
  try {
    // Create a new file upload handler
    ServletFileUpload upload = new ServletFileUpload();

    // Parse the request
    FileItemIterator iter = upload.getItemIterator(request);
    while (iter.hasNext()) {
      FileItemStream item = iter.next();
      InputStream in = item.openStream();
      if (item.isFormField()) {
        System.out.println("DataTransfer#doPost .. wanted file, got form fields.. NO-OP");
      } else {
        String fileName = item.getName();
        String newPath = FS.concatPath(path, fileName);
        if (FileData.save(newPath, in)) {
          // SUCCESS!
          // TODO: add progress indicators
          
        } else {
        }
      }
      in.close();
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

</body>
</html>