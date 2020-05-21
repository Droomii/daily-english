package poly.persistance.mongo;

import poly.dto.NLPDTO;

public interface IMongoNewsMapper {

	void insertNews(NLPDTO rDTO) throws Exception;
	
	void createCollection() throws Exception;

}
