package sc.learn.common.spring;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import sc.learn.common.thrift.ThriftProtocolEnum;
import sc.learn.common.util.StringUtil;
import sc.learn.common.util.ThriftUtil;

public class ThriftServiceProxyFactory<IFACE> implements FactoryBean<IFACE>, InitializingBean {

    private IFACE proxyClient;

    private int timeout;
    
    private CuratorFramework zkClient;
    
    private Class<IFACE> ifaceClass;
    
    /**
     * 传输协议
     * 1.TBinaryProtocol – 二进制格式.
     * 2.TCompactProtocol – 压缩格式
     * 3.TJSONProtocol – JSON格式
     * 4.TSimpleJSONProtocol –提供JSON只写协议, 生成的文件很容易通过脚本语言解析。
     */
    private ThriftProtocolEnum protocol;

    @Override
    public void afterPropertiesSet() throws Exception {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        if(triple.getLeft()){
//        	objectClass = (Class<IFACE>) classLoader.loadClass(service + ThriftUtil.Constants.IFACE_SUFFIX);
//        }else{
//        	objectClass = (Class<IFACE>) classLoader.loadClass(service + ThriftUtil.Constants.ASYN_IFACE_SUFFIX);
//        }
//    	ParameterizedType c=(ParameterizedType)getClass().getGenericInterfaces()[0];
//    	System.out.println(c.getActualTypeArguments()[0]);
//    	Class<IFACE> objectClass= (Class<IFACE>)((ParameterizedType)getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]; 
//        Triple<Boolean,String,String> triple=ThriftUtil.fetchSynchronizedAndIfacePathAndServiceName(objectClass);
        proxyClient =ThriftUtil.createClient(ifaceClass, timeout, protocol,zkClient);
    }
    
    @Override
    public IFACE getObject() throws Exception {
        return proxyClient;
    }

    @Override
    public Class<IFACE> getObjectType() {
        return ifaceClass;
    }

    public void setIfaceClass(Class<IFACE> ifaceClass) {
		this.ifaceClass = ifaceClass;
	}

	@Override
    public boolean isSingleton() {
        return true;
    }

    public void setProtocol(String protocol) {
    	if(StringUtil.isNotBlank(protocol)){
    		this.protocol = ThriftProtocolEnum.valueOf(protocol);
    	}
    }

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}