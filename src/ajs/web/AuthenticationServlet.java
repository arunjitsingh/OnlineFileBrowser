package ajs.web;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import ajs.files.FS;

/**
 * Servlet implementation class AuthenticationServlet
 * Manages user authentication
 * @author arunjitsingh
 * @version 0.1.1 : this was introduced
 */
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ServletContext context = null;
	private Responder responder = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthenticationServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.context = getServletContext();
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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		this.responder = new Responder(response);
		HttpSession current = request.getSession();
		Integer id = (Integer)Session.get(current, Session.ID_KEY);
		System.out.println("/auth#doGet(): " + Session.ID_KEY + ": "+id);
		JSONObject object = null;
		if (id != null) {
			object = Session.user(current);
		}
		if (object != null) {
			responder.json().send(object.toString());
		} else {
			responder.json().error(403, "User could not be found");
		}
		this.responder = null;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException {
		// TODO: something
		
	}
}