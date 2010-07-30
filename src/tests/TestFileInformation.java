package tests;

import java.io.File;

import ajs.files.FS;
import ajs.files.FileInformation;
import ajs.files.Filter;
import ajs.util.JSON;

public class TestFileInformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FS.initialize("default.properties");
		if(!FS.initialized()){
			System.out.println("FS ERROR!");
			System.exit(4);
		}
		String path = FS.sharedInstance().getUsers() + "/arunjitsingh";
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("File not found! " + path);
		}
		FileInformation fileInfo = new FileInformation(file, Filter.defaultFilter());
		String json = JSON.stringify(fileInfo);
		System.out.println(json);
	}
}
