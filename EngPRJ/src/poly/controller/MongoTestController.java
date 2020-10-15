package poly.controller;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.persistance.mongo.IMongoNewsMapper;
import poly.service.IMongoTestService;

@Controller
public class MongoTestController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "MongoTestService")
	IMongoTestService mongoTestService;
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;
	
	@RequestMapping(value = "mongo/test")
	@ResponseBody
	public String test(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".test start");

		mongoTestService.createCollection();
		
		log.info(this.getClass().getName() + ".test end");
		return "success";
	}
	
	@RequestMapping(value = "insertWords")
	@ResponseBody
	public String insertWords(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertWords start");
		
		mongoTestService.insertWords();

		log.info(this.getClass().getName() + ".insertWords end");
		return "success";
	}
	@ResponseBody
	@RequestMapping(value = "updateTest")
	public String updateTest(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".updateTest start");
		Set<String> a = new HashSet<>();
		a.add("hello");
		mongoNewsMapper.incrementDf(a);
		
		log.info(this.getClass().getName() + ".updateTest end");
		return "success";
	}
}
