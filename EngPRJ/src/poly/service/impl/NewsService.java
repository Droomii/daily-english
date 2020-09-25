package poly.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mongodb.DBCursor;

import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.service.ITfIdfService;
import poly.util.UrlUtil;
import poly.util.WebCrawler;

@Service("NewsService")
public class NewsService implements INewsService{
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	@Resource(name = "TfIdfService")
	ITfIdfService tfIdfService;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception {
		

	    NewsDTO rDTO = new NewsDTO(newsTitle, inputText, newsUrl, true);
	    boolean saved = mongoNewsMapper.insertNews(rDTO, true);
	    if(saved) {
	    	newsWordService.saveTodayWordToRedis(rDTO);
	    }
	    
	    return rDTO;
		
	}

	@Override
	public NewsDTO getLatestNews() throws Exception {
		NewsDTO rDTO = mongoNewsMapper.getLatestNews();
		return rDTO;
	}
	
	
	@Override
	//@Scheduled(cron="0 0 7 ? * *")
	public void scheduleCrawl() throws Exception{
		
		log.info(this.getClass().getName() + ".scheduleCrawl start");
		String[] crawlRes = WebCrawler.crawlHerald();
		String title = crawlRes[0];
		String inputText = crawlRes[1];
		String newsUrl = crawlRes[2];
		nlpAndSaveNews(title, inputText, newsUrl);
		log.info(this.getClass().getName() + ".scheduleCrawl end");
		
	}

	@Override
	public NewsDTO getNews(int i) throws Exception {
		NewsDTO rDTO = mongoNewsMapper.getNews(i);
		return rDTO;
	}

	@Override
	public void insertNews(NewsDTO nDTO) throws Exception {
		mongoNewsMapper.insertNews(nDTO, false);
		
	}

	@Override
	public void crawlAll() throws Exception {
		Set<String> articleSet = new HashSet<>();
		DBCursor existingArticles = mongoNewsMapper.getAllArticles();
		while(existingArticles.hasNext()) {
			articleSet.add((String)existingArticles.next().get("newsUrl"));
		}
		int np = 14647;
		try {
			np = mongoNewsMapper.getNp();
		}catch(Exception e) {}
		
		log.info(np);
		
		for(int i = np; i > 0; i--) {
			Set<String> newSet = WebCrawler.getArticleInPage(articleSet, i);
			for(String newArticle : newSet) {
				try {
					log.info("crawling " + newArticle);
					String[] news = WebCrawler.crawlHerald(newArticle);
					log.info("title : " + news[0]);
					Pattern p = Pattern.compile("[가-힣]");
					Matcher m = p.matcher(news[0]);
					if(m.find()) continue;
					NewsDTO nDTO = new NewsDTO(news, false);
					
					if(nDTO.getOriginalSentences().isEmpty()) continue;
					
					mongoNewsMapper.insertNews(nDTO, false);
					
				}catch(Exception e){
					log.info("deleted article");
					continue;
				}
			}
			mongoNewsMapper.addNp(i);
		}
	}

	@Override
	public void saveLatestNews() throws Exception {
		log.info(this.getClass().getName() + ".saveLatestNews start");
		Set<String> articleSet = new HashSet<>();

		List<NewsDTO> newArticles = new ArrayList<>();
		int np = 1;
		boolean duplicate = false;
		while(!duplicate) {
			Set<String> newSet = WebCrawler.getArticleInPage(articleSet, np++);
			if(newSet.isEmpty()) break;
			for(String newArticle : newSet) {
				try {
					log.info("crawling " + newArticle);
					String[] news = WebCrawler.crawlHerald(newArticle);
					log.info("title : " + news[0]);
					Pattern p = Pattern.compile("[가-힣]");
					Matcher m = p.matcher(news[0]);
					if(m.find()) continue;
					NewsDTO nDTO = new NewsDTO(news, false);
					
					if(nDTO.getOriginalSentences().isEmpty()) continue;
					
					if(mongoNewsMapper.newsExists(nDTO)) {
						log.info("news exists");
						duplicate = true;
						break;
					}
					
					newArticles.add(nDTO);
				}catch(Exception e){
					log.info("deleted article");
					continue;
				}
			}
		}
		if(!newArticles.isEmpty()) {
			Collections.reverse(newArticles);
			mongoNewsMapper.insertNews(newArticles, false);
			tfIdfService.insertNewArticles(newArticles);			
		}
		
		
	}
	
	@Override
	public String saveRelatedArticles() throws Exception {
		
		String requestURL = "http://192.168.136.132:5000/saveRelatedArticles";
		
		return UrlUtil.request(requestURL, false);
	}

	@Override
	public Set<String> findNotIn() throws Exception {
		Set<String> articleSet = new HashSet<>();
		DBCursor existingArticles = mongoNewsMapper.getAllArticles();
		while(existingArticles.hasNext()) {
			articleSet.add((String)existingArticles.next().get("newsUrl"));
		}
		
		Set<String> analyzedSet = new HashSet<>();
		DBCursor analyzedArticles = mongoNewsMapper.getTfIdf();
		while(analyzedArticles.hasNext()) {
			analyzedSet.add((String)analyzedArticles.next().get("newsUrl"));
		}
		articleSet.removeAll(analyzedSet);
		
		return articleSet;
	}

	
	
}
