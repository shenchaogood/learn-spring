package sc.learn.common.util;

import java.io.IOException;
import java.util.Properties;


public abstract class ZkConfig {
	public static final String PROFILE_KEY="profile";
	public static final String SESSTION_TIMEOUT_KEY="zk_session_timeout";
	public static final String CONNECTION_TIMEOUT_KEY="zk_connection_timeout";
	private static final Properties CONFIG_MAP=new Properties();
	static{
		try {
			CONFIG_MAP.load(ZkConfig.class.getResourceAsStream("/config/zookeeper.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public final static String ZK_SERVER = CONFIG_MAP.getProperty("zkserver", "127.0.0.1:2181");
	public final static String PROFILE =CONFIG_MAP.getProperty(PROFILE_KEY);
	public final static int ZK_SESSION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(SESSTION_TIMEOUT_KEY,"30000"));
	public final static int ZK_CONNECTION_TIMEOUT=Integer.parseInt(CONFIG_MAP.getProperty(CONNECTION_TIMEOUT_KEY,"30000"));
}
