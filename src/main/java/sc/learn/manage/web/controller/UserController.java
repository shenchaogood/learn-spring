package sc.learn.manage.web.controller;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sc.learn.common.pojo.DataTableParam;
import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.web.HttpSessionProvider;
import sc.learn.manage.biz.PrivilegeBiz;
import sc.learn.manage.biz.UserBiz;
import sc.learn.manage.po.Privilege;
import sc.learn.manage.po.User;
import sc.learn.manage.util.Constants;
import sc.learn.manage.vo.UserVo;

//@RestController
@RequestMapping("/manage/user")
public class UserController {
	
	private static Logger LOGGER=LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private HttpSessionProvider httpSession;
	@Autowired
	private UserBiz userBiz;
	@Autowired
	private PrivilegeBiz privilegeBiz;

	@RequestMapping("login")
	public ResponseResult login(UserVo userVo){
		User user=userBiz.login(userVo);
		if(user==null){
			return ResponseResult.createFail(Constants.LOGIN_USER_NO_EXISTS);
		}else{
			DataTableResult<Privilege> result=privilegeBiz.findPrivilgesByUser(user);
			userVo=new UserVo();
			try {
				userVo.setPrivileges(result.getData());
				BeanUtils.copyProperties(userVo, user);
				httpSession.setAttibute(Constants.CURRENT_LOGIN_USER, userVo);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return ResponseResult.createSuccess();
		}
	}
	
	@RequestMapping("list")
	public DataTableResult<User> list(@RequestBody DataTableParam param){
		return userBiz.list(param);
	}
	
	@RequestMapping("add")
	public ResponseResult add(User user){
		return userBiz.add(user);
	}
	
	@RequestMapping("ex")
	public ResponseResult ex() throws Exception{
		LOGGER.debug("故意的");
		Thread.sleep(5000);
		throw new RuntimeException("abc");
	}
	
	
}
