package sc.learn.test.common;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

import sc.learn.manage.po.User;

public class TestCollection {
	
	@Test
	public void testTreeMap(){
		TreeMap<User,String> map=new TreeMap<>();
		User u1=new User();
		u1.setId(1);u1.setName("1");
		User u2=new User();
		u2.setId(2);u2.setName("2");
		User u3=new User();
		u3.setId(3);u3.setName("3");
		
		map.put(u1, u1.toString());
		map.put(u2, u2.toString());
		map.put(u3, u3.toString());
		
		for(Entry<User,String> e:map.entrySet()){
			if(e.getKey().getId()==3){
				e.getKey().setId(2);
			}
		}
		
		TreeMap<User,String> nmap=new TreeMap<>();
		
		for(Entry<User,String> e:map.entrySet()){
			nmap.put(e.getKey(), e.getValue());
		}
		
		for(Entry<User,String> e:map.entrySet()){
			System.out.println(e.getKey());
		}
		
		
//		System.out.println(nmap);
		
		System.out.println(nmap.size());
		
	}

}
