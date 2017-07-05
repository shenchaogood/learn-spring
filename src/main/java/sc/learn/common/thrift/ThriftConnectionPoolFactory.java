package sc.learn.common.thrift;

import java.net.InetSocketAddress;
import java.util.Iterator;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftConnectionPoolFactory extends BasePoolableObjectFactory<ThriftTSocket> {
    private static Logger LOGGER = LoggerFactory.getLogger(ThriftConnectionPoolFactory.class);

    private final AddressProvider addressProvider;
    private int timeout = 2000;

    protected ThriftConnectionPoolFactory(AddressProvider addressProvider) throws Exception {
        this.addressProvider = addressProvider;
    }

    @Override
    public ThriftTSocket makeObject() throws Exception {
        String logPrefix = "makeObject_";
        ThriftTSocket thriftTSocket = null;
        InetSocketAddress address = null;
        Exception exception = null;
        try {
            address = this.addressProvider.selectOne();
            thriftTSocket = new ThriftTSocket(address.getHostName(), address.getPort(), timeout);
            thriftTSocket.open();
            LOGGER.info(logPrefix + "connect server:[" + address.getHostName() + ":" + address.getPort() + "] success");
        } catch (Exception e) {
            LOGGER.error(logPrefix + "connect server[" + address.getHostName() + ":" + address.getPort() + "] error: ",
                    e);
            exception = e;
            thriftTSocket = null;// 这里是为了下面连接其他服务器
        }
        // 轮循所有ip
        if (thriftTSocket == null) {
            String hostName = address.getHostName();
            int port = address.getPort();
            Iterator<InetSocketAddress> addressIterator = this.addressProvider.addressIterator();
            while (addressIterator.hasNext()) {
                try {
                    address = addressIterator.next();
                    // 不再尝试连接之前已经连接失败的主机
                    if (address.getHostName().equals(hostName) && address.getPort() == port) {
                        continue;
                    }
                    thriftTSocket = new ThriftTSocket(address.getHostName(), address.getPort(), timeout);
                    thriftTSocket.open();
                    LOGGER.info(logPrefix + "connect server:[" + address.getHostName() + ":" + address.getPort()
                            + "] success");
                    break;
                } catch (Exception e) {
                    LOGGER.error(logPrefix + "connect server[" + address.getHostName() + ":" + address.getPort()
                            + "] error: ", e);
                    exception = e;
                    thriftTSocket = null;
                }
            }
        }
        // 所有服务均无法建立连接时抛出异常
        if (thriftTSocket == null) {
            throw exception;
        }
        return thriftTSocket;

    }

    @Override
    public void destroyObject(ThriftTSocket tsocket) throws Exception {
        if (tsocket != null) {
            try {
                tsocket.close();
            } catch (Exception e) {
            }
        }

    }

    @Override
    public boolean validateObject(ThriftTSocket tsocket) {
        if (tsocket == null) {
            return false;
        }
        // 在成功创建连接后，将网络断掉，这里调用还是true
        return tsocket.isOpen();
    }
}