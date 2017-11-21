package sc.learn.manage.service;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import sc.learn.common.spring.annotation.ThriftService;
import sc.learn.manage.model.TUser;

//@ThriftService
public class ManageService implements TManageService.Iface,TManageService.AsyncIface{

	@Override
	public void saveUser(TUser user, AsyncMethodCallback<Void> resultHandler) throws TException {
		System.out.println("save1");
	}

	@Override
	public void saveUser(TUser user) throws TException {
		System.out.println("save2");
	}

	@Override
	public void getUserById(int id, AsyncMethodCallback<TUser> resultHandler) throws TException {
		resultHandler.onComplete(new TUser().setId(1));
	}

	@Override
	public TUser getUserById(int id) throws TException {
		return new TUser().setId(2).setName("sc2").setEmail("em2").setPassword("p2");
	}

}
