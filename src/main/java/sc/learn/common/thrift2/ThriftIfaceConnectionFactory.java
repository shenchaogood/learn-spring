package sc.learn.common.thrift2;


public class ThriftIfaceConnectionFactory extends AbstractThriftConnectionFactory<ThriftIfaceTransport> {

    protected ThriftIfaceConnectionFactory(AddressProvider addressProvider,int timeout) throws Exception {
        super(addressProvider, timeout, false);
    }

}