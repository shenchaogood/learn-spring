package sc.learn.test.common;

import org.junit.Test;

import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.JsonUtil;

public class TestJson {
	
	@Test
	public void testSerial(){
		String json=JsonUtil.toJSONString(ResponseResult.createSuccess());
		System.out.println(json);
		ResponseResult r=JsonUtil.parseJSONString(json, ResponseResult.class);
		System.out.println(r);
	}

}
