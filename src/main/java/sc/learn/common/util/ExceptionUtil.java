package sc.learn.common.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

public abstract class ExceptionUtil extends ExceptionUtils{
	
	public static void rethrow(Throwable throwable,Class<RuntimeException> class1) {
		if(class1.isAssignableFrom(throwable.getClass())){
			ExceptionUtils.rethrow(throwable);
		}
    }
}
