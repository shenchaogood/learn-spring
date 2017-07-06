package sc.learn.common.thrift2;

import java.io.IOException;

import org.apache.thrift.transport.TNonblockingSocket;

public class ThriftAsyncIfaceTransport extends TNonblockingSocket {
	
	private String host;
	private int port;
	
	public ThriftAsyncIfaceTransport(String host, int port, int timeout) throws IOException {
		super(host, port, timeout);
		this.host=host;
		this.port=port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
}
