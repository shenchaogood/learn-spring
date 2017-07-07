package sc.learn.common.thrift2;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

public class ThriftIfaceTransport extends TFramedTransport implements AcquirableThriftAddress{
	
	private String host;
	private int port;

	public ThriftIfaceTransport(String host,int port,int timeout) {
		super(new TSocket(host, port, timeout));
		this.host=host;
		this.port=port;
	}

	@Override
	public String getThriftHost() {
		return host;
	}

	@Override
	public int getThriftPort() {
		return port;
	}
	
}
