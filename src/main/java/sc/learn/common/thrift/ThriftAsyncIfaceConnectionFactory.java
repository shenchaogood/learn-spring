package sc.learn.common.thrift;


public class ThriftAsyncIfaceConnectionFactory extends AbstractThriftConnectionFactory<ThriftAsyncIfaceTransport> {

    protected ThriftAsyncIfaceConnectionFactory(AddressProvider addressProvider,int timeout){
    	super(addressProvider, timeout, false);  
    }

    
}