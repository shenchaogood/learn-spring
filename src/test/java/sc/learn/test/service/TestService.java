package sc.learn.test.service;

import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Test;

import sc.learn.common.util.ThriftUtil;
import sc.learn.manage.service.TUser;
import sc.learn.manage.service.TUserService;
import sc.learn.manage.service.UserService;

public class TestService {

	@Test
	public void testThriftServer() throws Exception {
		ThriftUtil.startThriftServer(new UserService());
		Thread.sleep(500000);
	}

	@Test
	public void testThriftClient() throws Exception {
		TUserService.Iface service = ThriftUtil.getIfaceClient(TUserService.Iface.class, 5000);
		service.save(new TUser().setEmail("6").setName("s").setPassword("g"));

		TUserService.AsyncIface service2 = ThriftUtil.getAsyncIfaceClient(TUserService.AsyncIface.class, 5000);
		System.out.println(service);
		service2.save(new TUser().setEmail("6").setName("s").setPassword("g"), new AsyncMethodCallback<Void>() {
			@Override
			public void onError(Exception exception) {
				System.out.println(exception);
			}
			@Override
			public void onComplete(Void response) {
				System.out.println("onComplete");
			}
		});
	}
}
