package sc.learn.common.pojo;

public class ResponseResult {
	
	public static final ResponseResult SUCCESS=new ResponseResult(true,"");
	public static final ResponseResult FAIL=new ResponseResult(false,"");
	
	public ResponseResult(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	private boolean success;
	
	private String error;

	public boolean isSuccess() {
		return success;
	}

	public ResponseResult setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public String getError() {
		return error;
	}

	public ResponseResult setError(String error) {
		this.error = error;
		return this;
	}
	
	

}
