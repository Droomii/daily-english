package poly.service.impl;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.service.INewsService;
import poly.service.INewsWordService;
import poly.util.WebCrawler;

@Service("NewsService")
public class NewsService implements INewsService{
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception {
		

	    NewsDTO rDTO = new NewsDTO(newsTitle, inputText, newsUrl);
	    boolean saved = mongoNewsMapper.insertNews(rDTO);
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
	
}
