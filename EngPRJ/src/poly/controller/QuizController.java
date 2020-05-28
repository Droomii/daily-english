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

import poly.dto.WordQuizDTO;
import poly.service.INewsWordService;


@RequestMapping(value = "today")
@Controller
public class QuizController {

	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	
	@RequestMapping(value = "todayQuiz")
	public String todayQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todayQuiz start");

		log.info(this.getClass().getName() + ".todayQuiz end");
		return "/today/todayQuiz";
	}
	
	@RequestMapping(value = "submitTodayQuizAnswer")
	@ResponseBody
	public int submitTodayQuizAnswer(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".submitTodayQuizAnswer start");

		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq="1";
		}
		
		String quizIdx = request.getParameter("quizIdx");
		String answer = request.getParameter("answer");
		log.info("quizIdx : " + quizIdx);
		log.info("answer : " + answer);
		boolean res = newsWordService.submitTodayQuizAnswer(user_seq, quizIdx, answer);
		
		log.info("answer : " + (res ? "right" : "wrong"));
		
		log.info(this.getClass().getName() + ".submitTodayQuizAnswer end");
		return res ? 1 : 0;
	}
	
	@RequestMapping(value = "getRandomTodayQuiz")
	@ResponseBody
	public WordQuizDTO getRandomTodayQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getRandomTodayQuiz start");
		
		WordQuizDTO qDTO = newsWordService.getRandomTodayQuiz(); 
		log.info("qDTO : " + qDTO);
		log.info(this.getClass().getName() + ".getRandomTodayQuiz end");
		return qDTO;
	}
}
