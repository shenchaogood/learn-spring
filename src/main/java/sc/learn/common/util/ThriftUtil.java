package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class ThriftUtil {
	
	private static final Map<EnvironmentType,ZookeeperClient> ENV_CLIENT_MAP=new HashMap<>();
	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);
	
	private static interface Constants {
		static final String SERVICE_PREFIX=ZkConfig.SERVICE_PREFIX;;
		static final String IFACE_SUFFIX = "$Iface";
		static final String CLIENT_SUFFIX = "$Client";
		static final String PROCESSOR_SUFFIX = "$Processor";

    }
	
	
	public static void startThriftServer(Object thriftServiceObj) {
		
		EnvironmentType env=EnvironmentUtil.getLocalEnviromentType();
		ZookeeperClient zkClient=ENV_CLIENT_MAP.get(env);
		if(zkClient==null){
			try {
				zkClient=new ZookeeperClient();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ENV_CLIENT_MAP.put(env, zkClient);
		}
		
		for(Class<?> inter:thriftServiceObj.getClass().getInterfaces()){
			String interfaceName = inter.getName();
            if (interfaceName.endsWith(ThriftUtil.Constants.IFACE_SUFFIX)) {
            	String serviceName = StringUtils.removeEnd(interfaceName, ThriftUtil.Constants.IFACE_SUFFIX);
            	String bindIp=ZkConfig.getServiceIp(serviceName);
            	int bindPort=ZkConfig.getServicePort(serviceName);
            	String path=Constants.SERVICE_PREFIX+serviceName+"/"+bindIp+":"+bindPort;
            	zkClient.createPath(path, "".getBytes(), ZookeeperClient.EPHEMERAL);
				try {
					Class<?> processorClass = Class.forName(serviceName + ThriftUtil.Constants.PROCESSOR_SUFFIX);
					Class<?> ifaceClass = Class.forName(serviceName + ThriftUtil.Constants.IFACE_SUFFIX);
					@SuppressWarnings("unchecked")
					Constructor<TProcessor> ctor = (Constructor<TProcessor>) processorClass.getConstructor(ifaceClass);
					TProcessor processor = ctor.newInstance(thriftServiceObj);
					TServerTransport serverTransport = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
					TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
					server.serve();
					final ZookeeperClient zkcli=zkClient;
					Runtime.getRuntime().addShutdownHook(new Thread(()->{
						zkcli.deletePath(path);
					}));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
            }
		}
	}

}
