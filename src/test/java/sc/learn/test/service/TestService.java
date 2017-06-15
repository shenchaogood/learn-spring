package sc.learn.test.service;

import org.junit.Test;

import sc.learn.common.util.ThriftUtil;
import sc.learn.manage.service.UserService;

public class TestService {

	@Test
	public void testThriftServer(){
		ThriftUtil.startThriftServer(new UserService());
	}
	
	@Test
	public void testThriftClient(){
		
	}
}
