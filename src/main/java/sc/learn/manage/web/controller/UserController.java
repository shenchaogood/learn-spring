package sc.learn.manage.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.web.HttpSessionProvider;
import sc.learn.manage.biz.UserBiz;
import sc.learn.manage.po.User;
import sc.learn.manage.util.Contants;
import sc.learn.manage.vo.UserVo;

@RestController
@RequestMapping("/manage/user")
public class UserController {
	
	@Autowired
	private HttpSessionProvider httpSession;
	@Autowired
	private UserBiz userBiz;
	
	
	@RequestMapping("login")
	public ResponseResult login(UserVo userVo){
		User user=userBiz.login(userVo);
		if(user==null){
			return ResponseResult.createFail(Contants.LOGIN_USER_NO_EXISTS);
		}else{
			httpSession.setAttibute(Contants.CURRENT_LOGIN_USER, user);
			return ResponseResult.createSuccess();
		}
	}
	
	@RequestMapping("list")
	public DataTableResult<UserVo> list(){
		userBiz.list();
		return null;
	}
	
	@RequestMapping("add")
	public ResponseResult add(User user){
		return userBiz.add(user);
	}
	
	
}
