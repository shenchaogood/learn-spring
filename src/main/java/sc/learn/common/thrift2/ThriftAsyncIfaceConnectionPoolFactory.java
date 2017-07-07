package sc.learn.common.thrift2;


public class ThriftAsyncIfaceConnectionPoolFactory extends AbstractThriftConnectionPoolFactory<ThriftAsyncIfaceTransport> {

    protected ThriftAsyncIfaceConnectionPoolFactory(AddressProvider addressProvider,int timeout) throws Exception {
    	super(addressProvider, timeout, true);  
    }

    
}