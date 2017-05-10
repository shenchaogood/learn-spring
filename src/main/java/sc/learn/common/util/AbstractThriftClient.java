package sc.learn.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractThriftClient implements ThriftClient,InvocationHandler {

	protected Map<String,Object> cache = new ConcurrentHashMap<>();
	
	protected Class<?> clazz;
	public AbstractThriftClient(Class<?> clazz){
		this.clazz=clazz;
	}
	
	@Override
	public Object createProxy() {
		return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{clazz} , this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		int size=cache.values().size();
		if(size==0){
			throw new RuntimeException("target 未生成");
		}
		return cache.values().toArray()[new Random().nextInt(size)];
	}
}
