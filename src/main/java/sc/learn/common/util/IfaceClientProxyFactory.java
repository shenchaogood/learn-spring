package sc.learn.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;


import sc.learn.common.util.ThriftUtil.Constants;

class IfaceClientProxyFactory extends AbstractThriftClient {

	public IfaceClientProxyFactory(Class<?> clazz) {
		super(clazz);
	}

	public void bindNewInstance(String serviceName,String ip,int port,int timeout){
		try {
			TNonblockingTransport transport = new TNonblockingSocket(ip, port, timeout);
			// transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			String syncClientName = serviceName + Constants.CLIENT_SUFFIX;
			Constructor<?> syncConstructor = Class.forName(syncClientName).getConstructor(TProtocol.class);

			TAsyncClientManager clientManager = new TAsyncClientManager();
			String asynClientName = serviceName + Constants.ASYN_CLIENT_SUFFIX;
			Constructor<?> asynConstructor = Class.forName(asynClientName).getConstructor(TProtocol.class, TAsyncClientManager.class,
					TNonblockingTransport.class);

			cache.put(ip+":"+port, syncConstructor.newInstance(protocol));
			
			//bind(asynConstructor.newInstance(protocol, clientManager, transport));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
