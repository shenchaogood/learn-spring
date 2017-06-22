package sc.learn.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.learn.common.mapper.BaseMapper;
import sc.learn.manage.po.User;

public interface UserMapper extends BaseMapper<User>{
	
	void deleteById(Integer id);
	
	void deleteUserRoles(Integer userId);
	
	User selectById(Integer id);
	
	User selectByEmailPassword(@Param("email") String email,@Param("password") String password);
	
	int insertUserRoles(User user);
	
	List<User> selectAll();
}
