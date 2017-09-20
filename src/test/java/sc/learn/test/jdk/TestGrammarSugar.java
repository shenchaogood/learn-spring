package sc.learn.test.jdk;

import java.util.List;

import org.junit.Test;

public class TestGrammarSugar {

	private List<Integer> list;//=[1,2,3,4];
	
	public void ifdef() {
		if (true) {
			System.out.println("true");
		} else {// 此处有警告--DeadCode
			System.out.println("false");
		}
	}

	private int xx(List<Integer> param) {
		return 0;
	}

	// private String xx(List<String> param){
	// return "";
	// }

	@Test
	public void test() {
		Integer a=1;
		Integer b=1;
		int c=1;
		System.out.println(a==b);
		System.out.println(a==c);
		Integer d=128;
		Integer e=128;
		int f=128;
		System.out.println(d==e);
		System.out.println(d==f);
		
		
		
		xx(list);
		//jdk1.6以上javac可以编译通过
		// xx(new ArrayList<String>());
	}
}
