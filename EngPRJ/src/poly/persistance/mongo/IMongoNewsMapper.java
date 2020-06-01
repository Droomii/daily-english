package poly.persistance.mongo;

import poly.dto.NewsDTO;

public interface IMongoNewsMapper {

	boolean insertNews(NewsDTO rDTO) throws Exception;
	
	void createCollection() throws Exception;

	NewsDTO getLatestNews() throws Exception;

	NewsDTO getNews(int i) throws Exception;


}
