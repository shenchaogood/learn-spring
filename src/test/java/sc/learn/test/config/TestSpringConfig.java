package sc.learn.test.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sc.learn.common.web.HttpSessionProvider;
import sc.learn.config.RootConfig;
import sc.learn.manage.biz.UserBiz;
import sc.learn.manage.model.TUser;
import sc.learn.manage.service.TManageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RootConfig.class)
@ActiveProfiles("DEV")
public class TestSpringConfig {
	protected final Logger log=LoggerFactory.getLogger(getClass());
	
	
	@Autowired
	private UserBiz userBiz;
	
	@Autowired
	private TManageService.Iface manageIface;
	
	@Autowired
	private HttpSessionProvider httpSession;
	
	@Test
	public void testStartup(){
		userBiz.checkAddParam(null);
		log.debug("{}",httpSession);
	}
	
	@Test
	public void testIface() throws Exception{
		Thread.sleep(3000);
		TUser user=manageIface.getUserById(1);
		System.out.println(user);
	}

}
