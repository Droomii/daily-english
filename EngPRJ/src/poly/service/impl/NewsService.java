package poly.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mongodb.DBCursor;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.persistance.redis.IRedisNewsWordMapper;
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
	
	@Resource(name = "RedisNewsWordMapper")
	IRedisNewsWordMapper redisNewsWordMapper;
	
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
	public NewsDTO getTodayNews() throws Exception {
		String todayNewsUrl = redisNewsWordMapper.getTodayNewsUrl();
		log.info("todayNewsUrl : " + todayNewsUrl);
		try {
			NewsDTO rDTO = mongoNewsMapper.getNews(todayNewsUrl);
			return rDTO;
		}catch(NullPointerException e) {
			return null;
		}
		
	}
	
	
	@Override
	@Scheduled(cron="0 0 7 ? * *")
	public void scheduleCrawl() throws Exception{
		
		log.info(this.getClass().getName() + ".scheduleCrawl start");
		saveLatestNews();
		saveHeadlineNews();
		saveRelatedArticles();
		newsWordService.saveTodayTTS();
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
		String headlineUrl = WebCrawler.crawlHerald()[2];
		redisNewsWordMapper.saveTodayNewsUrl(headlineUrl);
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
		
		String requestURL = "http://localhost:5000/saveRelatedArticles";
		
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

	@Override
	public void tfIdfTodayNews() throws Exception {
		log.info(this.getClass().getName() + ".tfIdfTodayNews start");
		String todayNewsUrl = redisNewsWordMapper.getTodayNewsUrl();
		log.info("todayNewsUrl : " + todayNewsUrl);
		NewsDTO todayNews = mongoNewsMapper.getNews(todayNewsUrl);
		tfIdfService.insertNewArticles(Arrays.asList(todayNews));
		
		log.info(this.getClass().getName() + ".tfIdfTodayNews end");
		
	}

	@Override
	public List<NewsDTO> getRelatedArticles(String newsUrl) throws Exception {
		
		return mongoNewsMapper.getRelatedArticles(newsUrl);
	}

	@Override
	public void saveHeadlineNews() throws Exception {
		
		String todayNewsUrl = redisNewsWordMapper.getTodayNewsUrl();
		NewsDTO headline = new NewsDTO();
		headline.setNewsUrl(todayNewsUrl);
		if(mongoNewsMapper.newsExists(headline)) {
			headline = mongoNewsMapper.getNews(todayNewsUrl);
			headline.translate();
			mongoNewsMapper.updateNews(headline);
			newsWordService.saveTodayWordToRedis(headline);
			redisNewsWordMapper.saveTodayNews(headline);
		}
		
		
		
		
	}

	@Override
	public JSONObject scoreTranslate(HttpServletRequest request) throws Exception {
		String requestURL = "http://localhost:5000/scoreTranslate";
		
		int idx = Integer.parseInt(request.getParameter("idx"));
		NewsDTO news = getTodayNews();
		String original = news.getTranslation().get(idx);
		log.info("userAnswer : " + request.getParameter("userAnswer"));
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(requestURL);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userAnswer", request.getParameter("userAnswer")));
		params.add(new BasicNameValuePair("original", original));
		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());
		StringBuffer sb = new StringBuffer();
		if(resEntity != null) {
			try(InputStream instream = resEntity.getContent()){
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	sb.append(line);
		        }
			}
		}
		log.info("sb : " + sb);
		JSONObject res = new JSONObject();
		res.put("original", original);
		res.put("score", sb.toString());
		log.info("res : " + res);
		return res;
	}

	
	
}
