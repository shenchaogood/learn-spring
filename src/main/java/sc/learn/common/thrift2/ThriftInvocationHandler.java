package sc.learn.common.thrift2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.util.ThriftUtil;

public class ThriftInvocationHandler<IFACE> 
		implements InvocationHandler {
	
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private AbstractThriftTransportPool<TTransport> pool; // 连接池
    private boolean isSynchronized;
    private String serviceName;
    private ThriftProtocolEnum protocol;
    private ThriftServiceStatus thriftServiceStatus;// 服务状态

    public ThriftInvocationHandler(AbstractThriftTransportPool<TTransport> pool,
    		String serviceName, boolean isSynchronized,ThriftProtocolEnum protocol) {
        this.pool = pool;
        this.isSynchronized=isSynchronized;
        this.protocol = protocol;
        this.serviceName=serviceName;
        this.thriftServiceStatus = new ThriftServiceStatus(serviceName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        String logPrefix = "ThriftInvocationHandler_";
        boolean ifBorrowException = true;
        TTransport transport=null;
        try {
            // 服务处于“切服”状态时 直接返回null
            if (!thriftServiceStatus.ifServiceUsable()) {
                return null;
            }
            // 当第三方服务不可用时，会阻塞在这里一定时间后抛出异常，并进行服务状态统计
            transport = pool.borrowObject();
            ifBorrowException = false;
            String interfaceWholeName;
            if(transport instanceof AcquirableThriftAddress){
            	AcquirableThriftAddress thriftAddress=(AcquirableThriftAddress)transport;
            	interfaceWholeName = getInterfaceName(method) + "&ip=" + thriftAddress.getThriftHost() + ":"
                        + thriftAddress.getThriftPort();
            }else{
            	interfaceWholeName = getInterfaceName(method) + "&ip=unknow" + ":unknow";
            }
            LOGGER.info(logPrefix + interfaceWholeName + " borrowed:" + this.pool.getNumActive() + "  idle:"
                    + this.pool.getNumIdle() + " total :" + (this.pool.getNumActive() + this.pool.getNumIdle()));
            long startTime = System.currentTimeMillis();
            long costTime;
            Object o = null;
            try {
            	Object target;
            	Class<?> clientClass;
            	TProtocolFactory protocolFactory = ThriftUtil.getTProtocolFactory(protocol);
            	if(isSynchronized){
            		clientClass=getClass().getClassLoader().loadClass(serviceName+ThriftUtil.Constants.CLIENT_SUFFIX);
        	        TProtocol protocol =protocolFactory.getProtocol(transport);
        	        transport.open();
        			Constructor<?> syncConstructor = clientClass.getConstructor(TProtocol.class);
        			target=syncConstructor.newInstance(protocol);
            	}else{
            		clientClass=getClass().getClassLoader().loadClass(serviceName+ThriftUtil.Constants.ASYN_CLIENT_SUFFIX);
            		TAsyncClientManager clientManager = new TAsyncClientManager();
        	        Constructor<?> asynConstructor = clientClass.getConstructor(TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class);
        	        target = asynConstructor.newInstance(protocolFactory, clientManager, transport);
            	}
                o = method.invoke(target, args);
                costTime = System.currentTimeMillis() - startTime;
                LOGGER.info(getUrl(interfaceWholeName, args) + "|200|0|" + costTime + "|0");
            } catch (Exception e) {
                costTime = System.currentTimeMillis() - startTime;
                LOGGER.error(getUrl(interfaceWholeName, args) + "|000|0|" + costTime + "|1");
                // 抛出异常的连接认为不可用，从池中remove掉
                pool.invalidateObject(transport);
                transport = null;
                o = null;
            }
            return o;
        } catch (Exception e) {
            LOGGER.error("thrift invoke error", e);
            if (ifBorrowException) {
                this.thriftServiceStatus.checkThriftServiceStatus();
            }
            return null;
        } finally {
            if (transport != null) {
                this.pool.returnObject(transport);
            }
        }
    }

    private String getInterfaceName(Method method) {
        String interfaceName = method.getDeclaringClass().getName();
        return interfaceName + "." + method.getName();
    }

    private String getUrl(String service, Object[] args) {
        StringBuilder wholeUrl = new StringBuilder("thrift://");
        wholeUrl.append(service.substring(service.lastIndexOf("$") + 1, service.indexOf("&ip="))).append("/?")
                .append("service=").append(service);
        if (args != null) {
            wholeUrl.append("&allParams=[ ");
            for (Object object : args) {
                wholeUrl.append(object);
            }
            wholeUrl.append(" ]");
        }
        return wholeUrl.toString();
    }
}