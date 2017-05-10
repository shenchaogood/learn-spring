package sc.learn.common.util;

import java.util.List;

interface ThriftClient {

	Object createProxy();
	
	void bind(String ip,int port,int timeout);

	void bindAll(List<String> ipPortTimeouts);
}
