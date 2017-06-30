package sc.learn.manage.service;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import sc.learn.common.spring.annotation.ThriftService;
import sc.learn.manage.model.TUser;

@ThriftService
public class ManageService implements TManageService.Iface,TManageService.AsyncIface{

	@Override
	public void saveUser(TUser user, AsyncMethodCallback<Void> resultHandler) throws TException {
		System.out.println("save");
	}

	@Override
	public void saveUser(TUser user) throws TException {
		System.out.println("save");
	}

}
