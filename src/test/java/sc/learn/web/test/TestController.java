package sc.learn.web.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sc.learn.test.mapper.Test;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping("/mvc")
	public Test testMvc(){
		return new Test();
	}

}
