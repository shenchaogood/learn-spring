package sc.learn.common.spring;

import java.io.Closeable;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import sc.learn.common.util.StringUtil;

public class ZookeeperFactory implements FactoryBean<CuratorFramework>, Closeable, InitializingBean {
	private static Logger LOGGER = LoggerFactory.getLogger(ZookeeperFactory.class);

	/**
	 * zookeeper集群地址
	 */
	private String zookeeperHosts;

	// session超时
	private int sessionTimeout = 3000;
	private int connectionTimeout = 3000;

	private CuratorFramework zkClient;

	// 第三方未提供，所以暂时用不到
	private String namespace;

	public void setZookeeperHosts(String zookeeperHosts) {
		this.zookeeperHosts = zookeeperHosts;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public CuratorFramework getObject() throws Exception {
		if(zkClient==null){
			afterPropertiesSet();
		}
		return zkClient;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtil.isBlank(this.zookeeperHosts)) {
			LOGGER.warn("zookeeperHosts为空！");
			return;
		}
		zkClient = create(zookeeperHosts, sessionTimeout, connectionTimeout, namespace);
		zkClient.start();
	}

	public static CuratorFramework create(String connectString, int sessionTimeout, int connectionTimeout, String namespace) {
		return CuratorFrameworkFactory.builder().connectString(connectString).sessionTimeoutMs(sessionTimeout)
				.connectionTimeoutMs(connectionTimeout).canBeReadOnly(true).namespace(namespace)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE)).defaultData(null).build();
		
	}

	public void close() {
		if (zkClient != null) {
			zkClient.close();
		}
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}