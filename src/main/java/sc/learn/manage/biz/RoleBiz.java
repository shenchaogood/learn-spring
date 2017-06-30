package sc.learn.manage.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.learn.common.biz.BaseBiz;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.manage.mapper.RoleMapper;
import sc.learn.manage.po.Role;

@Service
public class RoleBiz extends BaseBiz<Role,RoleMapper>{
	
	@Autowired
	public RoleBiz(RoleMapper mapper) throws ClassNotFoundException {
		super(mapper);
	}

	protected ResponseResult checkAddParam(Role role){
		if(role==null){
			return ResponseResult.createFail("参数为空");
		}else{
			return ResponseResult.createSuccess();
		}
	}
	
}
