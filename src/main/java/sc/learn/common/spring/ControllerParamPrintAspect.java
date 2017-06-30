package sc.learn.common.spring;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ControllerParamPrintAspect {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ControllerParamPrintAspect.class);
	
    @Pointcut("execution(* sc.learn.*.web.controller.*.*(..))")  
    private void anyMethod(){}//定义一个切入点  
      
    @Around("anyMethod()")  
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable{
    	LOGGER.debug("执行方法:{}",pjp.getSignature());
    	LOGGER.debug("方法参数:{}",Arrays.toString(pjp.getArgs()));
		try {
			Object object = pjp.proceed();
			LOGGER.debug("方法结果:{}",object);
	        LOGGER.debug("方法参数:{}",Arrays.toString(pjp.getArgs()));
	        return object;
		} catch (Throwable e) {
			LOGGER.debug("方法异常:{}",e.getLocalizedMessage());
			throw e;
		}
    }  
}  