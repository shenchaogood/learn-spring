package sc.learn.manage.mapper;

import java.util.List;

import sc.learn.manage.po.Role;


public interface RoleMapper {
	Integer insert(Role role);
	
	void deleteById(Integer id);
	
	void deleteRolePrivileges(Integer roleId);
	
	void deleteRoleUsers(Integer roleId);
	
	void update(Role role);
	
	Role selectById(Integer id);
	
	void insertRolePrivileges(Role role);
	
	List<Role> selectAll();

	Role selectByName(String name);
}
