package sc.learn.common.exception;

import sc.learn.common.pojo.ServerExceptionCode;

public class SeverException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	
	private ServerExceptionCode code;
	public SeverException(String message,ServerExceptionCode code) {
        super(message);
        this.code=code;
    }
	
	
	@Override
	public String getMessage() {
		return code+":"+super.getMessage();
	}
	

}
