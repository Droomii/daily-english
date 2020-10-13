package poly.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import poly.dto.NewsDTO;
import poly.dto.WordDTO;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.service.impl.NewsService;

@Controller
public class NewsWordController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	@Resource(name = "NewsService")
	INewsService newsService;
	
	// test
	@RequestMapping(value = "newsWord/insertWords")
	@ResponseBody
	public String insertWords(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertWords start");

		// map instance for words
		Map<String, WordDTO> wordMap = new HashMap<String, WordDTO>();

		// toeic words
		addWords("c:/toeic_word.txt", "TOEIC", wordMap);
		
		// bsl
		addWords("c:/bsl.txt", "Business", wordMap);
		
		// ngsl
		addWords("c:/ngsl.txt", "General", wordMap);
		
		// nawl
		addWords("c:/nawl.txt", "Academic", wordMap);
		
		// stop words
		removeStopWords("c:/stop_words.txt", wordMap);
		
		List<WordDTO> wordList = new ArrayList<WordDTO>(wordMap.values());
		
		newsWordService.insertWords(wordList);
		
		wordList = null;
		
		log.info(this.getClass().getName() + ".insertWords end");
		return "success";
	}
	
	// test
	@RequestMapping(value = "insertMeaning")
	@ResponseBody
	public String insertMeaning(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertMeaning start");

		newsWordService.insertMeaning();
		log.info(this.getClass().getName() + ".insertMeaning end");
		return "success";
	}
	
	// test
	@RequestMapping(value = "extractWords")
	@ResponseBody
	public List<Map<String, Object>> extractWords(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".extractWords start");

		NewsDTO pDTO = newsService.getTodayNews();
		List<Map<String, Object>> rList = newsWordService.extractWords(pDTO);
		
		log.info(this.getClass().getName() + ".extractWords end");
		return rList;
	}
	
	@RequestMapping(value = "saveLatestQuiz")
	@ResponseBody
	public String saveLatestQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveLatestQuiz start");

		NewsDTO news = newsService.getTodayNews();
		newsWordService.saveTodayWordToRedis(news);
		
		log.info(this.getClass().getName() + ".saveLatestQuiz end");
		return "success";
	}
	
	@RequestMapping(value = "saveAllQuiz")
	@ResponseBody
	public String saveAllQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".saveLatestQuiz start");

		try {
			int i = 0;
			while(true) {
				NewsDTO news = newsService.getNews(i);
				newsWordService.saveTodayWordToRedis(news);
				i++;
			}
		}catch (Exception e) {}
		
		
		log.info(this.getClass().getName() + ".saveLatestQuiz end");
		return "success";
	}
	private void removeStopWords(String path, Map<String, WordDTO> wordMap) throws IOException {
		
		File f = new File(path);
		BufferedReader bf = new BufferedReader(new FileReader(f));
		
		String line;
		while((line = bf.readLine()) != null) {
			String[] words = line.split("\\t");
			for(String word : words) {
				if(!word.trim().replaceAll("\\t", "").matches("^[a-zA-Z0-9]+$"))
					continue;
				
				if(wordMap.containsKey(line)) {
					wordMap.remove(line);
				}
			}
			
			
		}
		bf.close();
	}


	private void addWords(String path, String pool, Map<String, WordDTO> words) throws IOException {
		
		File f = new File(path);
		BufferedReader bf = new BufferedReader(new FileReader(f));
		
		String line;
		WordDTO pDTO = null;
		while((line = bf.readLine()) != null) {
			WordDTO rDTO = words.get(line);
			if(rDTO==null) {
				pDTO = new WordDTO(line, pool);
				words.put(line, pDTO);
			}else {
				rDTO.addPool(pool);
			}
		}
		
		pDTO = null;
		bf.close();
	}
	
}
