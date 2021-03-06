package poly.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.NewsDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.redis.IRedisNewsWordMapper;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.util.SessionUtil;
import poly.util.TranslateUtil;
import poly.util.WebCrawler;

@Controller
public class NewsController {

	Logger log = Logger.getLogger(this.getClass());

	@Resource(name = "NewsService")
	INewsService newsService;

	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;

	@Resource(name = "RedisNewsWordMapper")
	IRedisNewsWordMapper redisNewsWordMapper;
	
	
	@RequestMapping(value = "nlpForm")
	public String nlpForm(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".nlpForm start");

		log.info(this.getClass().getName() + ".nlpForm end");
		return "/nlpForm";
	}



	@RequestMapping(value = "today/todayNews")
	public String getLatestNews(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			ModelMap model) throws Exception {
		log.info(this.getClass().getName() + ".getNews start");
		ModelMap sessionModel = SessionUtil.verify(session, model);
		if (sessionModel != null) {
			model = sessionModel;
			return "/redirect";
		}
		String user_lvl = (String) session.getAttribute("user_lvl");
		String user_seq = (String) session.getAttribute("user_seq");
		log.info("user_lvl : " + user_lvl);
		if (user_lvl == null) {
			String url = "/wordTest/takeTest.do";
			String msg = "처음 가입 후 실력 측정 테스트가 필요합니다.";
			model.addAttribute("url", url);
			model.addAttribute("msg", msg);
			return "/redirect";
		}

		WordQuizDTO qDTO = newsWordService.getRandomTodayQuiz(user_seq);
		if (qDTO.getIdx() != -1) {
			model.addAttribute("url", "/index.do");
			model.addAttribute("msg", "오늘의 퀴즈를 풀지 않았습니다.");
			return "/redirect";
		}
		NewsDTO news = newsService.getTodayNews();
		newsWordService.highlightWords(news);
		List<NewsDTO> relatedArticles = newsService.getRelatedArticles(news.getNewsUrl());
		model.addAttribute("news", news);
		
		String imgUrl = WebCrawler.getImageUrl(news.getNewsUrl());
		model.addAttribute("imgUrl", imgUrl);
		
		model.addAttribute("relatedArticles", relatedArticles);
		log.info("relatedArticles.size() : " + relatedArticles.size());
		log.info(this.getClass().getName() + ".getNews end");
		return "/news/latestNews";
	}

	@RequestMapping(value = "translateNews")
	@ResponseBody
	public List<String> translateNews(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			ModelMap model) throws Exception {
		log.info(this.getClass().getName() + ".translateNews start");

		NewsDTO news = newsService.getTodayNews();
		List<String> res = TranslateUtil.translateNews(news);

		log.info(this.getClass().getName() + ".translateNews end");
		return res;
	}

	@RequestMapping(value = "doTTS")
	@ResponseBody
	public String doTTS(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".doTTS start");

		newsWordService.saveTodayTTS();

		log.info(this.getClass().getName() + ".doTTS end");
		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "crawlAll")
	public String crawlAll(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".crwalAll start");
		newsService.crawlAll();
		
		log.info(this.getClass().getName() + ".bfs end");
		return "success";
	}
	
	@ResponseBody
	@RequestMapping(value = "saveRelatedArticles")
	public String saveRelatedArticles(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveRelatedArticles start");
		newsService.saveRelatedArticles();
		
		log.info(this.getClass().getName() + ".saveRelatedArticles end");
		return "success";
	}
	
	@RequestMapping(value = "saveNews")
	@ResponseBody
	public NewsDTO doNLP(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".lemmatize start");

		String title = null;
		String inputText = request.getParameter("inputText");
		String newsUrl = null;
		if (inputText == null) {
			String[] crawlRes = WebCrawler.crawlHerald();
			title = crawlRes[0];
			inputText = crawlRes[1];
			newsUrl = crawlRes[2];
			log.info("inputText : " + inputText);
		}

		NewsDTO rDTO = newsService.nlpAndSaveNews(title, inputText, newsUrl);
		newsWordService.saveTodayTTS();

		log.info(this.getClass().getName() + ".lemmatize end");
		return rDTO;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "saveLatestNews")
	public String saveLatestNews(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveLatestNews start");

		newsService.saveLatestNews();
		newsService.saveHeadlineNews();
		newsService.saveRelatedArticles();
		newsWordService.saveTodayTTS();
		log.info(this.getClass().getName() + ".saveLatestNews end");
		return "success";
	}
	
	@RequestMapping(value = "saveHeadline")
	@ResponseBody
	public String saveHeadline(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveHeadline start");

		newsService.saveHeadlineNews();
		log.info(this.getClass().getName() + ".saveHeadline end");
		return "success";
	}
	
	@RequestMapping(value = "tfIdfTodayNews")
	@ResponseBody
	public String tfIdfTodayNews(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".tfIdfTodayNews start");
		newsService.tfIdfTodayNews();
		
		log.info(this.getClass().getName() + ".tfIdfTodayNews end");
		return "success";
	}
	
	@ResponseBody
	@RequestMapping(value = "notIn")
	public Set<String> notIn(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".notIn start");

		Set<String> notIn = newsService.findNotIn();
		
		log.info(this.getClass().getName() + ".notIn end");
		return notIn;
	}
	
	@RequestMapping(value = "insertNewsRedis")
	@ResponseBody
	public String insertNewsRedis(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertNewsRedis start");

		NewsDTO nDTO = newsService.getTodayNews();
		redisNewsWordMapper.saveTodayNews(nDTO);
		log.info(this.getClass().getName() + ".insertNewsRedis end");
		return "success";
	}
	
	@RequestMapping(value = "today/todayTranslate")
	public String todayTranslate(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".todayTranslate start");

		NewsDTO nDTO = newsService.getTodayNews();
		model.addAttribute("nDTO", nDTO);
		
		log.info(this.getClass().getName() + ".todayTranslate end");
		return "today/todayTranslate";
	}
	
	@RequestMapping(value = "today/scoreTranslate")
	@ResponseBody
	public Map<String, Object> scoreTranslate(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".scoreTranslate start");
		
		JSONObject res = newsService.scoreTranslate(request);
		
		log.info(this.getClass().getName() + ".scoreTranslate end");
		return res.toMap();
	}

}
