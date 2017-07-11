package sc.learn.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import sc.learn.manage.po.Privilege;
import sc.learn.manage.po.PrivilegeExample;

public interface PrivilegeMapper {
    long countByExample(PrivilegeExample example);

    int deleteByExample(PrivilegeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Privilege record);

    int insertSelective(Privilege record);

    List<Privilege> selectByExample(PrivilegeExample example);

    Privilege selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Privilege record, @Param("example") PrivilegeExample example);

    int updateByExample(@Param("record") Privilege record, @Param("example") PrivilegeExample example);

    int updateByPrimaryKeySelective(Privilege record);

    int updateByPrimaryKey(Privilege record);
}