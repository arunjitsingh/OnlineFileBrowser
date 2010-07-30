package tests;

import ajs.files.FS;
import ajs.files.FileController;
import ajs.files.FileInformation;
import ajs.files.Filter;
import ajs.util.JSON;

public class TestFileController {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String path = FS.USERS + "/arunjitsingh/deleteme";
//		System.out.println("Will try to create " + path);
//		System.out.println("Can write to path: " + new File(path).canWrite());
//		System.out.println("Can write to path's parent: " + new File(path).getParentFile().canWrite());
//		System.out.println(FileController.createDirectory(path));
//		System.out.println(FileController.deleteFile(path, true));
//		if (!FS.initialized()) {
//			System.out.println("FS not available");
//			System.exit(2);
//		}
		FS.initialize("default.properties");
		String path = FS.sharedInstance().getUsers() + "/arunjitsingh";
		System.out.println("Information for " + path);
		FileInformation fileInfo = FileController.getFileInformation(path, new Filter(FS.RE_NO_SPECIALS).get());
		System.out.println(JSON.stringify(fileInfo));
	}
}