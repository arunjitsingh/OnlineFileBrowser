Here's the rundown on this project:
## INTRODUCTION:
This is an online file browser, representing the exact files on the file system specified on the server.  
(This 'specification' is done using a .properties file. Check WebContent/WEB-INF/default.properties)  
Uses Java + Servlets and JSP.  
Supports session-based authentication  
Check the downloads section for a working WAR file (includes source)

---
## Working the server
The server has 4 servlets:  
> 1. auth _(/OnlineFileBrowser/auth)_
> 2. browser _(/OnlineFileBrowser/browser)_
> 3. data-transfer _(/OnlineFileBrowser/data-transfer)_
> 4. upload _(/OnlineFileBrowser/upload)_

---
### auth
#### GET
Uses a database (MySQL in this case) to load user information based on the username & password provided.  
Stores these in a session

---
### browser
#### GET
GETs the information for the requested path (URI). Eg: **GET** `/.../browser/users/arunjitsingh/uploads`
#### POST
Creates a new directory at the requested URI. Eg: **POST** `/.../browser/users/arunjitsingh/NEW-DIR-NAME`
#### PUT
Rename a file at the URI with the body of the request (JSON string) containing a 'name' key with its value as the new name.
Eg: **PUT** `/.../browser/users/arunjitsingh/OLD-DIR-NAME`; Request body: `{"name":"NEW-DIR-NAME"}`
#### DELETE
DELETEs a file at the URI requested. Eg: **DELETE** `/.../browser/users/arunjitsingh/DEL-THIS-DIR`

---
### data-transfer
#### GET
Download the file at the URI. Directories cannot be downloaded (yet).
Eg: **GET** `/.../data-transfer/users/arunjitsingh/sample.txt`
#### POST
Upload a file to the URI requested. Uses Apache Common's ServletFileUpload

---
### upload
Upload a file to the URI requested. Uses Apache Common's ServletFileUpload.  
Automatically redirects to `upload.html#success` or `upload.html#failure` in the root of the servlet's context.

---
## JSON and JSONP
Google them to know what they are.
#### Using JSON
All requests and responses, by default, use JSON.
#### Using JSONP
Send the JS callback in a parameter to the request. The parameters accepted for JSONP are `callback` _(recommended)_ and `jsonp`.
