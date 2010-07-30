package ajs.web;

import ajs.files.*;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;

/**
 * Servlet implementation class BrowserServlet
 * The main browser servlet for the application
 * Make sure that a url-pattern for this servlet is like {servlet-url}/*
 * The * represents the path for the operation, relative to the root.
 * 		For example, ../browser/users/abcd => (root/)users/abcd
 * This servlet works by using the HTTP 1.1 methods to determine operation.
 * GET		Retrieve information about the path specified
 * POST		Create a new directory at the given path.
 * DELETE	Delete the file/directory at the path specified
 * @author arunjitsingh
 * @version 0.1.1 Includes authentication
 */
public class BrowserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Requester requester = null;
	private Responder responder = null;
	
    private ServletContext context = null;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowserServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context = getServletContext();
		String path = context.getRealPath("WEB-INF/default.properties");
		FS.initialize(path);
		
		if (!FS.initialized()) {
			System.out.println("Could not initialize the file system!");
        	this.destroy();
      }
		
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		this.responder = null;
	}


	/**
	 * Retrieve information about the path specified
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		this.responder = new Responder(response);
		// the requested path
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
						Session.canRetrieveResource(request.getSession(), uri))
					? FileController.pathFromURI(uri)
					: null;
		System.out.println("Requested path: " + path);
		if (path == null) {
			responder.error(403, "Cannot get this resource!");
			this.responder = null;
			return;
		}
		
		// file information
		FileInformation fileInfo = FileController.getFileInformation(path, Filter.defaultFilter());
		if (fileInfo == null) {
			responder.error(FileController.error.code(), FileController.error.error());
		} else {
			responder.json(fileInfo);
		}
		
		this.responder = null;
	}

	
	/**
	 * Create a new directory at the given path.
	 * The end of the path specifies the name of the new directory 
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		this.responder = new Responder(response);
		
		// the requested path
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
				Session.canUpdateResource(request.getSession(), uri))
			? FileController.pathFromURI(uri)
			: null;
		System.out.println("Requested path: " + path);
		if (path == null) {
			responder.error(403, "Cannot create a directory here!");
			this.responder = null;
			return;
		}
		
		if (FileController.createDirectory(path)) {
			responder.json(FileController.getFileInformation(path, Filter.defaultFilter()), 201);
		} else {
			responder.error(409, "Could not create a new directory");
		}
		
		this.responder = null;
	}
	
	
	/**
	 * Rename a file or directory
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		this.responder = new Responder(response);
		HttpSession current = request.getSession();
		// the requested path
		String uri = request.getPathInfo();
		String parent = uri.substring(0, uri.lastIndexOf("/"));
		String path = (/*Conditions for valid URI*/
						Session.canUpdateResource(current, parent))
					? ((/*Conditions for valid URI*/
							Session.canUpdateResource(current, uri))
						? FileController.pathFromURI(uri)
						: null)
					: null;
		System.out.println("Requested path: " + path);
		if (path == null) {
			responder.error(403, "Cannot rename this resource!");
			this.responder = null;
			return;
		}
		
		this.requester = new Requester(request);
		String newName = null;
		try {
			requester.json();
			if (requester.object() != null) {				
				if (requester.object().has("error")) {
					String error = requester.object().get("error").toString();
					System.out.println("error! " + error);
					responder.error(400, error);
					this.requester = null;
					this.responder = null;
					return;
				} else if (requester.object().has("name")) {
					newName = requester.object().get("name").toString();
				}
			}
		} catch (JSONException jsone) {
			responder.error(500, "/browser#Server error.. Could not parse request");
			this.responder = null;
			jsone.printStackTrace();
			return;
		} catch (Exception e) {
			responder.error(500, "/browser#Server error..");
			this.responder = null;
			e.printStackTrace();
			return;
		}
		this.requester = null;
		
		if (/*failure conditions*/
				newName == null 
				|| newName.equalsIgnoreCase("")
				|| !ajs.util.Regex.find(FS.RE_VALID_FILE_NAME, newName)) {
			responder.error(400, "'" + newName + "' is not a vaild filename");
			this.responder = null;
			return;
		}
		
		String to = FS.concatPath(parent, newName);
		
		if (FileController.moveFile(path, to)) {
			responder.json(null, 200);
		} else {
			responder.error(FileController.error.code(), FileController.error.error());
		}
		
		this.responder = null;
	}
	
	
	/**
	 * Delete the file/directory at the path specified
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		this.responder = new Responder(response);
		
		// the requested path
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
				Session.canUpdateResource(request.getSession(), uri))
			? FileController.pathFromURI(uri)
			: null;
		System.out.println("Requested path: " + path);
		if (path == null) {
			responder.error(403, "Cannot delete this resource!");
			this.responder = null;
			return;
		}
		
		response.setStatus(202);// Accepted
		if (FileController.deleteFile(path, false)) {
			responder.json(null, 200);
		} else {
			responder.error(FileController.error.code(), FileController.error.error());
		}
		
		this.responder = null;
	}
}