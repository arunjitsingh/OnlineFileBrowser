package ajs.util;

import com.google.gson.*;

public class JSON {
	public static String stringify(Object o) {
		return new Gson().toJson(o);
	}
	
	public static org.json.JSONObject parse(String json) {
		try {
			return new org.json.JSONObject(json);
		} catch(Exception e) {
			return null;
		}
	}
		
	@SuppressWarnings("unchecked")
	public static Object parse(String json, Class klass) {
		return new Gson().fromJson(json, klass);
	}
}