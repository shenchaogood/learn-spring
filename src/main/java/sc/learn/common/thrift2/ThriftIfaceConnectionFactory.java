package sc.learn.common.thrift2;


public class ThriftIfaceConnectionFactory extends AbstractThriftConnectionFactory<ThriftIfaceTransport> {

    protected ThriftIfaceConnectionFactory(AddressProvider addressProvider,int timeout)  {
        super(addressProvider, timeout, false);
    }

}