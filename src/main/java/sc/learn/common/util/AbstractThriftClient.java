package sc.learn.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.mina.util.ConcurrentHashSet;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractThriftClient implements ThriftClient, InvocationHandler {

	private static final ThreadLocal<ThriftClientHolder> THREAD_LOCAL=new ThreadLocal<>();
	protected int timeout;
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	protected class ThriftClientHolder {
		public final TTransport transport;
		public final Object target;

		public ThriftClientHolder(TTransport transport, Object target) {
			this.transport = transport;
			this.target = target;
		}
	}

	protected Set<String> cache = new ConcurrentHashSet<>();

	protected Class<?> clazz;

	public AbstractThriftClient(Class<?> clazz,int timeout) {
		this.clazz = clazz;
		this.timeout=timeout;
	}

	@Override
	public void bindAll(List<String> ipPorts) {
		cache.clear();
		cache.addAll(ipPorts);
	}

	protected abstract ThriftClientHolder bindNewInstance(String ip, int port);

	@Override
	public Object createProxy() {
		return Proxy.newProxyInstance(getClass().getClassLoader(), clazz.getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		LOGGER.debug("执行方法:{}，参数:{}", method, Arrays.toString(args));
		long startTime = System.currentTimeMillis();
		int size;
		ThriftClientHolder clientHolder=THREAD_LOCAL.get();
		try {
			if(clientHolder==null){
				while ((size=(cache.size())) == 0&&timeout>System.currentTimeMillis()-startTime) {
					Thread.sleep(timeout/5);
				}
				if(size == 0){
					throw new RuntimeException("target 未生成,可能是因为服务未注册");
				}
				String[] ipPort = cache.toArray(new String[size])[new Random().nextInt(size)].split(":");
				clientHolder=bindNewInstance(ipPort[0], Integer.parseInt(ipPort[1]));
				THREAD_LOCAL.set(clientHolder);
			}
			System.out.println(clientHolder.target);
			Object result = method.invoke(clientHolder.target, args);
			LOGGER.debug("执行方法:{}，结果:{}，用时:{}", method, result, System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception e) {
			LOGGER.error("执行方法:{}，异常:{}", method, e);
			throw new RuntimeException(e);
		}finally {
			if(clientHolder!=null){
//				clientHolder.transport.close();
			}
		}
	}
}
