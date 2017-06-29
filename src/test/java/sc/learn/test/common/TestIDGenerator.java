package sc.learn.test.common;

import org.junit.Test;

import sc.learn.common.util.IDGenerator;

public class TestIDGenerator {

	@Test
    public void testGenerate() {
		long start=System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            long id = IDGenerator.generate("a");
//            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
            if((System.currentTimeMillis()-start)/1000>=1){
            	System.out.println(i);
            	break;
            }
        }
    }
}
