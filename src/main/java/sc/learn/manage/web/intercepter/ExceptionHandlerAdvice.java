package sc.learn.manage.web.intercepter;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.ExceptionUtil;
import sc.learn.manage.util.Constants;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
	
	@ExceptionHandler(RuntimeException.class)  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseResult handleRuntimeException(HttpServletRequest request, Exception ex){
		LOGGER.error(ExceptionUtil.getStackTrace(ex));
		return ResponseResult.createFail(Constants.INTERNAL_SERVER_ERROR);
    }  
      
    @ExceptionHandler(SQLException.class)  
    public ModelAndView handSQLException(Exception ex){
    	LOGGER.error(ExceptionUtil.getStackTrace(ex));
        ModelAndView mv = new ModelAndView();
        return mv;  
    }  
}
