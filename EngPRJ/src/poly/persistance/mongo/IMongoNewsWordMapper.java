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

	List<Map<String, Object>> getReviewWords(String user_seq) throws Exception;

	WordQuizDTO getRandomUsage(String word) throws Exception;

	void updateCorrectCounter(Map<String, String> rMap) throws Exception;

	void insertMeaning() throws Exception;

	List<Map<String, String>> getWrongWordMeaning(List<String> wrongWords) throws Exception;



}
