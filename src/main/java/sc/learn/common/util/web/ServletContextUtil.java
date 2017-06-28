package sc.learn.common.util.web;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;
/**
 * WEB应用上下文工具类
 */
public abstract class ServletContextUtil {

	public static ServletContext getServletContext(){
		return ContextLoader.getCurrentWebApplicationContext().getServletContext();
	}
}
