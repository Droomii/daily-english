package poly.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NLPController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value = "lemmatize")
	@ResponseBody
	public String lemmatize(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".lemmatize start");
		
		String sentence = request.getParameter("sentence");
		
		

		log.info(this.getClass().getName() + ".lemmatize end");
		return null;
	}
	
	
}
