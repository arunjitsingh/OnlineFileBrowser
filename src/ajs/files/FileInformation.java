package ajs.files;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.LinkedList;


public class FileInformation implements Serializable {
	// Initialize everything (and everything's an object) to NULL (null)
	
	/**
	 * Compiler generated UID
	 */
	private static final long serialVersionUID = 4413756813555274629L;
	/**
	 * @field id
	 * Unique IDs that are part of the path and can identify directories relative to the 
	 * servlet root. @see ajs.files.FileController.getInitialPath() to obtain a root-relative
	 * absolute path.
	 * These fields are also useful for front-end applications
	 */
	public  String			 id		=	null;		
//	public 	String 		   guid 	=	null;		// Removing this to reduce response size, esp. for deep files
//	public 	String 		   name		= 	null;		// Not required to be serialized, can be computed on client
//	public 	String 		   path 	=	null;		// Might deprecate this as it is not required after 'id' & 'guid'
	public 	String 		   type		= 	null;
//	public 	String 	 	    ext		= 	null;		// Not required to be serialized, can be computed on client
	public 	Long 		   size		= 	null;
	public  Long	 modifiedAt		=	null;		// MILLISECONDS
	public 	Boolean isDirectory 	= 	null;
	
	public LinkedList<FileInformation> children = null;
	
//	public String 		  parent 	= 	null;		// Possibly temporary.. Do I really need this?
	
	/**
	 * @constructor Some constructors calling the designated initializer
	 * @see ajs.files.FileInformation(4)
	 */
	public FileInformation(File file) {
		this(file, 1, null);
	}
	public FileInformation(File file, int depth) {
		this(file, depth, null);
	}
	public FileInformation(File file, FilenameFilter filter) {
		this(file, 1, filter);
	}
	/**
	 * @constructor (4)	The designated initializer for the class
	 * @param file		The file to profile
	 * @param depth		Traversal depth
	 * @param filter	A FilenameFilter. @see ajs.files.Filter
	 */
	public FileInformation(File file, int depth, FilenameFilter filter) {
		if (file.exists()) {
			// Get the path
			String path = this.sanitizePath(file.getPath());
					
			// Set the ID. These are also the URIs for their respective files
			this.id = FileController.URIFromPath(path);
//			this.guid = this.id;
			
			// Get the name
//			this.name = file.getName();
			
			// Get the parent directory
//			if (!this.name.equals(FS.ROOT_NAME)) {
//				this.parent = this.pathAsURI(this.sanitizePath(file.getParent()));
//			}

			// Get the last modified epoch TS
			this.modifiedAt = file.lastModified();
			
			// If it's a directory, set it's contents
			if (file.isDirectory()) {				
				this.type = "DIR";
				this.isDirectory = true;
				this.size = this.getDirectorySize(file);
				
				// Might change this in the future for trees to {depth--} ..
				if (depth == 1) { /* if (0 != depth--) */
					this.children = new LinkedList<FileInformation>();
					FileInformation info = null;
					File[] files = (filter != null) ? file.listFiles(filter) : file.listFiles();
					for (File f : files) {
						/* info = new FileInformation(f, depth, filter); */
						info = new FileInformation(f, 0, filter);	// .. and {0} here to {depth}
						this.children.add(info);
						info = null;
					}
				}
			} else {
				this.type = "FILE";
				this.size = file.length();
				this.isDirectory = false;
				this.children = null;
			}
		}
	}
	
	/**
	 * A helper function to determine size of directories through recursion
	 */
	private long getDirectorySize(File dir) {
		if (!dir.canRead()) {
			if (!dir.setReadable(true)) {
				System.out.println("FileInformation#(getDirectorySize)");
				System.out.println("There was an error reading path " + dir.getAbsolutePath());
				System.out.println("Silently ignoring the problem and adding 0B size for this path");
				return 0;
			}
		}
		if (dir.isFile()) {
			return dir.length();
		} else {
			long dirsize = 0;			
			File files[] = dir.listFiles();
			for (File file : files) {
				dirsize += getDirectorySize(file);
			}
			return dirsize;
		}
	}
	
	/**
	 * A convinience function to sanitize the path to hide the GLOBAL_ROOT to be relative to ROOT
	 * @param path the raw path to sanitize
	 * @return sanitized path
	 */
	private String sanitizePath(String path) {
		return path.substring(path.indexOf(FS.sharedInstance().getRootName()));
	}
}