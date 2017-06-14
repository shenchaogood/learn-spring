package sc.learn.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import sc.learn.web.TestIntercepter;

@Configuration
@EnableWebMvc
@ComponentScan("sc.learn")
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
		handlerMapping.setInterceptors(new TestIntercepter());
		return handlerMapping;
	}
	
}
