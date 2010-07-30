package ajs.web;

import ajs.util.JSON;


/**
 * The response information. Includes status data along with the content
 * @author arunjitsingh
 *
 */
public class InformationResponse {
	public boolean  status = true;
	public String  message = null;
	public String      now = null;		// ONLY USED FOR OUTPUT.. GENERATED WITH JSON
	public Object  content = null;
	
	public InformationResponse(boolean successful) {
		if (!successful) {
			this.status = false;
		}
	}
	public InformationResponse(Object content) {
		this.content = content;
	}

	public void setMessage(String value) {
		this.message = value;
	}
	
	public String toJSON() {
		this.now = ajs.util.DateTime.timestamp(ajs.util.DateTime.NOW);
		return JSON.stringify(this);
	}
}