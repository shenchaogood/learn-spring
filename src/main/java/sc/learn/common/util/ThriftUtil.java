package sc.learn.common.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThriftUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ThriftUtil.class);
	private static final Logger LOG=LoggerFactory.getLogger(ZookeeperClient.class);
	private static final Map<EnvironmentType, ZookeeperClient> ENV_CLIENT_MAP = new HashMap<>();
	private static final Map<Class<?>, Pair<?, ?>> CLIENT_MAP = new HashMap<>();

	public static interface Constants {
		static final String SERVICE_PREFIX = ZkConfig.SERVICE_PREFIX;;
		static final String IFACE_SUFFIX = "$Iface";
		static final String ASYN_IFACE_SUFFIX = "$AsyncIface";
		static final String ASYN_CLIENT_SUFFIX = "$AsyncClient";
		static final String CLIENT_SUFFIX = "$Client";
		static final String PROCESSOR_SUFFIX = "$Processor";

	}

	@SuppressWarnings("unchecked")
	public static <Iface> Iface getIfaceClient(Class<Iface> iface, int timeout) {
		return (Iface) CLIENT_MAP.get(iface).getLeft();
	}

	@SuppressWarnings("unchecked")
	public static <AsyncIface> AsyncIface getAsyncIfaceClient(Class<AsyncIface> iface, int timeout) {
		return (AsyncIface) CLIENT_MAP.get(iface).getRight();
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
						throw new RuntimeException(path + "节点下无任何可用节点");
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
