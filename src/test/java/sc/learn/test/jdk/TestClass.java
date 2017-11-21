package sc.learn.test.jdk;

import java.util.Arrays;

import org.junit.Test;

public class TestClass {
	
	static class I{}
	class M{}

	@Test
	public void test() {
		Class<?>[] classes = getClass().getDeclaredClasses();
		System.out.println(Arrays.toString(classes));
	}
}
