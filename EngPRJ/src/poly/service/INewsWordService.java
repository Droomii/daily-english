package poly.service;

import java.util.List;
import java.util.Map;

import poly.dto.NewsDTO;
import poly.dto.WordDTO;
import poly.dto.WordQuizDTO;

public interface INewsWordService {

	void insertWords(List<WordDTO> wordList) throws Exception;

	List<Map<String, Object>> extractWords(NewsDTO pDTO) throws Exception;
	
	void loadWordPool() throws Exception;

	void highlightWords(NewsDTO news) throws Exception;
	
	void saveTodayWordToRedis(NewsDTO news) throws Exception;

	WordQuizDTO getRandomTodayQuiz(String user_seq) throws Exception;

	Map<String, String> submitTodayQuizAnswer(String user_seq, String user_lvl, String quizIdx, String answer) throws Exception;

	void putReviewWordToRedis(String user_seq) throws Exception;

	Map<String, String> submitReviewQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception;

	WordQuizDTO getRandomReviewQuiz(String user_seq) throws Exception;

	List<Map<String, String>> getTodayWrongWords(String user_seq) throws Exception;

	void insertMeaning() throws Exception;

	void saveTodayTTS() throws Exception;

	List<Map<String, Object>> getTodaySentences() throws Exception;

	void resetQuiz(String user_seq) throws Exception;
	

}
