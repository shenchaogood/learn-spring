package sc.learn.common.util;

import org.joda.time.DateTime;

public abstract class DateTimeUtil {
	
	public static String format(long timestamp,String pattern){
		return new DateTime(timestamp).toString(pattern);
	}
	
	public static String formatCommon (long timestamp){
		return new DateTime(timestamp).toString("yyyyMMdd HH:mm:ss");
	}

}
