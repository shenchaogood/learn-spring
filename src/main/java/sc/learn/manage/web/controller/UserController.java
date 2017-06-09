package sc.learn.manage.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
			return ResponseResult.FAIL.setError(Contants.LOGIN_USER_NO_EXISTS);
		}else{
			httpSession.setAttibute(Contants.CURRENT_LOGIN_USER, user);
			return ResponseResult.SUCCESS;
		}
	}
	
	@RequestMapping("add")
	public ResponseResult add(User user){
		if(userBiz.add(user)==0){
			return 	ResponseResult.FAIL.setError(Contants.RECORD_EXISTS);
		}else{
			return 	ResponseResult.SUCCESS;
		}
	}
	
	
}
