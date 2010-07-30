package ajs.files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileData {
	private static final int BUFFER_SIZE = 1024;

	/**
	 * To save files to a path, given an input stream containing the bytes to store
	 * @param path The path of the file to save. If a file does not exist at this path,
	 * it will be created. If it exists, it will be over-written
	 * @param in The input stream containing the bytes to store
	 * @return true, if the operation was successful, false, otherwise
	 * @throws IOException For DataOutputStream, FileOutputStream. Given that an InputStream 
	 * exists in the caller, the caller is required to handle IO errors
	 */
	public static boolean save(String path, InputStream in) throws IOException {
		System.out.println("Will save to path: " + path);
		File file = new File(path);
		if (file.exists() || file.createNewFile()) {
			// Was able to create a file at that path
			// Process the input stream
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while (-1 != (bytesRead = in.read(buffer, 0, buffer.length))) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			//clean up
			out.close();
			return true;
		}
		return false;
	}
	
	public static boolean load(String path, OutputStream out) throws IOException {
		return FileData.load(path, out, false);
	}
	public static boolean load(String path, OutputStream out, boolean autoflush) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			// found a file
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = 0;

			// START STREAMING!
			while ( (out != null) && (in != null) && (-1 != (length = in.read(buffer))) ) {
				out.write(buffer, 0, length);
			}
			if (autoflush) out.flush();
			in.close();
			return true;
		}		
		return false;
	}
}