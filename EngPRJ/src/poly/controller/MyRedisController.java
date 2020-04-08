package poly.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.service.IMyRedisService;

@Controller
public class MyRedisController {

	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "MyRedisService")
	IMyRedisService myRedisService;
	
	@ResponseBody
	@RequestMapping(value = "myRedis/test")
	public String myRedis(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".myRedis start");
			
		myRedisService.doSaveData();
		
		log.info(this.getClass().getName() + ".myRedis end");
		
		return "success!!";
	}
}
