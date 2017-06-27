package sc.learn.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import sc.learn.common.util.EnvironmentUtil;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{RootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{WebConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement("/tmp/upload"));
	}
	
	@Override
	protected Filter[] getServletFilters() {
		//HttpPutFormContentFilter <form action="" method="put" enctype="application/x-www-form-urlencoded">
		return new Filter[]{new CharacterEncodingFilter("UTF-8"),new HttpPutFormContentFilter()};
	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
//		System.setProperty("spring.profiles.active", EnvironmentUtil.getLocalEnviromentType().name());
//		 context.getEnvironment().setActiveProfiles(EnvironmentUtil.getLocalEnviromentType().name());
		servletContext.setInitParameter("spring.profiles.default", EnvironmentUtil.getLocalEnviromentType().name());
		servletContext.addListener(RequestContextListener.class);
		super.onStartup(servletContext);
	}
	
}
