package sc.learn.common.thrift2;


public class ThriftIfaceConnectionPoolFactory extends AbstractThriftConnectionPoolFactory<ThriftIfaceTransport> {

    protected ThriftIfaceConnectionPoolFactory(AddressProvider addressProvider,int timeout) throws Exception {
        super(addressProvider, timeout, false);
    }

}