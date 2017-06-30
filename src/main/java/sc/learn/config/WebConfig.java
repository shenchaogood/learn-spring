package sc.learn.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import sc.learn.common.spring.ControllerParamPrintAspect;
import sc.learn.manage.web.intercepter.PrivilegeIntercepter;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan("sc.learn.*.web")
public class WebConfig extends WebMvcConfigurerAdapter{

	@Bean
	public ViewResolver viewResolver(){
		return new InternalResourceViewResolver("/WEB-INF/views", ".jsp");
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public MultipartResolver multipartResolver() throws IOException{
		/**
		 * 这种方式无需DispatcherServletInitializer中
		 * registration.setMultipartConfig(new MultipartConfigElement("/tmp/upload"));
		CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8");
		multipartResolver.setUploadTempDir(new FileSystemResource("/tmp/upload"));
		multipartResolver.setMaxUploadSize(4096000);
		**/
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping(){
		RequestMappingHandlerMapping handlerMapping=new RequestMappingHandlerMapping();
		handlerMapping.setInterceptors(new PrivilegeIntercepter());
		return handlerMapping;
	}
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(){
		RequestMappingHandlerAdapter handlerAdapter=new RequestMappingHandlerAdapter();
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		//<!-- 这里顺序不能写反，一定要先写text/html，否则IE下会出现下载提示 -->  
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML,MediaType.APPLICATION_JSON));
		handlerAdapter.getMessageConverters().add(0,converter);
		return handlerAdapter;
	}
	
	@Bean
	public ControllerParamPrintAspect controllerParamPrintAspect(){
		return new ControllerParamPrintAspect();
	}
}
