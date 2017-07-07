package sc.learn.common.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import sc.learn.common.thrift.AddressProvider;
import sc.learn.common.thrift.ThriftGenericObjectPool;
import sc.learn.common.thrift.ThriftInvocationHandler;
//http://blog.csdn.net/chen7253886/article/details/52779471
public class ThriftServiceClientProxyFactory implements FactoryBean<Object>, InitializingBean {
    /**
     * 接口的完整路径
     */
    private String service;

    /**
     * 连接池
     */
    private ThriftGenericObjectPool thriftGenericObjectPool;

    private Object proxyClient;

    private Class<Object> objectClass;
    
    /**
     * 传输协议
     * 1.TBinaryProtocol – 二进制格式.
     * 2.TCompactProtocol – 压缩格式
     * 3.TJSONProtocol – JSON格式
     * 4.TSimpleJSONProtocol –提供JSON只写协议, 生成的文件很容易通过脚本语言解析。
     */
    private Integer protocol;

    private AddressProvider addressProvider;

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        // 加载第三方提供的接口和Client.Factory类
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        objectClass = (Class<Object>) classLoader.loadClass(this.service + "$Iface");
        Class<TServiceClientFactory<TServiceClient>> tServiceClientFactoryClass = (Class<TServiceClientFactory<TServiceClient>>) classLoader
                .loadClass(this.service + "$Client$Factory");
        // 设置创建handler
        InvocationHandler clientHandler = new ThriftInvocationHandler(this.thriftGenericObjectPool,
                tServiceClientFactoryClass.newInstance(), this.protocol, this.service, this.addressProvider);
        this.proxyClient = Proxy.newProxyInstance(classLoader, new Class[] { objectClass }, clientHandler);
    }

    @Override
    public Object getObject() throws Exception {
        return this.proxyClient;
    }

    @Override
    public Class<?> getObjectType() {
        return this.objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setAddressProvider(AddressProvider addressProvider) {
        this.addressProvider = addressProvider;
    }

    public void setThriftGenericObjectPool(ThriftGenericObjectPool thriftGenericObjectPool) {
        this.thriftGenericObjectPool = thriftGenericObjectPool;
    }

}