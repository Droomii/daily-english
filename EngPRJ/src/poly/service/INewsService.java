package poly.service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import poly.dto.NewsDTO;

public interface INewsService {

	NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception;

	NewsDTO getTodayNews() throws Exception;
	
	void scheduleCrawl() throws Exception;

	NewsDTO getNews(int i) throws Exception;

	void insertNews(NewsDTO nDTO) throws Exception;

	void crawlAll() throws Exception;

	String saveRelatedArticles() throws Exception;

	void saveLatestNews() throws Exception;

	Set<String> findNotIn() throws Exception;

	void tfIdfTodayNews() throws Exception;

	List<NewsDTO> getRelatedArticles(String newsUrl) throws Exception;

	void saveHeadlineNews() throws Exception;

	JSONObject scoreTranslate(HttpServletRequest request) throws Exception;


}
