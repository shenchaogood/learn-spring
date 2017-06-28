package sc.learn.manage.web.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.JsonUtil;
import sc.learn.common.util.web.ServletUtil;
import sc.learn.common.web.HttpSessionProvider;
import sc.learn.manage.util.Constants;
import sc.learn.manage.vo.UserVo;

public class PrivilegeIntercepter extends HandlerInterceptorAdapter {
	private static final Logger LOGGER=LoggerFactory.getLogger(PrivilegeIntercepter.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserVo user=ContextLoader.getCurrentWebApplicationContext().getBean(HttpSessionProvider.class).getAttibute(Constants.CURRENT_LOGIN_USER, UserVo.class);
		String contextPath=request.getContextPath();
		String uri =request.getRequestURI();
		String privilegeUrl=uri.substring(contextPath.length());
		LOGGER.debug("访问路径:{}",privilegeUrl);
		if (user == null) {
			if (privilegeUrl.equals("/manage/user/login")) {
				return true;
			} else {
				 response.setContentType(ServletUtil.JSON_TYPE+"; charset=utf-8");
				 response.getWriter().append(JsonUtil.toJSONString(ResponseResult.createFail(Constants.NO_LOGIN))).flush();
			}
		}else {
			if (user.getPrivileges().stream().anyMatch((privilege)->privilege.getUrl().equals(privilegeUrl))) {
				return true;
			} else {
				response.getWriter().append(JsonUtil.toJSONString(ResponseResult.createFail(Constants.NO_PERMISION))).flush();
			}
		}
		return false;
	}
}
