package poly.persistance.mongo;

import poly.dto.NewsDTO;

public interface IMongoNewsMapper {

	void insertNews(NewsDTO rDTO) throws Exception;
	
	void createCollection() throws Exception;

	NewsDTO getNews() throws Exception;

}
