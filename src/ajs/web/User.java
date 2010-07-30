package ajs.web;

public class User {

	public static final int   INFO = 0;
	public static final int GROUPS = 1;
	public static final int ACCESS = 2;
	
	public static Integer exists(String username, String password) {
		int id = Database.getUser(username, password);
		if (id > 0) {
			return Integer.valueOf(id);
		} else {
			return null;
		}
	}
	
	public static Object get(int what, Integer userID) {
		switch(what) {
		case INFO:
			return Database.getUserInfo(userID.intValue());
		case GROUPS:
			return Database.getUserGroups(userID.intValue());
		case ACCESS:
			return Database.getUserAccess(userID.intValue());
		default:
			return null;
		}
	}
	
}