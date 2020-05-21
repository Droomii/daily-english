package poly.service;

import poly.dto.NLPDTO;

public interface INewsService {

	NLPDTO nlpAndSaveNews(String newsTitle, String inputText) throws Exception;

}
