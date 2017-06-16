package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThriftUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);
	private static final Logger LOG=LoggerFactory.getLogger(ZookeeperClient.class);
	private static final Map<EnvironmentType, ZookeeperClient> ENV_CLIENT_MAP = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Pair<?, ?>> CLIENT_MAP = new ConcurrentHashMap<>();

	public static interface Constants {
		static final String SERVICE_PREFIX = ZkConfig.SERVICE_PREFIX;;
		static final String IFACE_SUFFIX = "$Iface";
		static final String ASYN_IFACE_SUFFIX = "$AsyncIface";
		static final String ASYN_CLIENT_SUFFIX = "$AsyncClient";
		static final String CLIENT_SUFFIX = "$Client";
		static final String PROCESSOR_SUFFIX = "$Processor";

	}

	@SuppressWarnings("unchecked")
	public static <Iface> Iface getIfaceClient(Class<Iface> iface, int timeout) throws IOException {
		return (Iface) createClient(iface,timeout).getLeft();
	}

	@SuppressWarnings("unchecked")
	public static <AsyncIface> AsyncIface getAsyncIfaceClient(Class<AsyncIface> asyncIface, int timeout) throws IOException{
		return (AsyncIface) createClient(asyncIface,timeout).getRight();
	}

	public static Pair<?, ?> createClient(Class<?> clazz, int timeout) throws IOException {
		String ifaceName = clazz.getName();
		if (!ifaceName.endsWith(Constants.IFACE_SUFFIX) && !ifaceName.endsWith(Constants.ASYN_IFACE_SUFFIX)) {
			throw new IllegalArgumentException(ifaceName + "不是合法thrift接口");
		}

		String serviceName = StringUtils.removeEnd(StringUtils.removeEnd(ifaceName, Constants.IFACE_SUFFIX), Constants.ASYN_IFACE_SUFFIX);

		Pair<?, ?> client = (Pair<?, ?>) CLIENT_MAP.get(clazz);
		if (client == null) {
			ZookeeperClient zkClient = ENV_CLIENT_MAP.get(EnvironmentUtil.getLocalEnviromentType());
			if (zkClient == null){
				zkClient = new ZookeeperClient();
				ENV_CLIENT_MAP.put(EnvironmentUtil.getLocalEnviromentType(), zkClient);
			}

			StringBuilder path = new StringBuilder(Constants.SERVICE_PREFIX);
			if (!path.toString().endsWith("/")) {
				path.append("/");
			}
			path.append(serviceName);

			Class<?> syncClientName=null;
			Class<?> asynClientName = null;
			try {
				syncClientName = Class.forName(serviceName + Constants.CLIENT_SUFFIX);
				asynClientName = Class.forName(serviceName + Constants.ASYN_CLIENT_SUFFIX);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			ThriftClient ifaceObj=new IfaceClientProxyFactory(syncClientName);
			ThriftClient asynIfaceObj=new AsynIfaceClientProxyFactory(asynClientName);
			
			client = Pair.of(ifaceObj.createProxy(),asynIfaceObj.createProxy());
			CLIENT_MAP.put(clazz, client);
			ZookeeperClient currZkCli=zkClient;
			Map<String,Object> map=new HashMap<>();
			ChildrenCallback cb=(rc, paths, ctx, children) -> {
				switch (Code.get(rc)) {
				case CONNECTIONLOSS:
					currZkCli.getChildren(path.toString(),(Watcher)map.get("watcher"), (ChildrenCallback)map.get("cb"));
					break;
				case OK:
					if (children == null || children.size() == 0) {
						LOGGER.warn(path + "节点下无任何可用节点");
					}
					ifaceObj.bindAll(children);
					asynIfaceObj.bindAll(children);
					break;
				default:
					LOGGER.info(KeeperException.create(Code.get(rc)).getMessage());
					break;
				}
			};
			
			Watcher watcher=event->{
				switch(event.getType()){
				case NodeChildrenChanged:
					currZkCli.getChildren(path.toString(),(Watcher)map.get("watcher"),cb);
					break;
				default:
					LOG.info(event.toString());
					break;
				}
			};
			map.put("cb", cb);
			map.put("watcher", watcher);
			zkClient.getChildren(path.toString(),watcher,cb);
		}
		return client;
	}
	

	public static void startThriftServer(Object thriftServiceObj) {

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
				String path = Constants.SERVICE_PREFIX +"/"+serviceName + "/" + bindIp + ":" + bindPort;
				zkClient.createPath(path, "".getBytes(), ZookeeperClient.EPHEMERAL);
				try {
					Class<?> processorClass = Class.forName(serviceName + ThriftUtil.Constants.PROCESSOR_SUFFIX);
					Class<?> ifaceClass = Class.forName(serviceName + ThriftUtil.Constants.IFACE_SUFFIX);
					@SuppressWarnings("unchecked")
					Constructor<TProcessor> ctor = (Constructor<TProcessor>) processorClass.getConstructor(ifaceClass);
					TProcessor processor = ctor.newInstance(thriftServiceObj);
					
					TNonblockingServerSocket socket = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
					THsHaServer.Args arg = new THsHaServer.Args(socket);
//					// 高效率的、密集的二进制编码格式进行数据传输
//					// 使用非阻塞方式，按块的大小进行传输，类似于 Java 中的 NIO
					arg.protocolFactory(new TCompactProtocol.Factory());
					arg.transportFactory(new TFramedTransport.Factory());
					arg.processorFactory(new TProcessorFactory(processor));
					TServer server = new THsHaServer(arg);
					
//					TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(new InetSocketAddress(bindIp, bindPort));
//					TServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(serverTransport).processor(processor));
					final ZookeeperClient zkcli = zkClient;
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						try {
							zkcli.deletePath(path);
							zkcli.close();
						} catch (Exception e) {
						}
						server.stop();
//						serverTransport.close();
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
