package poly.persistance.mongo;

import java.util.List;
import java.util.Map;

import poly.dto.WordDTO;
import poly.dto.WordQuizDTO;

public interface IMongoNewsWordMapper {

	void insertWords(List<WordDTO> words) throws Exception;
	
	void createCollection(String colNm) throws Exception;

	Map<String, List<String>> getWordPool() throws Exception;

	void saveWordUsage(List<WordQuizDTO> rList) throws Exception;

	void insertQuizRecord(Map<String, String> rMap) throws Exception;

	void insertReviewWord(Map<String, String> rMap) throws Exception;


}
