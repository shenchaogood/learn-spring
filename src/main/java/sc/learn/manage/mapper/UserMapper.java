package sc.learn.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.learn.manage.po.User;

public interface UserMapper {
	Integer insert(User user);
	
	void deleteById(Integer id);
	
	void deleteUserRoles(Integer userId);
	
	void update(User user);
	
	User selectById(Integer id);
	
	User selectByNamePassword(@Param("name") String name,@Param("password") String password);
	
	void insertUserRoles(User user);
	
	List<User> selectAll();

	int selectCount();
}
