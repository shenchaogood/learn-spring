package sc.learn.common.util;

import com.alibaba.fastjson.JSON;

public abstract class JsonUtil {

	
	public static String toJSONString(Object bean){
		return JSON.toJSONString(bean);
	}
	
	
	public static <T> T parseJSONString(String json,Class<T> type){
		return JSON.parseObject(json, type);
	}
	
	
}
