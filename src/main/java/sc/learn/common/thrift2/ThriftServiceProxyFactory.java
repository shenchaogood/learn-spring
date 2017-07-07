package sc.learn.common.thrift2;

import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import sc.learn.common.util.ThriftUtil;

public class ThriftServiceProxyFactory<IFACE> implements FactoryBean<IFACE>, InitializingBean {
    /**
     * 接口的完整路径
     */
    private String service;

    /**
     * 连接池
     */
    private AbstractThriftTransportPool<TTransport> pool;

    private IFACE proxyClient;

    private Class<IFACE> objectClass;
    
    private int timeout;
    
    /**
     * 传输协议
     * 1.TBinaryProtocol – 二进制格式.
     * 2.TCompactProtocol – 压缩格式
     * 3.TJSONProtocol – JSON格式
     * 4.TSimpleJSONProtocol –提供JSON只写协议, 生成的文件很容易通过脚本语言解析。
     */
    private ThriftProtocolEnum protocol;

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(pool.isSynchronized()){
        	objectClass = (Class<IFACE>) classLoader.loadClass(service + ThriftUtil.Constants.IFACE_SUFFIX);
        }else{
        	objectClass = (Class<IFACE>) classLoader.loadClass(service + ThriftUtil.Constants.ASYN_IFACE_SUFFIX);
        }
        proxyClient =ThriftUtil.createClient(objectClass, timeout, (servicePath)->null, protocol);
    }
    
    @Override
    public IFACE getObject() throws Exception {
        return proxyClient;
    }

    @Override
    public Class<IFACE> getObjectType() {
        return objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setProtocol(String protocol) {
        this.protocol = ThriftProtocolEnum.valueOf(protocol);
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setPool(AbstractThriftTransportPool<TTransport> pool) {
        this.pool = pool;
    }
}