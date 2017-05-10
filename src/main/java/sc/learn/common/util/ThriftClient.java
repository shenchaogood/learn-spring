package sc.learn.common.util;

 interface ThriftClient {

	Object createProxy();
	
	void bindNewInstance(String serviceName,String ip,int port,int timeout);
}
