package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TProcessor;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThriftUtil {

	private static final Map<EnvironmentType, ZookeeperClient> ENV_CLIENT_MAP = new HashMap<>();

	private static final Map<Class<?>, Pair<?, ?>> CLIENT_MAP = new HashMap<>();

	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);

	private static interface Constants {
		static final String SERVICE_PREFIX = ZkConfig.SERVICE_PREFIX;;
		static final String IFACE_SUFFIX = "$Iface";
		static final String ASYN_IFACE_SUFFIX = "$AsyncIface";
		static final String ASYN_CLIENT_SUFFIX = "$AsyncClient";
		static final String CLIENT_SUFFIX = "$Client";
		static final String PROCESSOR_SUFFIX = "$Processor";

	}
	
	@SuppressWarnings("unchecked")
	public static <Iface> Iface getIfaceClient(Class<Iface> iface,int timeout){
		return (Iface)CLIENT_MAP.get(iface).getLeft();
	}
	
	@SuppressWarnings("unchecked")
	public static <AsyncIface> AsyncIface getAsyncIfaceClient(Class<AsyncIface> iface,int timeout){
		return (AsyncIface)CLIENT_MAP.get(iface).getRight();
	}
	
	

	public static Pair<?, ?> createClient(Class<?> clazz, int timeout) {
		String ifaceName = clazz.getName();
		if (!ifaceName.endsWith(Constants.IFACE_SUFFIX) && !ifaceName.endsWith(Constants.ASYN_IFACE_SUFFIX)) {
			throw new IllegalArgumentException(ifaceName + "不是合法thrift接口");
		}

		String serviceName=StringUtils.removeEnd(StringUtils.removeEnd(ifaceName, Constants.IFACE_SUFFIX), Constants.ASYN_IFACE_SUFFIX);
		
		Pair<?, ?> client = (Pair<?, ?>) CLIENT_MAP.get(clazz);
		if (client == null) {
			try{
				ZookeeperClient zkClient=ENV_CLIENT_MAP.get(EnvironmentUtil.getLocalEnviromentType());
				if(zkClient==null) zkClient=new ZookeeperClient();
				ENV_CLIENT_MAP.put(EnvironmentUtil.getLocalEnviromentType(), zkClient);
				
				String path=Constants.SERVICE_PREFIX;
				if(!path.endsWith("/")){
					path+="/";
				}
				path+="serviceName";
				List<String> children=zkClient.getChildren(path,(event)->{
					
				});
				if(children==null||children.size()==0){
					throw new RuntimeException(path+"节点下无任何可用节点");
				}
				
				TNonblockingTransport transport = new TNonblockingSocket("", 0, timeout);
				// transport.open();
				TProtocol protocol = new TBinaryProtocol(transport);
				String syncClientName = serviceName + Constants.CLIENT_SUFFIX;
				Constructor<?> syncConstructor = Class.forName(syncClientName).getConstructor(TProtocol.class);

				TAsyncClientManager clientManager = new TAsyncClientManager();
				String asynClientName = serviceName + Constants.ASYN_CLIENT_SUFFIX;
				Constructor<?> asynConstructor = Class.forName(asynClientName).getConstructor(TProtocol.class, TAsyncClientManager.class,
						TNonblockingTransport.class);
				Pair<?,?> pair=Pair.of(syncConstructor.newInstance(protocol), asynConstructor.newInstance(protocol, clientManager, transport));
				CLIENT_MAP.put(clazz,pair);
				client=pair;
			}catch(Exception e){
				
			}
			
		}
		return client;
	}

	public static void startThriftServer(Object thriftServiceObj) {

		EnvironmentType env = EnvironmentUtil.getLocalEnviromentType();
		ZookeeperClient zkClient = ENV_CLIENT_MAP.get(env);
		if (zkClient == null) {
			try {
				zkClient = new ZookeeperClient();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ENV_CLIENT_MAP.put(env, zkClient);
		}

		for (Class<?> inter : thriftServiceObj.getClass().getInterfaces()) {
			String interfaceName = inter.getName();
			if (interfaceName.endsWith(ThriftUtil.Constants.IFACE_SUFFIX)) {
				String serviceName = StringUtils.removeEnd(interfaceName, ThriftUtil.Constants.IFACE_SUFFIX);
				String bindIp = ZkConfig.getServiceIp(serviceName);
				int bindPort = ZkConfig.getServicePort(serviceName);
				String path = Constants.SERVICE_PREFIX + serviceName + "/" + bindIp + ":" + bindPort;
				zkClient.createPath(path, "".getBytes(), ZookeeperClient.EPHEMERAL);
				try {
					Class<?> processorClass = Class.forName(serviceName + ThriftUtil.Constants.PROCESSOR_SUFFIX);
					Class<?> ifaceClass = Class.forName(serviceName + ThriftUtil.Constants.IFACE_SUFFIX);
					@SuppressWarnings("unchecked")
					Constructor<TProcessor> ctor = (Constructor<TProcessor>) processorClass.getConstructor(ifaceClass);
					TProcessor processor = ctor.newInstance(thriftServiceObj);
					TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
					TServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(serverTransport).processor(processor));
					server.serve();
					final ZookeeperClient zkcli = zkClient;
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						zkcli.deletePath(path);
						server.stop();
						serverTransport.close();
					}));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
