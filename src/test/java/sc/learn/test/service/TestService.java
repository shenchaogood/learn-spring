package sc.learn.test.service;

import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Test;

import sc.learn.common.util.ThriftUtil;
import sc.learn.manage.model.TUser;
import sc.learn.manage.service.ManageService;
//import sc.learn.manage.service.UserService;
import sc.learn.manage.service.TManageService;

public class TestService {

	@Test
	public void testThriftServer() throws Exception {
		ThriftUtil.startThriftServer(new ManageService());
		Thread.sleep(2000000);
	}

	@Test
	public void testThriftClient() throws Exception {
		TManageService.Iface service = ThriftUtil.getIfaceClient(TManageService.Iface.class, 5000);
		service.saveUser(new TUser().setEmail("6").setName("s").setPassword("g"));
		service.saveUser(new TUser().setEmail("6").setName("s").setPassword("g"));

		TManageService.AsyncIface service2 = ThriftUtil.getAsyncIfaceClient(TManageService.AsyncIface.class, 5000);
		service2.saveUser(new TUser().setEmail("6").setName("s").setPassword("g"), new AsyncMethodCallback<Void>() {
			@Override
			public void onError(Exception exception) {
				System.out.println(exception);
			}
			@Override
			public void onComplete(Void response) {
				System.out.println("onComplete");
			}
		});
		Thread.sleep(1000);
	}
}
