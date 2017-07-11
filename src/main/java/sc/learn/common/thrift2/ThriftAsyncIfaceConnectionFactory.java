package sc.learn.common.thrift2;


public class ThriftAsyncIfaceConnectionFactory extends AbstractThriftConnectionFactory<ThriftAsyncIfaceTransport> {

    protected ThriftAsyncIfaceConnectionFactory(AddressProvider addressProvider,int timeout){
    	super(addressProvider, timeout, false);  
    }

    
}