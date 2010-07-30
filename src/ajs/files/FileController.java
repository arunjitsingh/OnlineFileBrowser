package ajs.files;

import ajs.util.DateTime;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @class FileController is the main controlling class responsible for file operations
 * It uses the FileInformation class for retrieving file information.
 * Note: Uses only pathname (String) in it's API
 * @see ajs.files.FileInformation
 * @author arunjitsingh
 *
 */
public class FileController {
	
	protected static boolean lastOperationSuccess = false;
	
	public static final String FILE_404 = "File does not exist!";
	
	
	public static ajs.web.Error error = new ajs.web.Error();
	
	public static boolean getLastOperationSuccess() {
		return FileController.lastOperationSuccess;
	}

	public static String getInitialPath() {
		return FS.sharedInstance().getRoot();
	}
	
	
	/**
	 * Concatenates path information to a URI
	 * @param uri The requested resource
	 * @return
	 */
	public static String pathFromURI(String uri) {
		return FS.concatPath(FileController.getInitialPath(), uri);
	}
	
	/**
	 * Processes a path by removing back-referencing path information to obtain a
	 * Uniform Resource Identifier (URI)
	 * @param path
	 * @return The path's resource's URI
	 */
	public static String URIFromPath(String path) {
		//System.out.println("FileController.URIFromPath.. path=" + path);
		if (/*Conditions that root was asked*/
				path.equalsIgnoreCase(FS.sharedInstance().getRoot())
				|| path.equalsIgnoreCase(FS.sharedInstance().getRoot()+"/")
				|| path.equalsIgnoreCase(FS.sharedInstance().getRootName())
				|| path.equalsIgnoreCase(FS.sharedInstance().getRootName()+"/")) {
			return ":root";
		} else {
			return path.substring(path.indexOf(FS.sharedInstance().getRootName())
					+ FS.sharedInstance().getRootName().length()+1);
		}
	}
	
	
	/**
	 * Checks with the file system if the uri is of a protected path
	 * @param uri The requested resource
	 * @return
	 */
	public static boolean isProtectedURI(String uri) {
		return FS.sharedInstance().isProtectedPath(pathFromURI(uri));
	}
	
	
	/**
	 * Create a new directory/make writeable an existing directory
	 * @param path the directory path
	 * @return true if directory was created/made writeable
	 */	
	public static boolean createDirectory(String path) {
		if (!FS.initialized()) {
			System.out.println("FS not inited!");
			return false;
		}
		File file = new File(path);
		boolean success = false;
		if (file.exists() && file.isDirectory()) {
			//success = true;
			try {
				success = true;
				if (!file.canWrite()){
					success = file.setWritable(true);
				}
			} catch (SecurityException se) {
				System.out.println("FileController.createDirectory .. SecurityException for " + path);
				success = false;
			}
		} else {
			try {
				success = file.mkdirs();
			} catch(SecurityException se) {
				System.out.println("FileController.createDirectory .. SecurityException for " + path);
				return false;
			}
		}
		return FileController.lastOperationSuccess = success;
	}
	
	
	public static boolean moveFile(String from, String to) {
		if (!FS.initialized()) {
			return false;
		}
	
		boolean success = false;
		File file = new File(from);
		if (!file.exists()) {
			FileController.error.setCode(404);
			FileController.error.setError(FileController.FILE_404);
			return FileController.lastOperationSuccess = false;
		}
		try {
			File dest = new File(to);
			success = file.renameTo(dest);
		} catch(Exception e) {
			System.out.println("FileController.moveFile .. Exception for " + file.getPath());
			e.printStackTrace();
			success = false;
			FileController.error.setCode(500);
			FileController.error.setError(e.toString());
		}
		
		return FileController.lastOperationSuccess = success;
	}
	
	
	/**
	 * Delete a file or a folder by moving it to the trash
	 * @param path 			The FILE/DIR to delete
	 * @param permanently	To invoke a thread to permanently remove the file
	 * @return true, if the FILE/DIR was deleted
	 */
	public static boolean deleteFile(String path, boolean permanently) {
		if (!FS.initialized()) {
			return false;
		}
		boolean success = false;
		File file = new File(path);
		if (!file.exists()) {
			FileController.error.setCode(404);
			FileController.error.setError(FileController.FILE_404);
			return FileController.lastOperationSuccess = false;
		}
		success = FileController.moveToTrash(file, permanently);
		return FileController.lastOperationSuccess = success;
	}
	
	private static boolean moveToTrash(File file, boolean invokesRemover) {
		if (!FS.initialized()) {
			return false;
		}
		boolean success = false;
		try {
			File dest = new File(FS.sharedInstance().getTrashDir(), file.getName() + "-" + DateTime.timestamp(DateTime.RAW_TIMESTAMP));
			success = file.renameTo(dest);
			if (invokesRemover) {
				if (file.isDirectory()) {
					new DirectoryRemover(file);
				} else {
					file.delete();
				}
			}
		} catch (Exception e) {
			System.out.println("FileController.moveToTrash .. Exception for " + file.getPath());
			e.printStackTrace();
			success = false;
			FileController.error.setCode(500);
			FileController.error.setError(e.toString());
		}
		return FileController.lastOperationSuccess = success;
	}
	
	public static boolean emptyTrash() {
		if (!FS.initialized()) {
			return false;
		}
		boolean success = false;
		try {
			File[] files = FS.sharedInstance().getTrashDir().listFiles();
			for (File file : files) {
				success = (new DirectoryRemover(file)).success;
			}
		} catch (Exception e) {
			System.out.println("FileController.emptyTrash .. Exception");
			e.printStackTrace();
			success = false;
			FileController.error.setCode(500);
			FileController.error.setError(e.toString());
		}
		return FileController.lastOperationSuccess = success;
	}
	
	
	/**
	 * Get information about a file. By default, uses a depth of 1
	 * @param path		The file path
	 * @param filter	A FilenameFilter
	 * @return instance of FileInformation. @see ajs.files.FileInformation
	 */
	public static FileInformation getFileInformation(String path, FilenameFilter filter) {
		File file = new File(path);
		if (!file.exists()) {
			FileController.error.setCode(404);
			FileController.error.setError(FileController.FILE_404);
			FileController.lastOperationSuccess = false;
			return null;
		}
		return new FileInformation(file, 1, filter);
	}
}

/**
 * A threaded class to permanently remove a DIR
 * @author arunjitsingh
 * NEEDS A LOT OF WORK!!
 */
class DirectoryRemover implements Runnable {
	private Thread directoryRemover;
	private File directory;
	
	public boolean success;
	
	public DirectoryRemover(File dir) {
		this.directory = dir;
		this.success = false;
		this.directoryRemover = new Thread(this);
		this.directoryRemover.start();
	}
	
	private boolean rmdir(File dir) {
		if (dir.isFile()) {
			return this.success = dir.delete();
		} else {
			File files[] = dir.listFiles();
			for (File file : files) {
				this.success = rmdir(file);
				if (!this.success) {
					break;
				}
			}
			return this.success;
		}
	}
	
	public void run() {
		this.rmdir(this.directory);
		FileController.lastOperationSuccess = this.success;
	}
}