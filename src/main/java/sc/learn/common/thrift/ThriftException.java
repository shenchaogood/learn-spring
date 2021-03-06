package sc.learn.common.thrift;

public class ThriftException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ThriftException(String message) {
        super(message);
    }
	
	public ThriftException(Throwable cause) {
        super(cause);
    }

}
