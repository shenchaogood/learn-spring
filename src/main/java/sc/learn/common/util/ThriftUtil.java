package sc.learn.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ThriftUtil {
	
	private static final Map<EnvironmentType,ZookeeperClient> ENV_CLIENT_MAP=new HashMap<>();
	
	public static void startThriftServer(Object thriftServiceObj) throws IOException{
		
		EnvironmentType env=EnvironmentUtil.getLocalEnviromentType();
		ZookeeperClient zkClient=ENV_CLIENT_MAP.get(env);
		if(zkClient==null){
			zkClient=new ZookeeperClient();
			ENV_CLIENT_MAP.put(env, zkClient);
		}
	}

}
