package sc.learn.test.jdk;

import org.junit.Test;

public class TestInteger {

	@Test
	public void test(){
		String bParam=Long.toBinaryString(7L);
        for(int i=bParam.length()-1,j=1;i>=0;i--,j++){
        	if(bParam.charAt(i)=='1'){
        		System.out.println(j);
        	}
        }
	}
}
