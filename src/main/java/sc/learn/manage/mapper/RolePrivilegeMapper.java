package sc.learn.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import sc.learn.manage.po.RolePrivilege;
import sc.learn.manage.po.RolePrivilegeExample;
import sc.learn.manage.po.RolePrivilegeKey;

public interface RolePrivilegeMapper {
    long countByExample(RolePrivilegeExample example);

    int deleteByExample(RolePrivilegeExample example);

    int deleteByPrimaryKey(RolePrivilegeKey key);

    int insert(RolePrivilege record);

    int insertSelective(RolePrivilege record);

    List<RolePrivilege> selectByExample(RolePrivilegeExample example);

    RolePrivilege selectByPrimaryKey(RolePrivilegeKey key);

    int updateByExampleSelective(@Param("record") RolePrivilege record, @Param("example") RolePrivilegeExample example);

    int updateByExample(@Param("record") RolePrivilege record, @Param("example") RolePrivilegeExample example);

    int updateByPrimaryKeySelective(RolePrivilege record);

    int updateByPrimaryKey(RolePrivilege record);
}