package poly.persistance.redis.impl;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import poly.dto.QuizInfoDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.redis.IRedisNewsWordMapper;

@Component("RedisNewsWordMapper")
public class RedisNewsWordMapper implements IRedisNewsWordMapper {

	@Autowired
	private RedisTemplate<String, Object> redisDB;

	Logger log = Logger.getLogger(this.getClass());
	
	private static final String COL_NM = "todayQuiz";
	
	@Override
	public void saveTodayWordToRedis(List<WordQuizDTO> pList) throws Exception {


		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
		
		if(redisDB.hasKey(COL_NM)) {
			log.info("todayQuiz key exists!!");
			redisDB.delete(COL_NM);
		}
		
		Iterator<WordQuizDTO> quiz = pList.iterator();
		
		WordQuizDTO wDTO = null;
		
		while(quiz.hasNext()) {
			
			wDTO = quiz.next();
			log.info("inserting : " + wDTO.getLemma());
			
			redisDB.opsForList().rightPush(COL_NM, wDTO);
		}

		Calendar c = Calendar.getInstance();
		
		// to tomorrow
		c.add(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 7);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		redisDB.expireAt(COL_NM, c.getTime());
		
	}

	@Override
	public WordQuizDTO getTodayQuiz() throws Exception {
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
		
		
		return (WordQuizDTO)redisDB.opsForList().index(COL_NM, 0);
	}

	@Override
	public boolean submitTodayQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));

		int quizIntIdx = Integer.parseInt(quizIdx);
		String key = "quizInfo_" + user_seq;
		
		// get quiz word by index
		WordQuizDTO rDTO = (WordQuizDTO)redisDB.opsForList().index(COL_NM, quizIntIdx);

		// get user quiz info
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		QuizInfoDTO qDTO = (QuizInfoDTO)redisDB.opsForValue().get(key);
		if(qDTO==null) {
			qDTO = new QuizInfoDTO();
		}
		boolean res;
		if(rDTO.getAnswer().equalsIgnoreCase(answer)) {
			res =  true;
		}else {
			res =  false;
		}
		
		qDTO.setUser_seq(user_seq);
		qDTO.getAnsweredQs().put(quizIntIdx, res);
		redisDB.opsForValue().set(key, qDTO);
		
		Calendar c = Calendar.getInstance();
		
		// to tomorrow
		c.add(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 7);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		redisDB.expireAt(key, c.getTime());
		return res;
		
	}
	
	
	
	
}
