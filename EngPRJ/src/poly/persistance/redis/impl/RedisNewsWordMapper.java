package poly.persistance.redis.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
	private static final String QUIZ_INFO_PREFIX = "quizInfo_";
	private static final String REVIEW_WORD_PREFIX = "reviewWord_";
	private static final String REVIEW_INFO_PREFIX = "reviewInfo_";
	private static List<WordQuizDTO> quizList = null;
	private static Random r = new Random();

	@Override
	public void saveTodayWordToRedis(List<WordQuizDTO> pList) throws Exception {

		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));

		if (redisDB.hasKey(COL_NM)) {
			log.info("todayQuiz key exists!!");
			redisDB.delete(COL_NM);
		}

		quizList = pList;
		Iterator<WordQuizDTO> quiz = pList.iterator();

		WordQuizDTO wDTO = null;

		while (quiz.hasNext()) {

			wDTO = quiz.next();
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

	@SuppressWarnings("unchecked")
	@Override
	public WordQuizDTO getTodayQuiz(String user_seq) throws Exception {

		redisDB.setKeySerializer(new StringRedisSerializer());
		

		String userKey = QUIZ_INFO_PREFIX + user_seq;
		if(quizList==null) {
			redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
			quizList = (List)redisDB.opsForList().range(COL_NM, 0, -1);
		}
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		// get user todayquiz info
		QuizInfoDTO qiDTO = (QuizInfoDTO)redisDB.opsForValue().get(userKey);
		
		// get random question if null
		if(qiDTO==null) {
			int randomIdx = r.nextInt(quizList.size());
			WordQuizDTO rDTO = quizList.get(randomIdx);
			rDTO.setIdx(randomIdx);
			rDTO.setAnsweredQCount(0);
			rDTO.setTotalQCount(quizList.size());
			return rDTO;
		}
		
		// getting left questions
		List<Integer> leftIndex = new ArrayList<Integer>();
		for(int i=0; i<quizList.size();i++) {
			leftIndex.add(i);
		}
		Set<Integer> answeredIndex = qiDTO.getAnsweredQs().keySet();
		// all questions - answered questions
		leftIndex.removeAll(answeredIndex);
		log.info("leftIndex : " + leftIndex);
		
		// returns null if all questions answered
		if(leftIndex.isEmpty()) {
			WordQuizDTO rDTO = new WordQuizDTO();
			rDTO.setIdx(-1);
			return rDTO;
		}
		
		int randomIdxOfIdx = r.nextInt(leftIndex.size());
		int randomIdx = leftIndex.get(randomIdxOfIdx);
		log.info("randomIdx : " + randomIdx);
		WordQuizDTO rDTO = quizList.get(randomIdx);
		rDTO.setIdx(randomIdx);
		rDTO.setAnsweredQCount(qiDTO.getAnsweredQs().size());
		rDTO.setTotalQCount(quizList.size());
		return rDTO;
		
		
		
	}

	@Override
	public Map<String, String> submitTodayQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));

		int quizIntIdx = Integer.parseInt(quizIdx);
		String key = QUIZ_INFO_PREFIX + user_seq;

		// get quiz word by index
		WordQuizDTO rDTO = (WordQuizDTO) redisDB.opsForList().index(COL_NM, quizIntIdx);

		Map<String, String> rMap = new HashMap<String, String>();
		rMap.put("lemma", rDTO.getLemma());
		rMap.put("answerSentence", rDTO.getAnswerSentence());

		// get user quiz info
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		QuizInfoDTO qDTO = (QuizInfoDTO) redisDB.opsForValue().get(key);
		if (qDTO == null) {
			qDTO = new QuizInfoDTO();
		}
		boolean res;
		if (rDTO.getAnswer().equalsIgnoreCase(answer)) {
			res = true;
		} else {
			res = false;
		}

		qDTO.setUser_seq(user_seq);
		qDTO.getAnsweredQs().put(quizIntIdx, res);
		rMap.put("result", res ? "1" : "0");
		redisDB.opsForValue().set(key, qDTO);


		redisDB.expireAt(key, getTomorrow());
		return rMap;

	}

	@Override
	public boolean hasKey(String colNm) throws Exception {
		
		return redisDB.hasKey(colNm);
	}
	
	@Override
	public void putReviewWordToRedis(String user_seq, List<WordQuizDTO> quizList) throws Exception {
		
		String key = REVIEW_WORD_PREFIX + user_seq;
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
		
		if(!redisDB.hasKey(key)) {
		
		Iterator<WordQuizDTO> quiz = quizList.iterator();

		WordQuizDTO wDTO = null;

		
		while (quiz.hasNext()) {

			wDTO = quiz.next();
			redisDB.opsForList().rightPush(key, wDTO);
		}

		redisDB.expireAt(key, getTomorrow());
		}
		
		
	}
	
	public Date getTomorrow() {
		// set expire time to tomorrow
		Calendar c = Calendar.getInstance();
		
		c.add(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 7);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}

	/**@return
	 * {lemma, answerSentence, res}
	 *
	 */
	@Override
	public Map<String, String> submitReviewQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));

		int quizIntIdx = Integer.parseInt(quizIdx);
		String key = REVIEW_INFO_PREFIX + user_seq;
		String reviewWord = REVIEW_WORD_PREFIX + user_seq;

		// get quiz word by index
		WordQuizDTO rDTO = (WordQuizDTO) redisDB.opsForList().index(reviewWord, quizIntIdx);

		Map<String, String> rMap = new HashMap<String, String>();
		rMap.put("lemma", rDTO.getLemma());
		rMap.put("answerSentence", rDTO.getAnswerSentence());

		// get user quiz info
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		QuizInfoDTO qDTO = (QuizInfoDTO) redisDB.opsForValue().get(key);
		if (qDTO == null) {
			qDTO = new QuizInfoDTO();
		}
		boolean res;
		if (rDTO.getAnswer().equalsIgnoreCase(answer)) {
			res = true;
		} else {
			res = false;
		}

		qDTO.setUser_seq(user_seq);
		qDTO.getAnsweredQs().put(quizIntIdx, res);
		rMap.put("result", res ? "1" : "0");
		redisDB.opsForValue().set(key, qDTO);


		redisDB.expireAt(key, getTomorrow());
		return rMap;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public WordQuizDTO getReviewQuiz(String user_seq) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		

		String userKey = REVIEW_INFO_PREFIX + user_seq;
		String reviewWord = REVIEW_WORD_PREFIX + user_seq;
		
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
		List<WordQuizDTO> quizList = (List)redisDB.opsForList().range(reviewWord, 0, -1);
		
		
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		// get user todayquiz info
		QuizInfoDTO qiDTO = (QuizInfoDTO)redisDB.opsForValue().get(userKey);
		
		// get random question if null
		if(qiDTO==null) {
			int randomIdx = r.nextInt(quizList.size());
			WordQuizDTO rDTO = quizList.get(randomIdx);
			rDTO.setIdx(randomIdx);
			rDTO.setAnsweredQCount(0);
			rDTO.setTotalQCount(quizList.size());
			return rDTO;
		}
		
		// getting left questions
		List<Integer> leftIndex = new ArrayList<Integer>();
		for(int i=0; i<quizList.size();i++) {
			leftIndex.add(i);
		}
		Set<Integer> answeredIndex = qiDTO.getAnsweredQs().keySet();
		// all questions - answered questions
		leftIndex.removeAll(answeredIndex);
		log.info("leftIndex : " + leftIndex);
		
		// returns null if all questions answered
		if(leftIndex.isEmpty()) {
			WordQuizDTO rDTO = new WordQuizDTO();
			rDTO.setIdx(-1);
			return rDTO;
		}
		
		int randomIdxOfIdx = r.nextInt(leftIndex.size());
		int randomIdx = leftIndex.get(randomIdxOfIdx);
		log.info("randomIdx : " + randomIdx);
		WordQuizDTO rDTO = quizList.get(randomIdx);
		rDTO.setIdx(randomIdx);
		rDTO.setAnsweredQCount(qiDTO.getAnsweredQs().size());
		rDTO.setTotalQCount(quizList.size());
		return rDTO;

	}

	



}
