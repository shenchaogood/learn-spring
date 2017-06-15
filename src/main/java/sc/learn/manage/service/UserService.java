package sc.learn.manage.service;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import sc.learn.common.spring.annotation.ThriftService;

@ThriftService
public class UserService implements TUserService.Iface,TUserService.AsyncIface{

	@Override
	public void save(TUser user, AsyncMethodCallback<Void> resultHandler) throws TException {
		System.out.println("save");
	}

	@Override
	public void save(TUser user) throws TException {
		System.out.println("save");
	}

}
