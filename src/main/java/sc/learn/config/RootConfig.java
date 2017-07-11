package sc.learn.config;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.curator.framework.CuratorFramework;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.alibaba.druid.pool.DruidDataSource;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import sc.learn.common.spring.BizParamPrintAspect;
import sc.learn.common.spring.ThriftServicePostProcessor;
import sc.learn.common.spring.ZookeeperFactory;
import sc.learn.common.thrift.AddressProvider;
import sc.learn.common.thrift.ThriftAsyncIfaceTransportPool;
import sc.learn.common.util.ExceptionUtil;
import sc.learn.common.util.StringUtil;
import sc.learn.common.util.ThriftUtil;
import sc.learn.common.util.ZkConfig;
import sc.learn.common.web.ClusterHttpSessionProvider;
import sc.learn.common.web.HttpSessionProvider;
import sc.learn.common.web.ServletHttpSessionProvider;
import sc.learn.manage.service.TManageService;

@Lazy(false)
@Configuration
@EnableAspectJAutoProxy
@PropertySources({
		@PropertySource("classpath:config/db.properties"),
		@PropertySource("classpath:config/redis.properties"),
		@PropertySource("classpath:config/mail.properties"),
		@PropertySource("classpath:config/zookeeper.properties")
	})
@ComponentScan(basePackages="sc.learn",excludeFilters={
		@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class),
		@Filter(type=FilterType.ANNOTATION,value=Controller.class)
		})
public class RootConfig implements EnvironmentAware {
	private static final Logger LOGGER=LoggerFactory.getLogger(RootConfig.class);
	private Environment env;
	@Override
	public void setEnvironment(Environment environment) {
		env=environment;
		try {
			for(Field field:ZkConfig.class.getDeclaredFields()){
				if(field.getName().endsWith("_KEY")||field.getName().endsWith("CONFIG_MAP")){
					continue;
				}
				field.setAccessible(true);//将字段的访问权限设为true：即去除private修饰符的影响  
				Field modifiers =field.getClass().getDeclaredField("modifiers");//去除final修饰符的影响，将字段设为可修改的
				modifiers.setAccessible(true);  
			    modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);//fianl标志位置0
			    field.set(null,ConvertUtils.convert(env.getProperty(StringUtil.lowerCase(field.getName())), field.getType()));  
			}
		} catch(Exception e) {
			LOGGER.error(ExceptionUtil.getStackTrace(e));
		}  
	}
	
	@Bean(initMethod="init",destroyMethod="close")
	public DataSource dataSource() throws SQLException{
		DruidDataSource dataSource=new DruidDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driver"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.username"));
		dataSource.setPassword(env.getProperty("jdbc.password"));
		dataSource.setMaxActive(env.getProperty("jdbc.maxActive", int.class));
		dataSource.setFilters("stat");
		dataSource.setValidationQuery("select 1");
		dataSource.setTestWhileIdle(true);
		dataSource.setTimeBetweenEvictionRunsMillis(60);
		return dataSource;
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource){
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
		SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("config/SqlMapConfig.xml"));
		return sqlSessionFactoryBean;
	}
	
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(){
		MapperScannerConfigurer mapperScannerConfigurer=new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		mapperScannerConfigurer.setBasePackage("sc.learn.*.mapper");
		return mapperScannerConfigurer;
	}
	
	@Bean
	public JedisCluster jedisCluster(){
		Set<HostAndPort> nodes=new HashSet<>();
		for(String hp:env.getProperty("redis.server").split(",")){
			nodes.add(new HostAndPort(hp.split(":")[0], Integer.parseInt(hp.split(":")[1])));
		}
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxIdle(20);
		return new JedisCluster(nodes, config);
	}
	
	@Profile("DEV")
	@Bean
	public HttpSessionProvider servletHttpSessionProvider(){
		return new ServletHttpSessionProvider();
	}
	
	
	@Profile({"PRODUCTION","TEST"})
	@Bean
	public HttpSessionProvider clusterHttpSessionProvider(JedisCluster jedisCluster){
		return new ClusterHttpSessionProvider(jedisCluster);
	}
	
	@Bean
	public JavaMailSender javaMailSender(){
		JavaMailSenderImpl mailSender=new JavaMailSenderImpl();
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(env.getProperty("mail.host"));
		mailSender.setUsername(env.getProperty("mail.username"));
		mailSender.setPassword(env.getProperty("mail.password"));
		mailSender.setPort(env.getProperty("mail.smtp.port",int.class));
		
		Properties mailProperties=new Properties();
		mailProperties.setProperty("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
		mailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		mailProperties.setProperty("mail.smtp.socketFactory.fallback", "false");
		mailProperties.setProperty("mail.smtp.socketFactory.port", env.getProperty("mail.smtp.port"));
		mailProperties.setProperty("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
		mailProperties.setProperty("mail.smtp.timeout", env.getProperty("mail.smtp.timeout"));
		mailProperties.setProperty("mail.smtp.port", env.getProperty("mail.smtp.port"));
		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}
	
	@Bean
	public BizParamPrintAspect bizParamPrintAspect(){
		return new BizParamPrintAspect();
	}
	
	@Bean
	public ThriftServicePostProcessor ThriftServicePostProcessor(){
		return new ThriftServicePostProcessor();
	}
	
	@Bean
	public ZookeeperFactory zookeeperFactory(){
		ZookeeperFactory zookeeperFactory=new ZookeeperFactory();
		zookeeperFactory.setZookeeperHosts(ZkConfig.ZK_SERVER);
		zookeeperFactory.setNamespace("");
		zookeeperFactory.setConnectionTimeout(ZkConfig.ZK_CONNECTION_TIMEOUT);
		zookeeperFactory.setSessionTimeout(ZkConfig.ZK_SESSION_TIMEOUT);
		return zookeeperFactory;
	}
	
	@Bean
	public ThriftAsyncIfaceTransportPool thriftAsyncIfaceTransportPool(CuratorFramework zkClient){
		Triple<Boolean,String,String> triple=ThriftUtil.fetchSynchronizedAndIfacePathAndServiceName(TManageService.Iface.class);
		AddressProvider addressProvider=new AddressProvider("", zkClient, triple.getMiddle());
		return new ThriftAsyncIfaceTransportPool(addressProvider, timeout, maxActive, maxIdle, minIdle, maxWait);
	}
	
//	@Bean
//	public TUserService.Iface userServiceIface() throws IOException{
//		return ThriftUtil.getIfaceClient(TUserService.Iface.class, 3000);
//	}
//	
//	@Bean
//	public TUserService.AsyncIface userServiceAsyncIface() throws IOException{
//		return ThriftUtil.getIfaceClient(TUserService.AsyncIface.class, 3000);
//	}
	
	
	
}
