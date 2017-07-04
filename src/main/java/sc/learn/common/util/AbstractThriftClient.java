package sc.learn.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractThriftClient implements ThriftClient, InvocationHandler {

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

	protected Map<String, ThriftClientHolder> cache = new ConcurrentHashMap<>();

	protected Class<?> clazz;

	public AbstractThriftClient(Class<?> clazz,int timeout) {
		this.clazz = clazz;
		this.timeout=timeout;
	}

	@Override
	public void bind(String ip, int port, int timeout) {
		this.timeout=timeout;
		cache.computeIfAbsent(ip + ":" + port, p -> bindNewInstance(ip, port));
	}

	@Override
	public void bindAll(List<String> ipPortTimeouts) {
		ipPortTimeouts.forEach(ipPortTimeout -> {
			String[] ipPortTimeoutStr = ipPortTimeout.split(":");
			if (!cache.containsKey(ipPortTimeoutStr[0] + ":" + ipPortTimeoutStr[1])) {
				cache.computeIfAbsent(ipPortTimeoutStr[0] + ":" + ipPortTimeoutStr[1],
						p -> bindNewInstance(ipPortTimeoutStr[0], Integer.parseInt(ipPortTimeoutStr[1])));
			}
		});
		CollectionUtils.subtract(cache.entrySet(), ipPortTimeouts).forEach(item -> {
			ThriftClientHolder holder = cache.remove(item);
			if (holder != null) {
				holder.transport.close();
			}
		});
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
		try {
			while ((size=(cache.values().size())) == 0&&timeout>System.currentTimeMillis()-startTime) {
				Thread.sleep(timeout/5);
			}
			if(size == 0){
				throw new RuntimeException("target 未生成,可能是因为服务未注册");
			}
			Object target = cache.values().toArray(new ThriftClientHolder[size])[new Random().nextInt(size)].target;
			Object result = method.invoke(target, args);
			LOGGER.debug("执行方法:{}，结果:{}，用时:{}", method, result, System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception e) {
			LOGGER.error("执行方法:{}，异常:{}", method, e);
			throw new RuntimeException(e);
		}
	}
}
