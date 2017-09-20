package sc.learn.test.jdk;

import java.util.regex.Pattern;

import org.junit.Test;

public class TestRegex {

	@Test
	public void test1(){
		boolean b=Pattern.compile("^[\n]", Pattern.DOTALL).matcher("\naaa\n").matches();
		System.out.println(b);
	}
}
