package sc.learn.manage.web.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MethodPrintIntercepter extends HandlerInterceptorAdapter {
	private static final Logger LOGGER=LoggerFactory.getLogger(MethodPrintIntercepter.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod=(HandlerMethod)handler;
		LOGGER.debug("执行方法：{}",handlerMethod);
		return super.preHandle(request, response, handler);
	}
}
