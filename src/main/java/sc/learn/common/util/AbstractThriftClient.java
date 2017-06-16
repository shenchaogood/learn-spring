package sc.learn.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractThriftClient implements ThriftClient, InvocationHandler {

	protected final Logger LOGGER=LoggerFactory.getLogger(getClass());
	
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

	public AbstractThriftClient(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void bind(String ip, int port, int timeout) {
		cache.computeIfAbsent(ip + ":" + port, p -> bindNewInstance(ip, port, timeout));
	}

	@Override
	public void bindAll(List<String> ipPortTimeouts) {
		ipPortTimeouts.forEach(ipPortTimeout -> {
			String[] ipPortTimeoutStr = ipPortTimeout.split(":");
			if (!cache.containsKey(ipPortTimeoutStr[0] + ":" + ipPortTimeoutStr[1])) {
				cache.computeIfAbsent(ipPortTimeoutStr[0] + ":" + ipPortTimeoutStr[1],
						p -> bindNewInstance(ipPortTimeoutStr[0], Integer.parseInt(ipPortTimeoutStr[1]), 
								ipPortTimeoutStr.length==3?Integer.parseInt(ipPortTimeoutStr[2]):1000));
			}
		});
		CollectionUtils.subtract(cache.entrySet(), ipPortTimeouts).forEach(item -> {
			ThriftClientHolder holder=cache.remove(item);
			if(holder!=null){
				holder.transport.close();
			}
		});
	}

	protected abstract ThriftClientHolder bindNewInstance(String ip, int port, int timeout);

	@Override
	public Object createProxy() {
		return Proxy.newProxyInstance(getClass().getClassLoader(), clazz.getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		int size = 0;
		try {
			while ((size = cache.values().size()) == 0) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			if (size == 0) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
				throw new RuntimeException("target 未生成");
			}
		}
		
		Object target = cache.values().toArray(new ThriftClientHolder[cache.values().size()])[new Random().nextInt(size)].target;
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
