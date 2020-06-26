package poly.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.TestInfoDTO;
import poly.dto.TestWordDTO;
import poly.persistance.mapper.IUserMapper;
import poly.service.ITestWordService;
import poly.service.IUserService;
import poly.util.SessionUtil;

@Controller
public class TestWordController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "TestWordService")
	ITestWordService testWordService;
	
	@Resource(name = "UserMapper")
	IUserMapper userMapper;
	
	@Resource(name = "UserService")
	IUserService UserService;
	
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
		
		ModelMap sessionModel = SessionUtil.verify(session, model);
		if(sessionModel != null) {
			model = sessionModel;
			return "/redirect";
		}
		String user_lvl = (String) session.getAttribute("user_lvl");
		if(user_lvl != null) {
			String url = "/index.do";
			String msg = "이미 실력을 측정하였습니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		
		
		log.info(this.getClass().getName() + ".randomTest end");
		return "/wordTest/wordTest";
	}
	
	@RequestMapping(value = "submitTestAnswer", method = RequestMethod.POST)
	@ResponseBody
	public TestWordDTO randomWord(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".randomWord start");
		
		String user_seq = (String) session.getAttribute("user_seq");
		
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
			log.info("rDTO.getFinalLevel() : " + rDTO.getFinalLevel());
			log.info("user_seq : " + user_seq);
			session.setAttribute("user_lvl", rDTO.getFinalLevel());
			UserService.updateUserLvl(user_seq, rDTO.getFinalLevel());
		}
		
		log.info(this.getClass().getName() + ".randomWord end");
		return rDTO;
	}
	
	@RequestMapping(value = "insertTestInfo", method = RequestMethod.POST)
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
