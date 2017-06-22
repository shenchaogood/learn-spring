package sc.learn.manage.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sc.learn.common.pojo.DataTableParam;
import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.StringUtil;
import sc.learn.common.util.security.Coder;
import sc.learn.manage.mapper.UserMapper;
import sc.learn.manage.po.User;
import sc.learn.manage.po.UserExample;
import sc.learn.manage.util.Contants;
import sc.learn.manage.vo.UserVo;

@Service
public class UserBiz {
	
	@Autowired
	private UserMapper userMapper;
	
	public User login(UserVo user){
		UserExample example=new UserExample();
		example.createCriteria().andEmailEqualTo(user.getEmail()).andPasswordEqualTo(Coder.encryptMD5(user.getPassword()));
		return userMapper.selectByExample(example).get(0);
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

	public DataTableResult<User> list(DataTableParam param) {
		PageHelper.offsetPage(param.getStart(), param.getLength());
		UserExample example=new UserExample();
		long recordsTotal=userMapper.countByExample(example);
		//TODO example.createCriteria().
		List<User> data=userMapper.selectByExample(example);
		PageInfo<User> pageInfo = new PageInfo<User>(data);
        long recordsFiltered = pageInfo.getTotal();
        
		return DataTableResult.createDataTableResult(param.getDraw(), recordsTotal, recordsFiltered, data);
	}
}
