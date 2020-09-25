package poly.service;

import java.util.Set;

import poly.dto.NewsDTO;

public interface INewsService {

	NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception;

	NewsDTO getLatestNews() throws Exception;
	
	void scheduleCrawl() throws Exception;

	NewsDTO getNews(int i) throws Exception;

	void insertNews(NewsDTO nDTO) throws Exception;

	void crawlAll() throws Exception;

	String saveRelatedArticles() throws Exception;

	void saveLatestNews() throws Exception;

	Set<String> findNotIn() throws Exception;

}
