package sc.learn.common.spring;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

import sc.learn.common.spring.annotation.ThriftService;
import sc.learn.common.thrift.ThriftProtocolEnum;
import sc.learn.common.util.ThriftUtil;

public class ThriftServicePostProcessor implements BeanPostProcessor,BeanFactoryAware{

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private BeanFactory beanFactory;
	private ThriftProtocolEnum protocol;
	private CuratorFramework zkClient;
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(protocol==null){
			protocol=ThriftProtocolEnum.BINARY;
		}
		if(zkClient==null){
			zkClient=beanFactory.getBean(CuratorFramework.class);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> cls =  bean.getClass();
        if (cls.isAnnotationPresent(ThriftService.class)) {
            ThriftUtil.startThriftServer(bean,protocol,zkClient);
            LOGGER.debug("启动Thrift服务:" + bean.getClass().getName());
        }
		return bean;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=beanFactory;
	}

}
