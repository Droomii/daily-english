package poly.persistance.mongo;

import java.util.List;

import poly.dto.WordDTO;

public interface IMongoNewsWordMapper {

	void insertWords(List<WordDTO> words) throws Exception;
	
	void createCollection() throws Exception;

}
