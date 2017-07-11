package sc.learn.common.thrift;


public class ThriftIfaceConnectionFactory extends AbstractThriftConnectionFactory<ThriftIfaceTransport> {

    protected ThriftIfaceConnectionFactory(AddressProvider addressProvider,int timeout)  {
        super(addressProvider, timeout, true);
    }

}