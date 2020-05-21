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
import poly.service.INewsService;
import poly.util.WebCrawler;

@Controller
public class NewsController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsService")
	INewsService newsService;
	
	
	
	@RequestMapping(value = "nlpForm")
	public String nlpForm(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".nlpForm start");

		log.info(this.getClass().getName() + ".nlpForm end");
		return "/nlpForm";
	}
	
	@RequestMapping(value = "saveNews")
	@ResponseBody
	public NLPDTO doNLP(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".lemmatize start");
		
		
		String title = null;
		String inputText = request.getParameter("inputText");
		if(inputText==null) {
			String[] crawlRes = WebCrawler.crawlHerald();
			title = crawlRes[0];
			inputText = crawlRes[1];
		}
		
		NLPDTO rDTO = newsService.nlpAndSaveNews(title, inputText);
		

		log.info(this.getClass().getName() + ".lemmatize end");
		return rDTO;
	}
	
	
}
