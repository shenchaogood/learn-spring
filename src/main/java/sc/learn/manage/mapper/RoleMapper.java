package sc.learn.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import sc.learn.manage.po.Role;
import sc.learn.manage.po.RoleExample;

public interface RoleMapper {
    long countByExample(RoleExample example);

    int deleteByExample(RoleExample example);

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByExample(RoleExample example);

    int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleExample example);

    int updateByExample(@Param("record") Role record, @Param("example") RoleExample example);
}