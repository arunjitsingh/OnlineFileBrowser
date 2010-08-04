package ajs.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

import ajs.files.FS;
import ajs.files.FileController;
import ajs.files.FileData;
import ajs.files.Filter;

/**
 * Servlet implementation class DataTransferServlet
 * Manages file upload (POST) and download (GET)
 * @author arunjitsingh
 * @version 0.1.1 Includes authentication
 * @version 0.2.0 : JSONP
 */
public class DataTransferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ServletContext context = null;
	protected Responder responder = null;   
	protected Requester requester = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataTransferServlet() {
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
	 * GET the resource. This will download the requested file (or rather, upload from here to the client)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		/*
		 * !!!!! IMPORTANT
		 *     initiate EITHER the responder or the download
		 *     use conditions to branch..
		 */
		String jsonp = Requester.callbackForJSONP(request);
		System.out.println(jsonp);
		// the requested path
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
				Session.canRetrieveResource(request.getSession(), uri))
			? FileController.pathFromURI(uri)
			: null;
		File file = (path != null) ? new File(path) : null;
		if (/* set the failure conditions*/
				path == null
				|| !file.exists()
				|| file.isDirectory()) {
			this.responder = new Responder(response);
			if (file.isDirectory()) {
				responder.jsonp(jsonp).error(405, "Directory compression not available");
			} else if(!file.exists()) {
				responder.jsonp(jsonp).error(404, "Resource not found");
			} else {
				responder.jsonp(jsonp).error(403, "Cannot modify resource");
			}
			this.responder.close();
			this.responder = null;
		} else {
			/*
			 * The download process...
			 * Note: given the conditional break above (with if..else), the response does not need to be reset
			 * {put this in a separate thread!}
			 * RESULT: Tried and failed! Use the servlet's thread pool instead.. 
			 * don't have to do anything for that! 
			 */

			System.out.println(":: Streaming {" + file.getAbsolutePath() 
					+ "} to {" + request.getLocalAddr() + "}");

			// prepare file information
			long size = file.length();
			String mime = context.getMimeType(path);
			String action = request.getParameter("do");
			System.out.println(":: size:" + size + "; mime:" + mime);
			
			// prepare response information
			response.setContentType( (mime != null) ? mime : "application/octet-stream" );
			response.setContentLength((int)size);
			if (action != null && action.equalsIgnoreCase("download")) {
				response.setHeader("Content-Disposition", "attachment; filename=\""+file.getName()+"\"");
			} else if (action != null && action.equalsIgnoreCase("stream")) {
				response.setHeader("Content-Disposition", "inline; filename=\""+file.getName()+"\"");
			}
			
			
			// prepare output stream
			ServletOutputStream out = response.getOutputStream();

			if (FileData.load(path, out, true)) {
				System.out.println(":: Streaming {" + file.getName() + "} done!");
			} else {
				System.out.println(":: Download error");
			}
			
			// clean up
			out.close();

			
		}
	}

	
	/**
	 * Here is where the files are uploaded
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		this.responder = new Responder(response);
		String jsonp = Requester.callbackForJSONP(request);
		System.out.println(jsonp);
		// the requested path
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
				Session.canUpdateResource(request.getSession(), uri))
			? FileController.pathFromURI(uri)
			: null;
		if (/* set the failure conditions*/
				path == null) {
			responder.jsonp(jsonp).error(403, "Cannot modify resource");
			this.responder.close();
			this.responder = null;
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
						responder.jsonp(jsonp).error(400, "No file attached!");
					} else {
						String fileName = item.getName();
						String newPath = FS.concatPath(path, fileName);
						if (FileData.save(newPath, in)) {
							// SUCCESS!
							// TODO: add progress indicators
							responder.jsonp(jsonp).send(FileController.getFileInformation(newPath, Filter.defaultFilter()));
							
						} else {
							responder.jsonp(jsonp).error(500, "Could not create the file!");
						}
					}
					in.close();
				}
			} catch (Exception e) {
				responder.jsonp(jsonp).error(500, "Error while uploading the file");
				e.printStackTrace();
			}
		}
		this.responder = null;
	}	
}