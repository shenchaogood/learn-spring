package sc.learn.common.thrift2;

import java.net.InetSocketAddress;
import java.util.Iterator;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractThriftConnectionPoolFactory<T extends TTransport> extends BasePoolableObjectFactory<T> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final AddressProvider addressProvider;
    private int timeout;
    private boolean isIface;

    protected AbstractThriftConnectionPoolFactory(AddressProvider addressProvider,int timeout,boolean isIface) throws Exception {
        this.addressProvider = addressProvider;
        this.timeout=timeout;
        this.isIface=isIface;
    }

    @SuppressWarnings("unchecked")
	@Override
    public T makeObject() throws Exception {
    	Class<T> socketClass=(Class<T>)getClass().getGenericSuperclass();
    	if(socketClass==null){
    		throw new ThriftException("必须指明AbstractThriftConnectionPoolFactory类的泛型");
    	}
        String logPrefix = "makeObject_"+socketClass.getSimpleName()+"_";
        T thriftTSocket = null;
        InetSocketAddress address = null;
        Exception exception = null;
        try {
            address = this.addressProvider.selectOne();
            thriftTSocket =  socketClass.getConstructor(String.class,int.class,int.class).newInstance(address.getHostName(), address.getPort(), timeout);
            if(isIface){
            	thriftTSocket.open();
            }
            LOGGER.info(logPrefix + "connect server:[" + address.getHostName() + ":" + address.getPort() + "] success");
        } catch (Exception e) {
            LOGGER.error(logPrefix + "connect server[" + address.getHostName() + ":" + address.getPort() + "] error: ", e);
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
                    thriftTSocket = socketClass.getConstructor(String.class,int.class,int.class).newInstance(address.getHostName(), address.getPort(), timeout);
                    if(isIface){
                    	thriftTSocket.open();
                    }
                    LOGGER.info(logPrefix + "connect server:[" + address.getHostName() + ":" + address.getPort()+ "] success");
                    break;
                } catch (Exception e) {
                    LOGGER.error(logPrefix + "connect server[" + address.getHostName() + ":" + address.getPort() + "] error: ", e);
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
    public void destroyObject(T tsocket) throws Exception {
        if (tsocket != null) {
            try {
                tsocket.close();
            } catch (Exception e) {
            }
        }

    }

    @Override
    public boolean validateObject(T tsocket) {
        if (tsocket == null) {
            return false;
        }
        // 在成功创建连接后，将网络断掉，这里调用还是true
        return tsocket.isOpen();
    }
}