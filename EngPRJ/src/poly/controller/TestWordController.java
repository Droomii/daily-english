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

import poly.dto.TestInfoDTO;
import poly.dto.TestWordDTO;
import poly.persistance.mapper.IUserMapper;
import poly.service.ITestWordService;

@Controller
public class TestWordController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "TestWordService")
	ITestWordService testWordService;
	
	@Resource(name = "UserMapper")
	IUserMapper userMapper;
	
	
	// 실력 측정용 단어 레디스에 저장하는 메서드
	@RequestMapping(value = "saveWordToRedis")
	@ResponseBody
	public String saveWordToRedis(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveWordToRedis start");

		testWordService.saveTestWords();
		
		log.info(this.getClass().getName() + ".saveWordToRedis end");
		return "success";
	}
	
	@RequestMapping(value = "wordTest/takeTest")
	public String randomTest(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".randomTest start");
		
		log.info(this.getClass().getName() + ".randomTest end");
		return "/wordTest/wordTest";
	}
	
	@RequestMapping(value = "submitTestAnswer")
	@ResponseBody
	public TestWordDTO randomWord(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".randomWord start");
		
		String index = request.getParameter("index");
		String answer = request.getParameter("answer");
		
		log.info("answer : " + answer);
		log.info("index : " + index);
		
		TestWordDTO rDTO = null;
		if(index==null) {
			rDTO = testWordService.getRandomWord();			
		}else {
			rDTO = testWordService.submitTestAnswer(index, answer);
		}
		
		if(rDTO.getFinalLevel()!=null) {
			
		}
		
		log.info(this.getClass().getName() + ".randomWord end");
		return rDTO;
	}
	
	@RequestMapping(value = "insertTestInfo")
	@ResponseBody
	public TestInfoDTO insertTestInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertTestInfo start");

		String userNo = request.getParameter("userNo");
		if(userNo==null) {
			userNo = "1";
		}
		
		TestInfoDTO tDTO = testWordService.getTestInfo(userNo);
		
		log.info(this.getClass().getName() + ".insertTestInfo end");
		return tDTO;
	}
}
