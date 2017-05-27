package sc.learn.common.util.web;

import javax.servlet.ServletContext;
/**
 * WEB应用上下文工具类
 */
public abstract class ServletContextUtil {

	private static ServletContext SERVLET_CONTEXT;
	public static ServletContext getServletContext(){
		return SERVLET_CONTEXT;
	}
	
	public synchronized static void setServletContext(ServletContext servletContext){
		if(SERVLET_CONTEXT==null){
			SERVLET_CONTEXT=servletContext;
		}
	}
}
