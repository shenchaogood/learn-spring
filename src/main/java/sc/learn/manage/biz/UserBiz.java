package sc.learn.manage.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.StringUtil;
import sc.learn.common.util.security.Coder;
import sc.learn.manage.mapper.UserMapper;
import sc.learn.manage.po.User;
import sc.learn.manage.util.Contants;
import sc.learn.manage.vo.UserVo;

@Service
public class UserBiz {
	
	@Autowired
	private UserMapper userMapper;
	
	public User login(UserVo user){
		return userMapper.selectByEmailPassword(user.getEmail(), Coder.encryptMD5(user.getPassword()));
	}

	public ResponseResult check(User user){
		if(StringUtil.isBlank(user.getEmail())){
			return ResponseResult.createFail("邮箱不能为空");
		}else if(StringUtil.isBlank(user.getName())){
			return ResponseResult.createFail("用户名不能为空");
		}else if(StringUtil.isBlank(user.getPassword())){
			return ResponseResult.createFail("密码不能为空");
		}else{
			return ResponseResult.createSuccess();
		}
	}
	
	public ResponseResult add(User user) {
		ResponseResult ret=check(user);
		if(!ret.isSuccess()){
			return ret;
		}
		if(userMapper.insert(user)>0){
			return ResponseResult.createSuccess();
		}else{
			return ResponseResult.createFail(Contants.RECORD_EXISTS);
		}
	}
}
