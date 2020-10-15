package poly.controller;

import java.util.HashMap;
import java.util.List;
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

import poly.dto.WordQuizDTO;
import poly.service.INewsWordService;

@Controller
public class WordCardController {

	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;

	
	
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value = "today/todayWordCard")
	public String todayWordCard(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq = "1";
		}
		log.info(this.getClass().getName() + ".todayWordCard start");
		WordQuizDTO qDTO = newsWordService.getRandomTodayQuiz(user_seq);
		if(qDTO.getIdx()!=-1) {
			String msg = "오늘의 퀴즈를 완료하지 않았습니다";
			String url = "/today/todayQuiz.do";
			model.addAttribute("msg", msg);
			model.addAttribute("url", url);
			return "/redirect";
		}

		log.info(this.getClass().getName() + ".todayWordCard end");
		return "/today/todayWordCard";
	}
	
	@RequestMapping(value = "today/getWrongWords", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<Map<String, String>>> getWrongWords(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getWrongWords start");
		String user_seq = (String) session.getAttribute("user_seq");
		if(user_seq==null) {
			user_seq = "1";
		}
		
		log.info(this.getClass().getName() + ".getWrongWords end");
		Map<String, List<Map<String, String>>> rMap = new HashMap<>();
		rMap.put("res", newsWordService.getTodayWrongWords(user_seq));
		return rMap;
	}
}
