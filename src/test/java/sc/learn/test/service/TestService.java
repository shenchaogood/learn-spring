package sc.learn.test.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Test;

import sc.learn.common.thrift.ThriftProtocolEnum;
import sc.learn.common.util.ThriftUtil;
import sc.learn.common.util.ZkConfig;
import sc.learn.manage.model.TUser;
import sc.learn.manage.service.ManageService;
import sc.learn.manage.service.TManageService;

public class TestService {
	
	private CuratorFramework zkClient=CuratorFrameworkFactory.newClient(
			ZkConfig.ZK_SERVER, ZkConfig.ZK_SESSION_TIMEOUT, ZkConfig.ZK_CONNECTION_TIMEOUT, new ExponentialBackoffRetry(1000,3));

	private int timeout=3000;
	
	@Test
	public void testThrift()throws Exception {
		testThriftServer();
		testThriftClient();
		Thread.sleep(timeout);
	}
	
//	@Test
	public void testThriftServer() throws Exception {
		ThriftUtil.startThriftServer(new ManageService(),ThriftProtocolEnum.BINARY,zkClient);
	}

//	@Test
	public void testThriftClient() throws Exception {
		TManageService.Iface service = ThriftUtil.createClient(TManageService.Iface.class, timeout,ThriftProtocolEnum.BINARY,zkClient);
		TUser user=service.getUserById(1);
		System.out.println(user);
		service.getUserById(1);
		for(int i=0;i<10;i++){
			new Thread(){
				@Override
				public void run() {
					try {
						TUser user = service.getUserById(1);
						System.out.println(user);
						service.saveUser(new TUser().setEmail("6").setName("s").setPassword("g"));
					} catch (TException e) {
						e.printStackTrace();
					}
					
				}
			}.start();
		}
		
		TManageService.AsyncIface service2 = ThriftUtil.createClient(TManageService.AsyncIface.class, timeout,ThriftProtocolEnum.BINARY,zkClient);
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
		service2.getUserById(1, new AsyncMethodCallback<TUser>() {
			@Override
			public void onComplete(TUser response) {
			}
			@Override
			public void onError(Exception exception) {
			}
		});
	}
	
}
