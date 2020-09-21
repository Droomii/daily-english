package poly.controller;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.NewsDTO;
import poly.dto.WordQuizDTO;
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
		NewsDTO news = newsService.getLatestNews();
		newsWordService.highlightWords(news);

		model.addAttribute("news", news);

		log.info(this.getClass().getName() + ".getNews end");
		return "/news/latestNews";
	}

	@RequestMapping(value = "translateNews")
	@ResponseBody
	public List<String> translateNews(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			ModelMap model) throws Exception {
		log.info(this.getClass().getName() + ".translateNews start");

		NewsDTO news = newsService.getLatestNews();
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
	@RequestMapping(value = "bfs")
	public String bfs(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".bfs start");
		Set<String> catSet = WebCrawler.categoryBFS();
		Set<String> articleSet = new HashSet<>();
		for (String cat : catSet) {
			Set<String> newSet = WebCrawler.articleBFS(articleSet, cat);
			Queue<String> articleQueue = new ArrayDeque<>();
			articleQueue.addAll(newSet);
			while (!articleQueue.isEmpty()) {
				String article = articleQueue.poll();
				Document doc = null;
				try {
					doc = WebCrawler.connectToArticle(article);
				} catch (IOException e) {
					continue;
				}
				NewsDTO nDTO = new NewsDTO(WebCrawler.crawlHerald(doc, article), false);
				Pattern p = Pattern.compile("[가-힣]");
				Matcher m = p.matcher(nDTO.getNewsTitle());
				if(nDTO.getOriginalSentences().size()!=0 && !m.find())
					newsService.insertNews(nDTO);
				log.info(nDTO.getNewsTitle());
				Set<String> newArticles = WebCrawler.getArticles(articleSet, doc);
				System.out.println("newArticles : " + newArticles);
				articleQueue.addAll(newArticles);
			}
		}

		log.info(this.getClass().getName() + ".bfs end");
		return "success";
	}

}
