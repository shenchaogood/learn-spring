package sc.learn.common.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import sc.learn.common.spring.annotation.ThriftService;
import sc.learn.common.util.ThriftUtil;

@Component
public class ThriftServicePostProcessor implements BeanPostProcessor {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> cls =  bean.getClass();
        if (cls.isAnnotationPresent(ThriftService.class)) {
            ThriftUtil.startThriftServer(bean);
            LOGGER.debug("启动Thrift服务:" + bean);
        }
		return bean;
	}

}
