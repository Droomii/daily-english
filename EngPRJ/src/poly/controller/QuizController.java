package poly.controller;

import java.util.Map;

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

import poly.dto.NewsDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.mongo.IMongoUserMapper;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.util.SessionUtil;


@Controller
public class QuizController {

	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	@Resource(name = "NewsService")
	INewsService newsService;
	
	@Resource(name = "MongoUserMapper")
	IMongoUserMapper mongoUserMapper;
	
	@RequestMapping(value = "today/todayQuiz")
	public String todayQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todayQuiz start");
		ModelMap sessionModel = SessionUtil.verify(session, model);
		if(sessionModel != null) {
			model = sessionModel;
			return "/redirect";
		}
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq = "1";
		}
		
		String user_lvl = (String) session.getAttribute("user_lvl");
		log.info("user_lvl : " + user_lvl);
		if(user_lvl == null) {
			String url = "/wordTest/takeTest.do";
			String msg = "처음 가입 후 실력 측정 테스트가 필요합니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		
		WordQuizDTO qDTO = null;
		
		try {
			qDTO = newsWordService.getRandomTodayQuiz(user_seq);
			
			}catch (IllegalArgumentException e) {
				
				NewsDTO news = newsService.getTodayNews();
				newsWordService.saveTodayWordToRedis(news);
				qDTO = newsWordService.getRandomTodayQuiz(user_seq);
				
			}
		
		if(qDTO.getIdx()==-1) {
			String url = "/today/todayWordCard.do";
			String msg = "오늘의 퀴즈를 이미 풀었습니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		
		log.info(this.getClass().getName() + ".todayQuiz end");
		return "/today/todayQuiz";
	}
	
	@RequestMapping(value = "today/submitTodayQuizAnswer", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> submitTodayQuizAnswer(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".submitTodayQuizAnswer start");

		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq="1";
		}
		
		String user_lvl = (String) session.getAttribute("user_lvl");
		if(user_lvl==null) {
			user_lvl = "5";
		}
		
		String quizIdx = request.getParameter("quizIdx");
		String answer = request.getParameter("answer");
		log.info("quizIdx : " + quizIdx);
		log.info("answer : " + answer);
		Map<String, String> rMap = newsWordService.submitTodayQuizAnswer(user_seq, user_lvl, quizIdx, answer);
		
		log.info("answer : " + (rMap.get("result").equals("1") ? "right" : "wrong"));
		rMap.put("originalSentence", rMap.get("answerSentence"));
		
		log.info(this.getClass().getName() + ".submitTodayQuizAnswer end");
		return rMap;
	}
	
	@RequestMapping(value = "today/getRandomTodayQuiz", method = RequestMethod.POST)
	@ResponseBody
	public WordQuizDTO getRandomTodayQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getRandomTodayQuiz start");
		
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq = "1";
		}
		
		WordQuizDTO qDTO = null;
		
		try {
		qDTO = newsWordService.getRandomTodayQuiz(user_seq);
		
		if(qDTO.getIdx()==-1) {
			mongoUserMapper.insertAttend(user_seq);
		}
		
		
		}catch (IllegalArgumentException e) {
			
			NewsDTO news = newsService.getTodayNews();
			newsWordService.saveTodayWordToRedis(news);
			qDTO = newsWordService.getRandomTodayQuiz(user_seq);
			
		}
		log.info("qDTO : " + qDTO);
		log.info(this.getClass().getName() + ".getRandomTodayQuiz end");
		return qDTO;
	}
	
	@RequestMapping(value = "review/reviewQuiz")
	public String reviewQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".reviewQuiz start");
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq == null) {
			return "/login";
		}
		
		String user_lvl = (String) session.getAttribute("user_lvl");
		log.info("user_lvl : " + user_lvl);
		if(user_lvl == null) {
			String url = "/wordTest/takeTest.do";
			String msg = "처음 가입 후 실력 측정 테스트가 필요합니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}
		newsWordService.putReviewWordToRedis(user_seq);
		
		log.info(this.getClass().getName() + ".reviewQuiz end");
		return "/review/reviewQuiz";
	}
	
	@RequestMapping(value = "review/submitReviewQuizAnswer", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> submitReviewQuizAnswer(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".submitReviewQuizAnswer start");

		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq="1";
		}
		
		String quizIdx = request.getParameter("quizIdx");
		String answer = request.getParameter("answer");
		log.info("quizIdx : " + quizIdx);
		log.info("answer : " + answer);
		Map<String, String> rMap = newsWordService.submitReviewQuizAnswer(user_seq, quizIdx, answer);
		
		log.info("answer : " + (rMap.get("result").equals("1") ? "right" : "wrong"));
		rMap.put("originalSentence", rMap.get("answerSentence"));
		
		log.info(this.getClass().getName() + ".submitReviewQuizAnswer end");
		return rMap;
	}
	
	@RequestMapping(value = "review/getRandomReviewQuiz", method = RequestMethod.POST)
	@ResponseBody
	public WordQuizDTO getRandomReviewQuiz(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, ModelMap model) throws Exception {
		log.info(this.getClass().getName() + ".getRandomReviewQuiz start");

		String user_seq = (String) session.getAttribute("user_seq");
		if (user_seq == null) {
			user_seq = "1";
		}

		WordQuizDTO qDTO = null;
		try {
			qDTO = newsWordService.getRandomReviewQuiz(user_seq);
		} catch (IllegalArgumentException e) {
			WordQuizDTO rDTO = new WordQuizDTO();
			rDTO.setIdx(-1);
			return rDTO;
		}
		log.info("qDTO : " + qDTO);
		log.info(this.getClass().getName() + ".getRandomReviewQuiz end");
		return qDTO;
	}


}
