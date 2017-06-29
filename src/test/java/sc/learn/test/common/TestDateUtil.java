package sc.learn.test.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class TestDateUtil {

	
	@Test
	public void testTimeZone(){
		System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Harbin"));
        System.out.println(formatter.format(new Date()));
	}
	
	
}
