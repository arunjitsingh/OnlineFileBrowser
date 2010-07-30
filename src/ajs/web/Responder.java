package ajs.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class Responder {
	private HttpServletResponse response = null;
	private PrintWriter				 out = null;
	
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
	
	public void error(int code, String reason) {
		this.response.setStatus(code);
		this.response.setContentType("text/json");
		InformationResponse info = new InformationResponse(false);
		info.setMessage(reason);
		out.print(info.toJSON());
		return;		
	}
	
	public void json(Object content) {
		this.json(content, 200);
	}
	
	public void json(Object content, int code) {
		this.response.setStatus(code);
		this.response.setContentType("text/json");
		InformationResponse info = new InformationResponse(content);
		out.print(info.toJSON());
		return;
	}
	
	public void close() {
		this.out.close();
	}
	
	protected void finalize() {
		this.out.close();
	}
}