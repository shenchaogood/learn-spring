package sc.learn.test.config;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sc.learn.config.RootConfig;
import sc.learn.test.mapper.TestMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RootConfig.class)
@ActiveProfiles("DEV")
public class TestSpringConfig {
	protected final Logger log=LoggerFactory.getLogger(getClass());
	
	
	@Autowired
	protected TestMapper testMapper;
	
	
	@Test
	public void testStartup(){
		List<sc.learn.test.mapper.Test> tests=testMapper.findAll();
		log.debug("testStartup:{}", tests);
	}

}
