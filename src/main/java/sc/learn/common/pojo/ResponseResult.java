package sc.learn.common.pojo;

import org.apache.commons.lang3.ArrayUtils;

public class ResponseResult<T> {
	
	public static <T> ResponseResult<T> createSuccess(String...desc){
		return new ResponseResult<T>(true).setDesc(ArrayUtils.isEmpty(desc)?"":desc[0]);
	}
	
	public static <T> ResponseResult<T> createFail(String...desc){
		return new ResponseResult<T>(false).setDesc(ArrayUtils.isEmpty(desc)?"":desc[0]);
	}
	
	public static <T> ResponseResult<T> create(boolean success,String...desc){
		return new ResponseResult<T>(success).setDesc(ArrayUtils.isEmpty(desc)?"":desc[0]);
	}
	
	private ResponseResult() {
	}
	
	private ResponseResult(boolean success) {
		this.success = success;
	}
	
	private boolean success;
	
	private String desc;
	
	private T data;

	public T getData() {
		return data;
	}

	public ResponseResult<T> setData(T data) {
		this.data = data;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public ResponseResult<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public ResponseResult<T> setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	@Override
	public String toString() {
		return "ResponseResult [success=" + success + ", desc=" + desc + ", data=" + data + "]";
	}
}
