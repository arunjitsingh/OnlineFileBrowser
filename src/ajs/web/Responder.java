package ajs.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class Responder {
	private HttpServletResponse response = null;
	private PrintWriter				 out = null;
	private String		   jsonpCallback = null;
	
	public Responder(HttpServletResponse response) {
		this.response = response;
		try {
			this.out = this.response.getWriter();
		} catch(IOException ioe) {
			// can't really do much here, just
			response.setStatus(500);
			ioe.printStackTrace();
		}
	}
	
	public Responder error(int code, String reason) {
		InformationResponse info = new InformationResponse(false);
		info.setMessage(reason);
		String output = info.toJSON();		
		if (this.jsonpCallback != null) {
			output = this.jsonpCallback + "(\"" + output + "\")";
		}
		this.response.setStatus(code);
		this.response.setContentType("text/json");
		this.out.print(output);
		this.out.flush();
		return this;
	}
	
	public Responder json() {
		this.jsonpCallback = null;
		return this;
	}
	
	/**
	 * @deprecated 
	 * @see {@link Responder#json()#send(Object)}
	 */
	public Responder json(Object content) {
		return this.json(content, 200);
	}
	
	/**
	 * @deprecated 
	 * @see {@link Responder#json()#send(int, Object)}
	 */
	public Responder json(Object content, int code) {
		this.jsonpCallback = null;
		this.response.setStatus(code);
		this.response.setContentType("text/json");
		this.out.print(this.serializeObject(content));
		this.out.flush();
		return this;
	}
	
	public Responder jsonp(String callback) {
		this.jsonpCallback = callback;
		return this;
	}
	
	public Responder send(Object content) {
		return this.send(200, content);
	}
	
	public Responder send(int code, Object content) {
		String output = this.serializeObject(content);
		if (this.jsonpCallback != null) {
			output = this.jsonpCallback + "(\"" + output + "\")";
		}
		this.response.setStatus(code);
		this.response.setContentType("text/json");
		this.out.print(output);
		this.out.flush();
		return this;
	}
	
	public void close() {
		this.out.close();
	}
	
	private String serializeObject(Object content) {
		return (new InformationResponse(content)).toJSON();
	}
	
	protected void finalize() {
		this.out.close();
	}
}