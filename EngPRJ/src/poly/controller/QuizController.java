package poly.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping(value = "today")
@Controller
public class QuizController {

	
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value = "todayQuiz")
	public String todayQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todayQuiz start");

		log.info(this.getClass().getName() + ".todayQuiz end");
		return "/today/todayQuiz";
	}
}