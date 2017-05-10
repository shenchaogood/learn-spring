package sc.learn.common.util;

import java.lang.reflect.Constructor;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

class IfaceClientProxyFactory extends AbstractThriftClient {

	public IfaceClientProxyFactory(Class<?> clazz) {
		super(clazz);
	}

	protected ThriftClientHolder bindNewInstance(String ip,int port,int timeout){
		try {
			TNonblockingTransport transport = new TNonblockingSocket(ip, port, timeout);
			// transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			Constructor<?> syncConstructor = clazz.getConstructor(TProtocol.class);

			return new ThriftClientHolder(transport,syncConstructor.newInstance(protocol));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
