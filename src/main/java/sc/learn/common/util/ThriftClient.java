package sc.learn.common.util;

import java.util.List;

interface ThriftClient {

	Object createProxy();
	
	void bindAll(List<String> ipPort);
}
