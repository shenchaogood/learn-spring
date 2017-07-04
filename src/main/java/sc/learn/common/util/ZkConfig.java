package sc.learn.common.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;


public abstract class ZkConfig {
	public static final String PROFILE_KEY="zk_profile";
	public static final String ZKSERVER_KEY="zk_server";
	public static final String SESSTION_TIMEOUT_KEY="zk_session_timeout";
	public static final String CONNECTION_TIMEOUT_KEY="zk_connection_timeout";
	public static final String MODULE_NAME_KEY="zk_module_name";
	public static final String SERVICE_PREFIX_KEY="zk_services_prefix";
	public static final String SERVICE_DEFAULT_IP_KEY="zk_ip_default";
	public static final String SERVICE_DEFAULT_PORT_KEY="zk_port_default";
	public static final String ZK_AUTH_KEY="zk_auth";
	private static final Properties CONFIG_MAP=new Properties();
	static{
		try {
			CONFIG_MAP.load(ZkConfig.class.getResourceAsStream("/config/zookeeper.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public final static String ZK_SERVER = CONFIG_MAP.getProperty(ZKSERVER_KEY,System.getProperty(ZKSERVER_KEY, System.getenv(ZKSERVER_KEY)));
	public final static String PROFILE =CONFIG_MAP.getProperty(PROFILE_KEY,System.getProperty(PROFILE_KEY, System.getenv(PROFILE_KEY)));
	public final static int ZK_SESSION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(SESSTION_TIMEOUT_KEY,System.getProperty(SESSTION_TIMEOUT_KEY, System.getenv(SESSTION_TIMEOUT_KEY))));
	public final static int ZK_CONNECTION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(CONNECTION_TIMEOUT_KEY,System.getProperty(CONNECTION_TIMEOUT_KEY, System.getenv(CONNECTION_TIMEOUT_KEY))));
	public static final String MODULE_NAME = CONFIG_MAP.getProperty(MODULE_NAME_KEY,System.getProperty(MODULE_NAME_KEY, System.getenv(MODULE_NAME_KEY)));
	public static final String SERVICE_PREFIX = CONFIG_MAP.getProperty(SERVICE_PREFIX_KEY,System.getProperty(SERVICE_PREFIX_KEY, System.getenv(SERVICE_PREFIX_KEY)));
	public static final String SERVICE_DEFAULT_IP = CONFIG_MAP.getProperty(SERVICE_DEFAULT_IP_KEY,System.getProperty(SERVICE_DEFAULT_IP_KEY, System.getenv(SERVICE_DEFAULT_IP_KEY)));
	public static final String SERVICE_DEFAULT_PORT = CONFIG_MAP.getProperty(SERVICE_DEFAULT_PORT_KEY,System.getProperty(SERVICE_DEFAULT_PORT_KEY, System.getenv(SERVICE_DEFAULT_PORT_KEY)));
	public static final boolean ZK_AUTH = BooleanUtils.toBoolean(CONFIG_MAP.getProperty(ZK_AUTH_KEY,System.getProperty(ZK_AUTH_KEY, System.getenv(ZK_AUTH_KEY))));
	public static final String getServiceIp(String serviceName){
		String key="ip_"+serviceName;
		String ip=CONFIG_MAP.getProperty(key,System.getProperty(key, System.getenv(key)));
		if(StringUtils.isBlank(ip)){
			ip=SERVICE_DEFAULT_IP;
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
			port=SERVICE_DEFAULT_PORT;
		}
		return Integer.parseInt(port);
	}
	
}
