package poly.persistance.mongo;

import java.util.List;
import java.util.Map;

import poly.dto.WordDTO;

public interface IMongoNewsWordMapper {

	void insertWords(List<WordDTO> words) throws Exception;
	
	void createCollection(String colNm) throws Exception;

	Map<String, List<String>> getWordPool() throws Exception;


}
