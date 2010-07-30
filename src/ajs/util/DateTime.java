package ajs.util;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateTime {
	public static final String NOW = "yyyy-MM-dd HH:mm:ss";
	public static final String TIMESTAMP = "yyyy-MM-dd-HH-mm-ss-SSS";
	public static final String RAW_TIMESTAMP = "yyyyMMddHHmmssSSS";
	
	public static String timestamp(String format) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());
	}
}