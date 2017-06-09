package sc.learn.manage.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.learn.common.util.security.Coder;
import sc.learn.manage.mapper.UserMapper;
import sc.learn.manage.po.User;
import sc.learn.manage.vo.UserVo;

@Service
public class UserBiz {
	
	@Autowired
	private UserMapper userMapper;
	
	public User login(UserVo user){
		return userMapper.selectByEmailPassword(user.getEmail(), Coder.encryptMD5(user.getPassword()));
	}

	public int add(User user) {
		return userMapper.insert(user);
	}
	
}
