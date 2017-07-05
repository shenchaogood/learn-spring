package sc.learn.common.thrift;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class ThriftTSocket extends TSocket {

    private String hostThrift;//连接的ip
    private int portThrift;//连接的port
    private int timeoutThrift;//连接设置的timeout时长

    public ThriftTSocket(String host, int port, int timeout) throws TTransportException {
        super(host, port, timeout);
        this.hostThrift = host;
        this.portThrift = port;
        this.timeoutThrift = timeout;
    }

    public String getHostThrift() {
        return this.hostThrift;
    }

    public int getPortThrift() {
        return this.portThrift;
    }

    public int getTimeoutThrift() {
        return this.timeoutThrift;
    }

}