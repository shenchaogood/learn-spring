package sc.learn.common.util;

import java.lang.reflect.Constructor;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

class AsynIfaceClientProxyFactory extends AbstractThriftClient {

	public AsynIfaceClientProxyFactory(Class<?> clazz) {
		super(clazz);
	}

	protected Object bindNewInstance(String ip,int port,int timeout){
		try {
			TNonblockingTransport transport = new TNonblockingSocket(ip, port, timeout);
			// transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			TAsyncClientManager clientManager = new TAsyncClientManager();
			Constructor<?> asynConstructor = clazz.getConstructor(TProtocol.class, TAsyncClientManager.class, TNonblockingTransport.class);

			return asynConstructor.newInstance(protocol, clientManager, transport);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
