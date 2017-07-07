package sc.learn.common.thrift;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftInvocationHandler implements InvocationHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(ThriftInvocationHandler.class);

    private GenericObjectPool<ThriftTSocket> pool; // 连接池
    private TServiceClientFactory<TServiceClient> tServiceClientFactory;
    private Integer protocol;
    private ThriftServiceStatus thriftServiceStatus;// 服务状态

    public ThriftInvocationHandler(GenericObjectPool<ThriftTSocket> pool,
            TServiceClientFactory<TServiceClient> tServiceClientFactory, Integer protocol, String serviceName,
            AddressProvider addressProvider) {
        this.pool = pool;
        this.tServiceClientFactory = tServiceClientFactory;
        this.protocol = protocol;
        this.thriftServiceStatus = new ThriftServiceStatus(serviceName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        String logPrefix = "ThriftInvocationHandler_";
        ThriftTSocket thriftTSocket = null;
        boolean ifBorrowException = true;
        try {
            // 服务处于“切服”状态时 直接返回null
            if (!this.thriftServiceStatus.ifServiceUsable()) {
                return null;
            }

            // 当第三方服务不可用时，会阻塞在这里一定时间后抛出异常，并进行服务状态统计
            thriftTSocket = this.pool.borrowObject();
            ifBorrowException = false;

            String interfaceWholeName = this.getInterfaceName(method) + "&ip=" + thriftTSocket.getHostThrift() + ":"
                    + thriftTSocket.getPortThrift();
            LOGGER.info(logPrefix + interfaceWholeName + " borrowed:" + this.pool.getNumActive() + "  idle:"
                    + this.pool.getNumIdle() + " total :" + (this.pool.getNumActive() + this.pool.getNumIdle()));

            long startTime = System.currentTimeMillis();
            long costTime;
            Object o = null;
            try {
                o = method.invoke(this.tServiceClientFactory.getClient(this.getTProtocol(thriftTSocket)), args);
                costTime = System.currentTimeMillis() - startTime;
                LOGGER.info(this.getUrl(interfaceWholeName, args) + "|200|0|" + costTime + "|0");
            } catch (Exception e) {
                costTime = System.currentTimeMillis() - startTime;
                LOGGER.error(this.getUrl(interfaceWholeName, args) + "|000|0|" + costTime + "|1");
                // 抛出异常的连接认为不可用，从池中remove掉
                this.pool.invalidateObject(thriftTSocket);
                thriftTSocket = null;
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
            if (thriftTSocket != null) {
                this.pool.returnObject(thriftTSocket);
            }
        }
    }

    private String getInterfaceName(Method method) {
        String interfaceName = method.getDeclaringClass().toString();
        interfaceName = interfaceName.substring(10, interfaceName.length());
        return interfaceName + "$" + method.getName();
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

    private TProtocol getTProtocol(TSocket tSocket) {
        // 服务端均为非阻塞类型
        TTransport transport = new TFramedTransport(tSocket);
        TProtocol tProtocol = null;
        switch (this.protocol) {
        case 1:
            tProtocol = new TBinaryProtocol(transport);
            break;
        case 2:
            tProtocol = new TCompactProtocol(transport);
            break;
        case 3:
            tProtocol = new TJSONProtocol(transport);
            break;
        case 4:
            tProtocol = new TSimpleJSONProtocol(transport);
            break;
        default:
            tProtocol = new TBinaryProtocol(transport);
        }
        return tProtocol;
    }
}