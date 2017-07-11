package sc.learn.common.thrift;

import java.io.IOException;

import org.apache.thrift.transport.TNonblockingSocket;

public class ThriftAsyncIfaceTransport extends TNonblockingSocket implements AcquirableThriftAddress{
	
	private String host;
	private int port;
	
	public ThriftAsyncIfaceTransport(String host, int port, int timeout) throws IOException {
		super(host, port, timeout);
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
