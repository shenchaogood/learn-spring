package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.thrift2.AbstractThriftTransportPool;
import sc.learn.common.thrift2.AddressProvider;
import sc.learn.common.thrift2.ThriftAsyncIfaceTransportPool;
import sc.learn.common.thrift2.ThriftException;
import sc.learn.common.thrift2.ThriftIfaceTransportPool;
import sc.learn.common.thrift2.ThriftInvocationHandler;
import sc.learn.common.thrift2.ThriftProtocolEnum;

public abstract class ThriftUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);
	private static final Map<EnvironmentType, ZookeeperClient> ENV_CLIENT_MAP = new HashMap<>();
	
	private static final Map<Class<?>,AbstractThriftTransportPool<? extends TTransport>> TRANSPORT_REGISTER=new HashMap<>();
	
	private static final Map<Class<?>,AddressProvider> ADDRESSPROVIDER_MAP=new HashMap<>();
	
	public static void registTransportPool(Class<?> iface,AbstractThriftTransportPool<?> pool){
		if(!TRANSPORT_REGISTER.containsKey(iface)){
			synchronized (TRANSPORT_REGISTER) {
				if(!TRANSPORT_REGISTER.containsKey(iface)){
					TRANSPORT_REGISTER.put(iface, pool);
				}
			}
		}
	}
	
	public static interface Constants {
		static final String SERVICE_PREFIX = ZkConfig.SERVICE_PREFIX;
		static final String IFACE_SUFFIX = "$Iface";
		static final String ASYN_IFACE_SUFFIX = "$AsyncIface";
		static final String ASYN_CLIENT_SUFFIX = "$AsyncClient";
		static final String CLIENT_SUFFIX = "$Client";
		static final String PROCESSOR_SUFFIX = "$Processor";
	}
	
	public static TProtocolFactory getTProtocolFactory(ThriftProtocolEnum protocol) {
        // 服务端均为非阻塞类型
    	TProtocolFactory tProtocolFactory ;
        switch (protocol) {
        case BINARY:
        	tProtocolFactory = new TBinaryProtocol.Factory();
            break;
        case COMPACT:
        	tProtocolFactory = new TCompactProtocol.Factory();
            break;
        case JSON:
        	tProtocolFactory = new TJSONProtocol.Factory();
            break;
        case SIMPLE_JSON:
        	tProtocolFactory = new TSimpleJSONProtocol.Factory();
            break;
        default:
        	tProtocolFactory = new TBinaryProtocol.Factory();
        }
        return tProtocolFactory;
    }
	
	public static Triple<Boolean,String,String> fetchSynchronizedAndIfacePathAndServiceName(Class<?> ifaceClass){
		String ifaceName = ifaceClass.getName();
		String serviceName;
		boolean isSynchronized;
		if (ifaceName.endsWith(Constants.IFACE_SUFFIX)){
			isSynchronized=true;
			serviceName = StringUtils.removeEnd(ifaceName, Constants.IFACE_SUFFIX);
		} else if (ifaceName.endsWith(Constants.ASYN_IFACE_SUFFIX)) {
			isSynchronized=false;
			serviceName = StringUtils.removeEnd(ifaceName, Constants.ASYN_IFACE_SUFFIX);
		} else {
			throw new ThriftException(ifaceName + "不是合法thrift接口");
		}
		StringBuilder addressPath = new StringBuilder(Constants.SERVICE_PREFIX).append("/").append(serviceName);
		return Triple.of(isSynchronized, addressPath.toString(),serviceName);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createClient(Class<T> ifaceClass, int timeout, ThriftProtocolEnum protocol,CuratorFramework zkClient) {
		if(zkClient.getState()==CuratorFrameworkState.LATENT){
			zkClient.start();
		}
		AbstractThriftTransportPool<? extends TTransport> pool = TRANSPORT_REGISTER.get(ifaceClass);
		Triple<Boolean,String,String> sis=fetchSynchronizedAndIfacePathAndServiceName(ifaceClass);
		if(pool==null){
			synchronized (ThriftUtil.class) {
				if(!TRANSPORT_REGISTER.containsKey(ifaceClass)){
					try {
						AddressProvider addressProvider=ADDRESSPROVIDER_MAP.get(Class.forName(sis.getRight()));
						if(addressProvider==null){
							synchronized (ThriftUtil.class) {
								if(addressProvider==null){
									addressProvider=new AddressProvider(null, zkClient, sis.getMiddle());
									ADDRESSPROVIDER_MAP.put(Class.forName(sis.getRight()), addressProvider);
								}
							}
						}
						if(sis.getLeft()){
							pool=new ThriftIfaceTransportPool(addressProvider ,timeout, 8, 8, 1, 1000);
						}else{
							pool=new ThriftAsyncIfaceTransportPool(addressProvider, timeout, 8, 8, 1, 1000);
						}
						registTransportPool(ifaceClass, pool);
					} catch (ClassNotFoundException e) {
						throw new ThriftException(e);
					}
				}
			}
		}
		
		// 加载第三方提供的接口和Client类
		// 设置创建handler
		InvocationHandler clientHandler = new ThriftInvocationHandler<T>(pool, sis.getRight(), sis.getLeft(), protocol);
		return (T) Proxy.newProxyInstance(ThriftUtil.class.getClassLoader(), new Class[] { ifaceClass }, clientHandler);
	}
	
	public static void startThriftServer(Object thriftServiceObj,ThriftProtocolEnum protocol,final CuratorFramework zkClient) {
		Class<?>[] ifaces=thriftServiceObj.getClass().getInterfaces();
		if(ArrayUtil.isEmpty(ifaces)){
			throw new ThriftException(thriftServiceObj.getClass().getName()+"不是一个有效的thrift实现");
		}
		if(zkClient.getState()==CuratorFrameworkState.LATENT){
			zkClient.start();
		}
		for (Class<?> inter : thriftServiceObj.getClass().getInterfaces()) {
			String interfaceName = inter.getName();
			if (interfaceName.endsWith(ThriftUtil.Constants.IFACE_SUFFIX)) {
				String serviceName = StringUtils.removeEnd(interfaceName, ThriftUtil.Constants.IFACE_SUFFIX);
				String bindIp = ZkConfig.getServiceIp(serviceName);
				int bindPort = ZkConfig.getServicePort(serviceName);
				String path = Constants.SERVICE_PREFIX + "/" + serviceName ;
				try {
					if(zkClient.checkExists().forPath(path)==null){
						zkClient.create().creatingParentsIfNeeded().forPath(path, new byte[0]);
					}
					zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(path+"/"+EnvironmentUtil.getEnvironmentName() ,(bindIp + ":" + bindPort).getBytes());
					Class<?> processorClass = Class.forName(serviceName + ThriftUtil.Constants.PROCESSOR_SUFFIX);
					Class<?> ifaceClass = Class.forName(serviceName + ThriftUtil.Constants.IFACE_SUFFIX);
					@SuppressWarnings("unchecked")
					Constructor<TProcessor> ctor = (Constructor<TProcessor>) processorClass.getConstructor(ifaceClass);
					TProcessor processor = ctor.newInstance(thriftServiceObj);

					// TNonblockingServerSocket socket = new
					// TNonblockingServerSocket(new InetSocketAddress(bindIp,
					// bindPort));
					// THsHaServer.Args arg = new THsHaServer.Args(socket);
					// // 高效率的、密集的二进制编码格式进行数据传输
					// // 使用非阻塞方式，按块的大小进行传输，类似于 Java 中的 NIO
					// arg.protocolFactory(new TCompactProtocol.Factory());
					// arg.transportFactory(new TFramedTransport.Factory());
					// arg.processorFactory(new TProcessorFactory(processor));
					// TServer server = new THsHaServer(arg);

					TNonblockingServerTransport socket = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
					TThreadedSelectorServer.Args arg = new TThreadedSelectorServer.Args(socket);
					arg.protocolFactory(getTProtocolFactory(protocol));
					arg.processor(processor);
					TServer server = new TThreadedSelectorServer(arg);

					// TNonblockingServerTransport serverTransport = new
					// TNonblockingServerSocket(new InetSocketAddress(bindIp,
					// bindPort));
					// TServer server = new TThreadedSelectorServer(new
					// TThreadedSelectorServer.Args(serverTransport).processor(processor));
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						try {
							zkClient.delete().forPath(path);
						} catch (Exception e) {
						}finally {
							if(server!=null&&server.isServing()){
								server.stop();
							}
							if(socket!=null){
								socket.close();
							}
							// serverTransport.close();
						}
					}));
					new Thread(server::serve).start();
				} catch (Exception e) {
					throw new ThriftException(e);
				}
			}
		}
	}

	public static void startThriftServer(Object thriftServiceObj,ThriftProtocolEnum protocol) {
		EnvironmentType env = EnvironmentUtil.getLocalEnviromentType();
		ZookeeperClient zkClient = ENV_CLIENT_MAP.get(env);
		if (zkClient == null) {
			try {
				synchronized (ThriftUtil.class) {
					if (zkClient == null) {
						zkClient = new ZookeeperClient();
						ENV_CLIENT_MAP.put(env, zkClient);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		for (Class<?> inter : thriftServiceObj.getClass().getInterfaces()) {
			String interfaceName = inter.getName();
			if (interfaceName.endsWith(ThriftUtil.Constants.IFACE_SUFFIX)) {
				String serviceName = StringUtils.removeEnd(interfaceName, ThriftUtil.Constants.IFACE_SUFFIX);
				String bindIp = ZkConfig.getServiceIp(serviceName);
				int bindPort = ZkConfig.getServicePort(serviceName);
				String path = Constants.SERVICE_PREFIX + "/" + serviceName + "/" + bindIp + ":" + bindPort;
				zkClient.createPath(path, "".getBytes(), ZookeeperClient.EPHEMERAL);
				try {
					Class<?> processorClass = Class.forName(serviceName + ThriftUtil.Constants.PROCESSOR_SUFFIX);
					Class<?> ifaceClass = Class.forName(serviceName + ThriftUtil.Constants.IFACE_SUFFIX);
					@SuppressWarnings("unchecked")
					Constructor<TProcessor> ctor = (Constructor<TProcessor>) processorClass.getConstructor(ifaceClass);
					TProcessor processor = ctor.newInstance(thriftServiceObj);

					// TNonblockingServerSocket socket = new
					// TNonblockingServerSocket(new InetSocketAddress(bindIp,
					// bindPort));
					// THsHaServer.Args arg = new THsHaServer.Args(socket);
					// // 高效率的、密集的二进制编码格式进行数据传输
					// // 使用非阻塞方式，按块的大小进行传输，类似于 Java 中的 NIO
					// arg.protocolFactory(new TCompactProtocol.Factory());
					// arg.transportFactory(new TFramedTransport.Factory());
					// arg.processorFactory(new TProcessorFactory(processor));
					// TServer server = new THsHaServer(arg);

					TNonblockingServerTransport socket = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
					TThreadedSelectorServer.Args arg = new TThreadedSelectorServer.Args(socket);
					arg.protocolFactory(getTProtocolFactory(protocol));
					arg.processor(processor);
					TServer server = new TThreadedSelectorServer(arg);

					// TNonblockingServerTransport serverTransport = new
					// TNonblockingServerSocket(new InetSocketAddress(bindIp,
					// bindPort));
					// TServer server = new TThreadedSelectorServer(new
					// TThreadedSelectorServer.Args(serverTransport).processor(processor));
					final ZookeeperClient zkcli = zkClient;
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						try {
							zkcli.deletePath(path);
							zkcli.close();
						} catch (Exception e) {
						}
						server.stop();
						// serverTransport.close();
						socket.close();
					}));
					new Thread(server::serve).start();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
