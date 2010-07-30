package ajs.files;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * A singleton class that contains some file-system related information.
 * {@code FS.initialize} is used to load the default properties 
 * from the {@code default.properties} file
 * @author arunjitsingh
 *
 */

public class FS {
	private static FS THIS = null;
	
	/**
	 * @field GLOBAL_ROOT (String) is the string value prepended to the application's root
	 * For example, if the application's files and directories were stored on, say, a different volume,
	 * {@code GLOBAL_ROOT} could be set to "/Volumes/Data/", ending always with the system's 
	 * {@code File.separator}
	 * @see the properties file {@link default.properties}.
	 */
	private  String 	GLOBAL_ROOT = null;
	
	private  String  ROOT_NAME = null;
	private  String 		ROOT = null;
	private  File 	ROOT_DIR = null;
	
	private  String USERS_NAME = null;
	private  String 	   USERS = null;
	private  File    USERS_DIR = null;
	
	private  String TRASH_NAME = null;
	private  String 	   TRASH = null;
	private  File    TRASH_DIR = null;

	private  String TEMP_NAME = null;
	private  String 	   TEMP = null;
	private  File    TEMP_DIR = null;
	
	private  String GROUPS_NAME = null;
	private  String 	   GROUPS = null;
	private  File    GROUPS_DIR = null;
	
	
	public  static String RE_VALID_FILE_NAME = null;
	public  static String 	     RE_SPECIALS = null;
	public  static String     RE_NO_SPECIALS = null;	// if true, file allowed
	
	
	private FS(String propertiesPath) {
		// FS not inited...
		File file = new File(propertiesPath);
		System.out.println("Initializing using:\n" + file.getAbsolutePath());
		
		Properties def = new Properties();
		FileInputStream fin = null;
		try {
			 fin = new FileInputStream(propertiesPath);
			 def.load(fin);
			 fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nLoaded properties:\n" + def.stringPropertyNames());
		
		GLOBAL_ROOT = def.getProperty("GLOBAL_ROOT");
		
		ROOT_NAME = def.getProperty("ROOT_NAME");
		ROOT = FS.concatPath(this.GLOBAL_ROOT, this.ROOT_NAME);
		ROOT_DIR = new File(this.ROOT);
		if (!ROOT_DIR.exists() || !ROOT_DIR.isDirectory()) {
			System.out.println(ROOT + " error");
		}
		
		TRASH_NAME = def.getProperty("TRASH_NAME");
		TRASH = FS.concatPath(this.ROOT, this.TRASH_NAME);
		TRASH_DIR = new File(this.getTrash());
		if (!TRASH_DIR.exists() || !TRASH_DIR.isDirectory()) {
			System.out.println(TRASH + " error");
		}
		
		USERS_NAME = def.getProperty("USERS_NAME");
		USERS = FS.concatPath(this.ROOT, this.USERS_NAME);
		USERS_DIR = new File(this.getUsers());
		if (!USERS_DIR.exists() || !USERS_DIR.isDirectory()) {
			System.out.println(USERS + " error");
		}
		
		GROUPS_NAME = def.getProperty("GROUPS_NAME");
		GROUPS = FS.concatPath(this.ROOT, this.GROUPS_NAME);
		GROUPS_DIR = new File(this.getGroups());
		if (!GROUPS_DIR.exists() || !GROUPS_DIR.isDirectory()) {
			System.out.println(GROUPS + " error");
		}		
		
		TEMP_NAME = def.getProperty("TEMP_NAME");
		TEMP = FS.concatPath(this.ROOT, this.TEMP_NAME);
		TEMP_DIR = new File(this.getTemp());
		if (!TEMP_DIR.exists() || !TEMP_DIR.isDirectory()) {
			System.out.println(TEMP + " error");
		}
		
		
		// the static fields
		RE_NO_SPECIALS = def.getProperty("RE_NO_SPECIALS");
		if (RE_NO_SPECIALS == null) {
			RE_NO_SPECIALS = "^[^\\.|\\*].*$";
		}
		
		RE_SPECIALS = def.getProperty("RE_SPECIALS");
		if (RE_SPECIALS == null) {
			RE_SPECIALS = "^[\\.|\\*].*$";
		}
		
		RE_VALID_FILE_NAME = def.getProperty("RE_VALID_FILE_NAME");
		if (RE_VALID_FILE_NAME == null) {
			RE_VALID_FILE_NAME = "^[^\\\\./:\\*\\?\"<>\\|]{1}[^\\/:\\*\\?\"<>\\|]{0,254}$";
		}
	}
	
	
	public static FS initialize(String path) {
		if (THIS == null) THIS = new FS(path);
		return THIS;
	}
	
	public static FS sharedInstance() {
		return THIS;
	}	
	
	public static boolean initialized() {
		return (THIS != null);
	}
	
	// ROOT INFORMATION
	public  String getRoot() {
		return this.ROOT;
	}
	public  String getRootName() {
		return this.ROOT_NAME;
	}
	public  File getRootDir() {
		return this.ROOT_DIR;
	}
	
	// TRASH INFORMATION
	public  String getTrash() {
		return this.TRASH;
	}
	public  String getTrashName() {
		return this.TRASH_NAME;
	}
	public  File getTrashDir() {
		return this.TRASH_DIR;
	}
	
	// USERS INFORMATION
	public  String getUsers() {
		return this.USERS;
	}
	public  String getUsersName() {
		return this.USERS_NAME;
	}
	public  File getUsersDir() {
		return this.USERS_DIR;
	}
	
	// GROUPS INFORMATION
	public  String getGroups() {
		return this.GROUPS;
	}
	public  String getGroupsName() {
		return this.GROUPS_NAME;
	}
	public  File getGroupsDir() {
		return this.GROUPS_DIR;
	}
	
	// TEMP INFORMATION
	public  String getTemp() {
		return this.TEMP;
	}
	public  String getTempName() {
		return this.TEMP_NAME;
	}
	public  File getTempDir() {
		return this.TEMP_DIR;
	}
	
	
	public boolean isProtectedPath(String path) {
		return (path.equalsIgnoreCase(ROOT)
				|| path.equalsIgnoreCase(USERS)
				|| path.equalsIgnoreCase(GROUPS)
				|| path.equalsIgnoreCase(TRASH)
				|| path.equalsIgnoreCase(TEMP)
				);
	}
	
	/**
	 * Use this function to concatenate two paths
	 * @param path The left side
	 * @param with The right side
	 * @return Concatenated path
	 */
	public static String concatPath(String path, String with) {
		if (/*Conditions to add File.separator*/
				!(path.endsWith(File.separator) 
				|| with.startsWith(File.separator)))
			path += File.separator;
		return path + with;
	}
	
}