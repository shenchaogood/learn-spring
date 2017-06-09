package sc.learn.test.mapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class TestMybatis {
	
	@org.junit.Test
	public void testMybatis(){
		SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(TestMybatis.class.getResourceAsStream("/SqlMapConfig.xml"));
		SqlSession session=factory.openSession();
		session.getMapper(TestMapper.class).findAll();
	}

}