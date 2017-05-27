package sc.learn.manage.mapper;

import java.util.List;

import sc.learn.manage.po.Privilege;

public interface PrivilegeMapper {
	Integer insert(Privilege privilege);
	
	Privilege selectById(Integer id);
	
	List<Privilege> selectAll();

	List<Privilege> selectMenuPrivileges();
	
	List<Privilege> selectByParentId(Integer parentId);

	List<Privilege> selectByIds(Integer[] ids);
	
	Privilege selectByUrl(String url);

	List<String> selectAllUrl();
}
