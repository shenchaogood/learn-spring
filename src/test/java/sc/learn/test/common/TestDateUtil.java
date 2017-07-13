package sc.learn.test.common;

import java.text.ParseException;
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
	
	@Test
	public void testDate() throws ParseException{
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		Date date=df.parse("2017-07-09");
		System.out.println(date.getTime());
		
		System.out.println(new Date(1481040000000L));
	}
	
	
}
