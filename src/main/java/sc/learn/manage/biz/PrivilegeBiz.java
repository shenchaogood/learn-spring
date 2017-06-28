package sc.learn.manage.biz;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.learn.common.biz.BaseBiz;
import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.manage.mapper.PrivilegeMapper;
import sc.learn.manage.mapper.RolePrivilegeMapper;
import sc.learn.manage.mapper.UserRoleMapper;
import sc.learn.manage.po.Privilege;
import sc.learn.manage.po.PrivilegeExample;
import sc.learn.manage.po.RolePrivilege;
import sc.learn.manage.po.RolePrivilegeExample;
import sc.learn.manage.po.User;
import sc.learn.manage.po.UserRole;
import sc.learn.manage.po.UserRoleExample;

@Service
public class PrivilegeBiz extends BaseBiz<Privilege,PrivilegeMapper>{
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RolePrivilegeMapper rolePrivilegeMapper;
	@Autowired
	public PrivilegeBiz(PrivilegeMapper mapper) throws ClassNotFoundException {
		super(mapper);
	}

	public ResponseResult checkAddParam(Privilege privilege){
		if(privilege==null){
			return ResponseResult.createFail("参数为空");
		}else{
			return ResponseResult.createSuccess();
		}
	}

	public DataTableResult<Privilege> findPrivilgesByUser(User user) {
		UserRoleExample userRoleExample=new UserRoleExample();
		userRoleExample.createCriteria().andUserIdEqualTo(user.getId());
		List<UserRole> userRoles=userRoleMapper.selectByExample(userRoleExample);
		PrivilegeExample privilegeExample=new PrivilegeExample();
		List<Privilege> privileges;
		if(userRoles.stream().filter((userRole)->userRole.getRoleId()==1).count()>0){
			//具有管理员角色具有所有权限
			privileges=mapper.selectByExample(privilegeExample);
		}else{
			List<Integer> roleIds=userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
			RolePrivilegeExample rolePrivilegeExample=new RolePrivilegeExample();
			rolePrivilegeExample.createCriteria().andPrivilegeIdIn(roleIds);
			List<RolePrivilege> rolePrivileges=rolePrivilegeMapper.selectByExample(rolePrivilegeExample);
			List<Integer> privilegeIds=rolePrivileges.stream().map(RolePrivilege::getPrivilegeId).distinct().collect(Collectors.toList());
			privilegeExample.createCriteria().andParentIdIn(privilegeIds);
			privileges=mapper.selectByExample(privilegeExample);
		}
		return DataTableResult.createDataTableResult(1, privileges.size(), privileges.size(), privileges, "");
	}
	
}
