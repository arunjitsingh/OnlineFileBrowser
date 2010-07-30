package ajs.web;

public class Error {
	public int _code = 0;
	public void setCode(int value) {this._code = value;}
	public int code() {return this._code;}
	
	public String _error = "";
	public void setError(String value) {this._error = value;}
	public String error() {return this._error;}
}