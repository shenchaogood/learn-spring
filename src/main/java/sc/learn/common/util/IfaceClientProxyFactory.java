package sc.learn.common.util;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

class IfaceClientProxyFactory extends AbstractThriftClient {

	public IfaceClientProxyFactory(Class<?> clazz,int timeout) {
		super(clazz,timeout);
	}

	protected ThriftClientHolder bindNewInstance(String ip, int port) {
		try {
			
//			TTransport transport = new TFramedTransport(new TSocket(ip, port, timeout));
//			TProtocol protocol = new TCompactProtocol(transport);
			
			TTransport transport = new TFramedTransport(new TSocket(ip, port, timeout));
	        TProtocol protocol = new TBinaryProtocol(transport);
			transport.open();
			Constructor<?> syncConstructor = clazz.getConstructor(TProtocol.class);
			return new ThriftClientHolder(transport, syncConstructor.newInstance(protocol));
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
	}

}
