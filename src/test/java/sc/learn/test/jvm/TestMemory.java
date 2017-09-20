package sc.learn.test.jvm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestMemory {
	
	@Test
	public void testHeap(){
		List<TestMemory> list=new ArrayList<>();
		while(true){
			list.add(new TestMemory());
		}
	}

}
