package poly.service;

import java.util.List;

import poly.dto.NewsDTO;

public interface INewsService {

	NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception;

	NewsDTO getLatestNews() throws Exception;

}
