package poly.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.NewsDTO;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.util.TranslateUtil;
import poly.util.WebCrawler;

@Controller
public class NewsController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsService")
	INewsService newsService;
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	
	@RequestMapping(value = "nlpForm")
	public String nlpForm(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".nlpForm start");

		log.info(this.getClass().getName() + ".nlpForm end");
		return "/nlpForm";
	}
	
	@RequestMapping(value = "saveNews")
	@ResponseBody
	public NewsDTO doNLP(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".lemmatize start");
		
		
		String title = null;
		String inputText = request.getParameter("inputText");
		String newsUrl = null;
		if(inputText==null) {
			String[] crawlRes = WebCrawler.crawlHerald();
			title = crawlRes[0];
			inputText = crawlRes[1];
			newsUrl = crawlRes[2];
			log.info("inputText : " + inputText);
		}
		
		NewsDTO rDTO = newsService.nlpAndSaveNews(title, inputText, newsUrl);
		

		log.info(this.getClass().getName() + ".lemmatize end");
		return rDTO;
	}
	
	@RequestMapping(value = "getLatestNews")
	public String getLatestNews(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getNews start");
		
		NewsDTO news = newsService.getLatestNews();
		newsWordService.highlightWords(news);

		model.addAttribute("news", news);
		
		log.info(this.getClass().getName() + ".getNews end");
		return "/news/latestNews";
	}
	
	@RequestMapping(value = "translateNews")
	@ResponseBody
	public List<String> translateNews(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".translateNews start");

		NewsDTO news = newsService.getLatestNews();
		List<String> res = TranslateUtil.translateNews(news);
		
		log.info(this.getClass().getName() + ".translateNews end");
		return res;
	}
	
}
