package ajs.util;

import java.util.regex.Pattern;

public class Regex {

	/**
	 * To check whether the needle is in the haystack
	 * Uses {@link java.util.regex.Matcher#find()}
	 * @param needle   the string to search for
	 * @param haystack the string to look in 
	 * @return true, if needle is in haystack
	 */
	public static boolean find(String needle, String haystack) {
		return Pattern.compile(needle).matcher(haystack).find();
	}
	
	/**
	 * To check whether the needle is in the haystack
	 * Uses {@link java.util.regex.Matcher#matches()}
	 * @param needle   the string to search for
	 * @param haystack the string to look in 
	 * @return true, if needle is in haystack
	 */
	public static boolean matches(String needle, String haystack) {
		return Pattern.compile(needle).matcher(haystack).matches();
	}
	
}