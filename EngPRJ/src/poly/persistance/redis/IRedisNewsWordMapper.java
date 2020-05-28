package poly.persistance.redis;

import java.util.List;
import java.util.Map;

import poly.dto.WordQuizDTO;

public interface IRedisNewsWordMapper {

	void saveTodayWordToRedis(List<WordQuizDTO> rList) throws Exception;

	WordQuizDTO getTodayQuiz(String user_seq) throws Exception;
	
	Map<String, String> submitTodayQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception;

}
