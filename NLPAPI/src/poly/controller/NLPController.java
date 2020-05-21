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

import poly.dto.NLPDTO;
import poly.service.INLPService;

@Controller
public class NLPController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NLPService")
	INLPService nlpService;
	
	@RequestMapping(value = "lemmatize")
	@ResponseBody
	public NLPDTO lemmatize(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".lemmatize start");
		
		String inputText = request.getParameter("inputText");
		
		NLPDTO rDTO = nlpService.process(inputText);

		log.info(this.getClass().getName() + ".lemmatize end");
		return null;
	}
	
	
}
