package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.thrift2.AbstractThriftTransportPool;
import sc.learn.common.thrift2.ThriftException;
import sc.learn.common.thrift2.ThriftInvocationHandler;
import sc.learn.common.thrift2.ThriftProtocolEnum;

public abstract class ThriftUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);
	private static final Map<EnvironmentType, ZookeeperClient> ENV_CLIENT_MAP = new HashMap<>();

	public static interface Constants {
		static final String SERVICE_PREFIX = ZkConfig.SERVICE_PREFIX;;
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

	public static interface ProvideTransportFillBackServicePath{
		AbstractThriftTransportPool<TTransport> providePath(String serviceName);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T createClient(Class<T> clazz, int timeout, ProvideTransportFillBackServicePath provider, ThriftProtocolEnum protocol) {
		String ifaceName = clazz.getName();
		boolean isSynchronized;
		String serviceName;
		if (ifaceName.endsWith(Constants.IFACE_SUFFIX)) {
			isSynchronized = true;
			serviceName = StringUtils.removeEnd(ifaceName, Constants.IFACE_SUFFIX);
		} else if (ifaceName.endsWith(Constants.ASYN_IFACE_SUFFIX)) {
			isSynchronized = false;
			serviceName = StringUtils.removeEnd(ifaceName, Constants.ASYN_IFACE_SUFFIX);
		} else {
			throw new ThriftException(ifaceName + "不是合法thrift接口");
		}
		StringBuilder addressPath = new StringBuilder(Constants.SERVICE_PREFIX);
		if (!addressPath.toString().endsWith("/")) {
			addressPath.append("/");
		}
		addressPath.append(serviceName);
		
		AbstractThriftTransportPool<TTransport> pool=provider.providePath(serviceName);
		
		// 加载第三方提供的接口和Client类
		// 设置创建handler
		InvocationHandler clientHandler = new ThriftInvocationHandler<T>(pool, serviceName, isSynchronized, protocol);
		return (T) Proxy.newProxyInstance(ThriftUtil.class.getClassLoader(), new Class[] { clazz }, clientHandler);
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
