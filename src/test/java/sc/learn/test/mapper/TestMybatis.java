package sc.learn.test.mapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import sc.learn.manage.mapper.UserMapper;
import sc.learn.manage.po.UserExample;

public class TestMybatis {
	
	@org.junit.Test
	public void testMybatis(){
		SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(TestMybatis.class.getResourceAsStream("/config/SqlMapConfig.xml"));
		SqlSession session=factory.openSession();
		session.getMapper(UserMapper.class).countByExample(new UserExample());
	}

}
