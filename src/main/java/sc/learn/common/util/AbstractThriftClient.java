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
import org.apache.thrift.transport.TTransport;

abstract class AbstractThriftClient implements ThriftClient,InvocationHandler {

	protected class ThriftClientHolder{
		public final TTransport transport;
		public final Object target;
		public ThriftClientHolder(TTransport transport, Object target) {
			this.transport = transport;
			this.target = target;
		}
	}
	
	protected Map<String,ThriftClientHolder> cache = new ConcurrentHashMap<>();
	
	protected Class<?> clazz;
	public AbstractThriftClient(Class<?> clazz){
		this.clazz=clazz;
	}
	
	@Override
	public void bind(String ip,int port,int timeout){
		try {
			cache.computeIfAbsent(ip+":"+port, p->bindNewInstance(ip,port,timeout));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void bindAll(List<String> ipPortTimeouts){
		try {
			ipPortTimeouts.forEach(ipPortTimeout->{
				String[] ipPortTimeoutStr=ipPortTimeout.split(":");
				cache.computeIfAbsent(ipPortTimeoutStr[0]+":"+ipPortTimeoutStr[1], p->bindNewInstance(ipPortTimeoutStr[0],
						Integer.parseInt(ipPortTimeoutStr[1]),Integer.parseInt(ipPortTimeoutStr[2])));
			});
			CollectionUtils.subtract(cache.entrySet(),ipPortTimeouts).forEach(item->cache.remove(item).transport.close());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract ThriftClientHolder bindNewInstance(String ip,int port,int timeout);
	
	@Override
	public Object createProxy() {
		return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{clazz} , this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		int size=cache.values().size();
		if(size==0){
			throw new RuntimeException("target 未生成");
		}
		Object target=cache.values().toArray(new ThriftClientHolder[cache.values().size()])[new Random().nextInt(size)].target;
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
