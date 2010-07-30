package ajs.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

import ajs.files.FS;
import ajs.files.FileController;
import ajs.files.FileData;

/**
 * Servlet implementation class Uploader
 */
public class UploaderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
	private ServletContext context = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploaderServlet() {
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
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		this.doUpload(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		this.doUpload(request, response);
	}
	
	protected void doUpload(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		//System.out.println(this.context.getContextPath());
		String uri = request.getPathInfo();
		String path = (/*Conditions for valid URI*/
				Session.canUploadToResource(request.getSession(), uri))
				? FileController.pathFromURI(uri)
				: null;
		System.out.println("/upload#doUpload .. Requested URI: " + uri);
		System.out.println("/upload#doUpload .. Requested path: " + path);
		if (/* set the failure conditions*/
				path == null) {
			response.sendRedirect(this.context.getContextPath()+"/upload.html#failure");
			return;
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
						System.out.println("/uploads#doPost .. wanted file, got form fields.. NO-OP");
					} else {
						String fileName = item.getName();
						String newPath = FS.concatPath(path, fileName);
						if (FileData.save(newPath, in)) {
							// SUCCESS!
							// TODO: add progress indicators
							response.sendRedirect(this.context.getContextPath()+"/upload.html#success");
							return;
						} else {
							// FAILURE!
							response.sendRedirect(this.context.getContextPath()+"/upload.html#failure");
							return;
						}
					}
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect(this.context.getContextPath()+"/upload.html#failure");
				return;
			}
		}
	}
}