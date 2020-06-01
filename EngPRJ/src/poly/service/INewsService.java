package poly.service;

import poly.dto.NewsDTO;

public interface INewsService {

	NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception;

	NewsDTO getLatestNews() throws Exception;
	
	void scheduleCrawl() throws Exception;

	NewsDTO getNews(int i) throws Exception;

}
