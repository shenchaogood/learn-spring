package sc.learn.common.util;

 interface ThriftClient {

	Object createProxy();
	
	void bind(String ip,int port,int timeout);
}
