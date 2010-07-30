package ajs.web;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import ajs.util.JSON;

/**
 * Parses a request's body as a string or JSON object
 * @version 0.1.1 uses {@link org.json.JSONObject}
 * @version 0.1.0 uses {@link ajs.web.RequestObject}
 * @return
 */
public class Requester {
	private int BUFFER_SIZE = 1024;
	
	private HttpServletRequest request = null;
	private String body = null;
	private JSONObject object = null;
	
	public Requester(HttpServletRequest request) {
		this.request = request;
	}
	
	public JSONObject json() {
		if (this.body == null) this.parseBody();
		return this.object = JSON.parse(this.body);
	}
	
	public JSONObject object() {
		if (this.body == null) this.parseBody(); 
		if (this.object == null) return this.json();
		return this.object;
	}
	
	public String body() {
		if (this.body == null) this.parseBody();
		return this.body;
	}
		
	
	private void parseBody() {
		BufferedReader in = null;
		char[] bytes = new char[BUFFER_SIZE];
		StringBuilder b = new StringBuilder("");
		try {
			in = this.request.getReader();
			int bytesRead = 0;
			while(in != null && -1 != (bytesRead = in.read(bytes, 0, bytes.length))){
				b.append(bytes, 0, bytesRead);
			}
			this.body = b.toString();
		} catch(IOException ioe) {
			this.body = "{\"error\":\"Could not parse the body\"}";
			System.out.println("Could not parse the body");
		}
	}	
}