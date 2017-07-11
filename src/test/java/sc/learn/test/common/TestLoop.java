package sc.learn.test.common;

import org.junit.Test;

public class TestLoop {
	
	@Test
	public void testJump(){
		jump:
			while(true){
				for(int i=0;i<10;i++){
					if(i==5){
						break jump;
					}
					System.out.println(i);
				}
			}
	}
	
	
}
