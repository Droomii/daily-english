package poly.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AudioController {

	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value = "today/todaySentence")
	public String todaySentence(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todaySentence start");

		
		log.info(this.getClass().getName() + ".todaySentence end");
		return "/today/todaySentence";
	}
}
