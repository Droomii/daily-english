package poly.persistance.redis;

import java.util.List;
import java.util.Map;

import poly.dto.NewsDTO;
import poly.dto.WordQuizDTO;

public interface IRedisNewsWordMapper {

	void saveTodayWordToRedis(List<WordQuizDTO> rList, String newsUrl) throws Exception;

	WordQuizDTO getTodayQuiz(String user_seq) throws Exception;
	
	Map<String, String> submitTodayQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception;

	void putReviewWordToRedis(String user_seq, List<WordQuizDTO> quizList) throws Exception;

	boolean hasKey(String colNm) throws Exception;

	Map<String, String> submitReviewQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception;

	WordQuizDTO getReviewQuiz(String user_seq) throws Exception;

	List<String> getTodayWrongWords(String user_seq) throws Exception;

	void saveTodayTTS() throws Exception;

	List<Map<String, Object>> getTodaySentences() throws Exception;

	String getTodayNewsUrl() throws Exception;

	void saveTodayNewsUrl(String headlineUrl) throws Exception;

	void saveTodayNews(NewsDTO headline) throws Exception;

	void resetQuiz(String user_seq) throws Exception;

}
