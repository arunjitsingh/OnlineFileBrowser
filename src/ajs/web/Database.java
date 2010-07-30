package ajs.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import org.json.JSONArray;
import org.json.JSONObject;

public class Database {
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/jdbc?" +
												"user=jdbc&password=9XcwNbmxCcUAdPQw";
	/**
	 * @field SELECT_USER_ID String{1}:username, String{2}:password
	 */
	private static final String SELECT_USER_ID = "SELECT id FROM users WHERE username=? AND password=PASSWORD(?)";
	
	/**
	 * @field SELECT_USER_INFO int{1}:id
	 */
	private static final String SELECT_USER_INFO = "SELECT username, name, home FROM users WHERE id=?";
	
	/**
	 * @field SELECT_USER_GROUPS int{1}:user_id
	 */
	private static final String SELECT_USER_GROUPS ="SELECT groups.group, groups.name, groups.home "+
													"FROM groups INNER JOIN UserGroups " +
													"ON groups.id = UserGroups.group_id " +
													"WHERE UserGroups.user_id = ?";
	
	/**
	 * @field SELECT_USER_ACCESS int{1}:user_id, int{2}:user_id
	 */
	private static final String SELECT_USER_ACCESS ="SELECT users.home " +
													"FROM users " +
													"WHERE users.id = ? " +
													"UNION " +
													"SELECT groups.home " +
													"FROM groups INNER JOIN UserGroups AS ug " +
													"ON groups.id = ug.group_id " +
													"WHERE ug.user_id = ?";
	
	private static Connection connection = null;
	
	private static void startConnection() {
		if (connection == null) {
			try {
				Class.forName(Database.DB_DRIVER).newInstance();
				connection = DriverManager.getConnection(Database.DB_CONNECTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getUser(String username, String password) {
		if (connection == null) Database.startConnection();
		PreparedStatement query = null;
		ResultSet result = null;
		int id = 0;
		try {
			query = connection.prepareStatement(Database.SELECT_USER_ID);
			query.setString(1, username);
			query.setString(2, password);
			result = query.executeQuery();
			if (result.first()) {
				id = result.getInt("id");
			}
		} catch (Exception e) {
			
		}
		return id;
	}
	
	public static JSONObject getUserInfo(int id) {
		if (connection == null) Database.startConnection();
		PreparedStatement query = null;
		ResultSet result = null;
		JSONObject info = null;
		try {
			query = connection.prepareStatement(Database.SELECT_USER_INFO);
			query.setInt(1, id);
			result = query.executeQuery();
			info = new JSONObject();
			if (result.first()) { // expect only one result.. id is (UNIQUE) PRIMARY KEY
				info.put("username", result.getString("username"))
					.put("name", result.getString("name"))
					.put("home", result.getString("home"))
				;
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONArray getUserGroups(int id) {
		if (connection == null) Database.startConnection();
		PreparedStatement query = null;
		ResultSet result = null;
		JSONArray groups = null;
		try {
			query = connection.prepareStatement(Database.SELECT_USER_GROUPS);
			query.setInt(1,id);
			result = query.executeQuery();
			groups = new JSONArray();
			while (result.next()) {
				groups.put(new JSONObject()
								.put("group", result.getString("group"))
								.put("name", result.getString("name"))
								.put("id", result.getString("home"))
				);
			}
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONArray getUserAccess(int id) {
		if (connection == null) Database.startConnection();
		PreparedStatement query = null;
		ResultSet result = null;
		JSONArray access = null;
		try {
			query = connection.prepareStatement(Database.SELECT_USER_ACCESS);
			query.setInt(1,id);
			query.setInt(2,id);
			result = query.executeQuery();
			access = new JSONArray();
			while (result.next()) {
				access.put(result.getString("home"));
			}
			return access;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}