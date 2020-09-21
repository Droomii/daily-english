package poly.persistance.mongo;

import java.util.List;

import poly.dto.NewsDTO;

public interface IMongoNewsMapper {

	boolean insertNews(NewsDTO rDTO, boolean translate) throws Exception;
	
	void createCollection() throws Exception;

	NewsDTO getLatestNews() throws Exception;

	NewsDTO getNews(int i) throws Exception;

	List<NewsDTO> getAllArticles() throws Exception;


}
