package sc.learn.common.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.util.net.NetToolUtil;
import sc.learn.config.RootConfig;


public abstract class ZkConfig {
	private static final Logger LOGGER=LoggerFactory.getLogger(RootConfig.class);
	
	private static final String ZK_PROFILE_KEY="zk_profile";
	private static final String ZK_SERVER_KEY="zk_server";
	private static final String ZK_SESSTION_TIMEOUT_KEY="zk_session_timeout";
	private static final String ZK_CONNECTION_TIMEOUT_KEY="zk_connection_timeout";
	private static final String ZK_MODULE_NAME_KEY="zk_module_name";
	private static final String ZK_SERVICE_PREFIX_KEY="zk_service_prefix";
	private static final String ZK_SERVICE_DEFAULT_IP_KEY="zk_service_default_ip";
	private static final String ZK_SERVICE_DEFAULT_PORT_KEY="zk_service_default_port";
	private static final String ZK_AUTH_KEY="zk_auth";
	private static final String ZK_ENVIRONMENT_KEY="zk_environment";
	private static final Properties CONFIG_MAP=new Properties();
	static{
		try {
			CONFIG_MAP.load(ZkConfig.class.getResourceAsStream("/config/zookeeper.properties"));
		} catch (IOException e) {
			LOGGER.error(ExceptionUtil.getStackTrace(e));
		}
	}
	
	
	public final static String ZK_SERVER = CONFIG_MAP.getProperty(ZK_SERVER_KEY,System.getProperty(ZK_SERVER_KEY, System.getenv(ZK_SERVER_KEY)));
	public final static String ZK_PROFILE =CONFIG_MAP.getProperty(ZK_PROFILE_KEY,System.getProperty(ZK_PROFILE_KEY, System.getenv(ZK_PROFILE_KEY)));
	public final static int ZK_SESSION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(ZK_SESSTION_TIMEOUT_KEY,System.getProperty(ZK_SESSTION_TIMEOUT_KEY, System.getenv(ZK_SESSTION_TIMEOUT_KEY))));
	public final static int ZK_CONNECTION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(ZK_CONNECTION_TIMEOUT_KEY,System.getProperty(ZK_CONNECTION_TIMEOUT_KEY, System.getenv(ZK_CONNECTION_TIMEOUT_KEY))));
	public static final String ZK_MODULE_NAME = CONFIG_MAP.getProperty(ZK_MODULE_NAME_KEY,System.getProperty(ZK_MODULE_NAME_KEY, System.getenv(ZK_MODULE_NAME_KEY)));
	public static final String ZK_SERVICE_PREFIX = CONFIG_MAP.getProperty(ZK_SERVICE_PREFIX_KEY,System.getProperty(ZK_SERVICE_PREFIX_KEY, System.getenv(ZK_SERVICE_PREFIX_KEY)));
	public static final String ZK_SERVICE_DEFAULT_IP = CONFIG_MAP.getProperty(ZK_SERVICE_DEFAULT_IP_KEY,System.getProperty(ZK_SERVICE_DEFAULT_IP_KEY, System.getenv(ZK_SERVICE_DEFAULT_IP_KEY)));
	public static final String ZK_SERVICE_DEFAULT_PORT = CONFIG_MAP.getProperty(ZK_SERVICE_DEFAULT_PORT_KEY,System.getProperty(ZK_SERVICE_DEFAULT_PORT_KEY, System.getenv(ZK_SERVICE_DEFAULT_PORT_KEY)));
	public static final boolean ZK_AUTH = BooleanUtils.toBoolean(CONFIG_MAP.getProperty(ZK_AUTH_KEY,System.getProperty(ZK_AUTH_KEY, System.getenv(ZK_AUTH_KEY))));
	public static final String ZK_ENVIRONMENT = CONFIG_MAP.getProperty(ZK_ENVIRONMENT_KEY,System.getProperty(ZK_ENVIRONMENT_KEY, System.getenv(ZK_ENVIRONMENT_KEY)));
	
	public static final String getServiceIp(String serviceName){
		String key="zk_ip_"+serviceName;
		String ip=CONFIG_MAP.getProperty(key,System.getProperty(key, System.getenv(key)));
		if(StringUtils.isBlank(ip)){
			ip=ZK_SERVICE_DEFAULT_IP;
		}
		if(StringUtils.isBlank(ip)){
			ip=NetToolUtil.getLocalIP();
		}
		return ip;
	}
	
	public static final int getServicePort(String serviceName){
		String key="zk_port_"+serviceName;
		String port=CONFIG_MAP.getProperty(key,System.getProperty(key, System.getenv(key)));
		if(StringUtils.isBlank(port)){
			port=ZK_SERVICE_DEFAULT_PORT;
		}
		return Integer.parseInt(port);
	}
	
}
