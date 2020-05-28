package poly.persistance.redis;

import java.util.List;

import poly.dto.WordQuizDTO;

public interface IRedisNewsWordMapper {

	void saveTodayWordToRedis(List<WordQuizDTO> rList) throws Exception;

	WordQuizDTO getTodayQuiz() throws Exception;

}
