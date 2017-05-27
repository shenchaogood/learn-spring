package sc.learn.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
	
	class P{
		String name="lisi";
	}
	@RequestMapping("/mvc")
	public Object testMvc(){
		
		P p=new P();
		p.name="ww";
		return p;
	}

}
