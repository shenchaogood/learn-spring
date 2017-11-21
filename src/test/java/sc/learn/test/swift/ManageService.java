package sc.learn.test.swift;

import org.apache.thrift.TException;

import sc.learn.thrift.swift.TManageService;
import sc.learn.thrift.swift.TUser;

public class ManageService implements TManageService {

	@Override
	public void saveUser(TUser user) throws TException {
		System.out.println("swift saveUser");

	}

	@Override
	public TUser getUserById(int id) throws TException {
		System.out.println("swift getUserById");
		return new TUser();
	}

}
