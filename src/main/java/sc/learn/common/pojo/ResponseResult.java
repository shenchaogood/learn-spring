package sc.learn.common.pojo;

import sc.learn.common.util.ArrayUtil;

public class ResponseResult {
	
	public static ResponseResult createSuccess(String...desc){
		return new ResponseResult(true).setDesc(ArrayUtil.isEmpty(desc)?"":desc[0]);
	}
	
	public static ResponseResult createFail(String...desc){
		return new ResponseResult(false).setDesc(ArrayUtil.isEmpty(desc)?"":desc[0]);
	}
	
	public static ResponseResult create(boolean success,String...desc){
		return new ResponseResult(success).setDesc(ArrayUtil.isEmpty(desc)?"":desc[0]);
	}
	
	private ResponseResult() {
	}
	
	private ResponseResult(boolean success) {
		this.success = success;
	}
	
	private boolean success;
	
	private String desc;
	
	private Object data;

	public Object getData() {
		return data;
	}

	public ResponseResult setData(Object data) {
		this.data = data;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public ResponseResult setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public ResponseResult setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	@Override
	public String toString() {
		return "ResponseResult [success=" + success + ", desc=" + desc + "]";
	}
}
