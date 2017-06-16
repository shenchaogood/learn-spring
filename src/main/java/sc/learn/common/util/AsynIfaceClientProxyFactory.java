package sc.learn.common.util;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

class AsynIfaceClientProxyFactory extends AbstractThriftClient {

	public AsynIfaceClientProxyFactory(Class<?> clazz) {
		super(clazz);
	}

	protected ThriftClientHolder bindNewInstance(String ip,int port,int timeout){
		try {
			TNonblockingTransport transport = new TNonblockingSocket(ip, port, timeout);
			TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
			TAsyncClientManager clientManager = new TAsyncClientManager();
			Constructor<?> asynConstructor = clazz.getConstructor(TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class);
			return new ThriftClientHolder(transport, asynConstructor.newInstance(protocolFactory, clientManager, transport));
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
	}
}
