package ajs.web;

import java.io.File;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ajs.files.FileController;

public class Session {

	public static final String     ID_KEY = "user-id";
	public static final String   INFO_KEY = "user-info";
	public static final String GROUPS_KEY = "user-groups";
	public static final String ACCESS_KEY = "user-access"; 
	
	public static final String[] SESSION_KEYS = {ID_KEY, INFO_KEY, GROUPS_KEY, ACCESS_KEY};
	
	/**
	 * Cleans the URI of a leading {@link File.separator}
	 * @param uri
	 * @return A path without a leading {@link File.separator}
	 */
	private static String cleanURI(String uri) {
		return (/*Conditions requiring a cleanup*/
				uri != null 
				&& uri.startsWith(File.separator)
				&& uri.length() > 1) 
			? uri.substring(1) 
			: uri;
	}

	public static Object get(HttpSession session, String key) {
		return session.getAttribute(key);
	}
	
	public static HttpSession set(HttpSession session, String key, Object value) {
		session.setAttribute(key, value);
		return session;
	}
	
	public static HttpSession del(HttpSession session, String key) {
		session.removeAttribute(key);
		return session;
	}
	
	public static boolean has(HttpSession session, String key) {
		if (session.getAttribute(key) != null) return true;
		else return false;
	}
	
	@SuppressWarnings("unchecked")
	public static Enumeration keys(HttpSession session) {
		return session.getAttributeNames();
	}
	
	public static boolean hasKeys(HttpSession session, String[] keys) {
		boolean success = false;
		for (int i = 0; i < keys.length; ++i) {
			if (!(success = Session.has(session, keys[i]))) break;
		}		
		return success;
	}
	
	/*
	 * Session init and destroy
	 */
	
	public static HttpSession initialize(HttpSession session) {
		return Session.initialize(session, (Integer)Session.get(session, ID_KEY));
	}

	public static HttpSession initialize(HttpSession session, Integer id) {
		if (id != null) {
			session.setAttribute(ID_KEY, id);
			session.setAttribute(INFO_KEY, User.get(User.INFO, id));
			session.setAttribute(GROUPS_KEY, User.get(User.GROUPS, id));
			session.setAttribute(ACCESS_KEY, User.get(User.ACCESS, id));
		}
		return session;
	}
	
	public static boolean isInitialized(HttpSession session) {
		return Session.hasKeys(session, Session.SESSION_KEYS);
	}
	
	@SuppressWarnings("unchecked")
	public static void kill(HttpSession session) {
		Enumeration keys = session.getAttributeNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			session.removeAttribute(key);
		}
		session.invalidate();
	}
	
	/*
	 * The real reason for this class
	 */
	public static JSONObject user(HttpSession session) {
		try {
			return new JSONObject()
						.put("user-id", session.getAttribute(ID_KEY))
						.extend(session.getAttribute(INFO_KEY))
						.put("groups", session.getAttribute(GROUPS_KEY))
						.put("access", session.getAttribute(ACCESS_KEY));
		} catch (JSONException jsone) {
			jsone.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static JSONArray groups(HttpSession session) {
		return (JSONArray)session.getAttribute(GROUPS_KEY);
	}
	
	public static JSONArray access(HttpSession session) {
		return (JSONArray)session.getAttribute(ACCESS_KEY);
	}
	
	/**
	 * Validates a URI
	 * @param session
	 * @param uri
	 * @return valid path string
	 * @deprecated Use {@link Session.canRetrieveResource()} or {@link Session.canUpdateResource()}
	 */
	public static String validateURI(HttpSession session, String uri) {
		return Session.validateURI(session, uri, null);
	}
	
	/**
	 * Validates a URI
	 * @param session
	 * @param uri
	 * @return valid path string
	 * @deprecated Use {@link Session.canRetrieveResource()} or {@link Session.canUpdateResource()}
	 */
	public static String validateURI(HttpSession session, String uri, Boolean exact) {
		boolean valid = false;
		uri = cleanURI(uri);
		Integer id = (Integer)session.getAttribute(ID_KEY);
		if (id != null) {
			try {
				JSONArray access = Session.access(session);
				if (access == null) {
					System.out.println("Session.validatePath .. access is null");
					return null;
				}
				boolean isRoot = access.contains(":root"); 
				if (exact == null) {
					if (isRoot) {
						// all paths are valid as user is allowed at root
						valid = true;
					} else if (isRootURI(uri)) {
						valid = false;
					} else {
						for (int i = 0; i < access.length(); ++i) {
							String prefix = access.getString(i);
							if (uri.startsWith(prefix)) {
								valid = true;
								break;
							}
						}
					}
				} else {
					if (isRoot && isRootURI(uri)) {
						valid = exact;
					} else {
						
					}
				}
			} catch (JSONException jsone) {
				jsone.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (valid) {
			return FileController.pathFromURI(uri);
		} else {
			return null;
		}
	}
	
	public static boolean canRetrieveResource(HttpSession session, String uri) {
		boolean valid = false;
		
		uri = cleanURI(uri);
		if (Session.isInitialized(session)) {
			try {
				JSONArray access = Session.access(session);
				if (access == null) {
					System.out.println("Session.canRetrieveResource .. access is null");
					return false;
				}
				if (access.contains(":root")) { 	// user is admin.. retrieve everything
					valid = true;
				} else if (isRootURI(uri)) {		// user not admin but root requested
					valid = false;
				} else {							// user not admin, root not requested
					for (int i = 0; i < access.length(); ++i) {
						String prefix = access.getString(i);
						if (uri.startsWith(prefix)) {
							valid = true;			// found the URI and it is valid
							break;
						}
					}
				}
			} catch (Exception e) {
				valid = false;
				e.printStackTrace();
			}
		}
		
		return valid;
	}
	
	public static boolean canUpdateResource(HttpSession session, String uri) {
		boolean valid = false;
		
		uri = cleanURI(uri);
		if (Session.isInitialized(session)) {
			if (FileController.isProtectedURI(uri)) {
				valid = false;							// no-one can change these resources
			} else {
				try {
					JSONArray access = Session.access(session);
					if (access.contains(":root")) {		// user is admin
						valid = true;
					} else {
						for (int i = 0; i < access.length(); ++i) {
							String prefix = access.getString(i);
							if (uri.startsWith(prefix) && !uri.equalsIgnoreCase(prefix)) {
								valid = true;			// found the URI and it is valid
								break;
							}
						}
					}
				} catch (Exception e) {
					valid = false;
					e.printStackTrace();
				}
			}
		}
		
		return valid;
	}
	
	public static boolean canUploadToResource(HttpSession session, String uri) {
		boolean valid = false;
		
		uri = cleanURI(uri);
		if (Session.isInitialized(session)) {
			try {
				JSONArray access = Session.access(session);
				if (access == null) {
					System.out.println("Session.canUploadToResource .. access is null");
					return false;
				}
				if (access.contains(":root")) { 	// user is admin.. retrieve everything
					valid = true;
				} else if (isRootURI(uri)) {		// user not admin but root requested
					valid = false;
				} else {							// user not admin, root not requested
					for (int i = 0; i < access.length(); ++i) {
						String prefix = access.getString(i);
						if (uri.startsWith(prefix)) {
							valid = true;			// found the URI and it is valid
							break;
						}
					}
				}
			} catch (Exception e) {
				valid = false;
				e.printStackTrace();
			}
		}
		
		return valid;
	}
	
	private static boolean isRootURI(String uri) {
		return (uri == null || uri.equals("/"));
	}
}