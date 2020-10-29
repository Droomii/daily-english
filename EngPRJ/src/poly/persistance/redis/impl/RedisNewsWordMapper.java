package poly.persistance.redis.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import poly.dto.NewsDTO;
import poly.dto.QuizInfoDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.redis.IRedisNewsWordMapper;
import poly.util.TTSUtil;

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

	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void loadQuizList()  throws Exception{
		log.info("post construct of quizList start!!");
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
		quizList = (List) redisDB.opsForList().range(COL_NM, 0, -1);
		log.info("quizList : " + quizList);
	}
	
	@Override
	public void saveTodayWordToRedis(List<WordQuizDTO> pList, String newsUrl) throws Exception {

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

		// url 레디스에 넣기
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		if (redisDB.hasKey("todayNewsUrl")) {
			redisDB.delete("todayNewsUrl");
		}
		
		redisDB.opsForValue().getAndSet("todayNewsUrl", newsUrl);

		redisDB.expireAt("todayNewsUrl", getTomorrow());
		redisDB.expireAt(COL_NM, getTomorrow());

	}

	@SuppressWarnings("unchecked")
	@Override
	public WordQuizDTO getTodayQuiz(String user_seq) throws Exception {

		redisDB.setKeySerializer(new StringRedisSerializer());

		String userKey = QUIZ_INFO_PREFIX + user_seq;
		if (quizList == null) {
			redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
			quizList = (List) redisDB.opsForList().range(COL_NM, 0, -1);
		}
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		// get user todayquiz info
		QuizInfoDTO qiDTO = (QuizInfoDTO) redisDB.opsForValue().get(userKey);

		// get random question if null
		if (qiDTO == null) {
			int randomIdx = r.nextInt(quizList.size());
			WordQuizDTO rDTO = quizList.get(randomIdx);
			rDTO.setIdx(randomIdx);
			rDTO.setAnsweredQCount(0);
			rDTO.setTotalQCount(quizList.size());
			return rDTO;
		}

		// getting left questions
		List<Integer> leftIndex = new ArrayList<Integer>();
		for (int i = 0; i < quizList.size(); i++) {
			leftIndex.add(i);
		}
		Set<Integer> answeredIndex = qiDTO.getAnsweredQs().keySet();
		// all questions - answered questions
		leftIndex.removeAll(answeredIndex);
		log.info("leftIndex : " + leftIndex);

		// returns null if all questions answered
		if (leftIndex.isEmpty()) {
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

		if (!redisDB.hasKey(key)) {

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
		c.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		return c.getTime();
	}

	/**
	 * @return {lemma, answerSentence, res}
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
		List<WordQuizDTO> quizList = (List) redisDB.opsForList().range(reviewWord, 0, -1);

		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));
		// get user todayquiz info
		QuizInfoDTO qiDTO = (QuizInfoDTO) redisDB.opsForValue().get(userKey);

		// get random question if null
		if (qiDTO == null) {
			int randomIdx = r.nextInt(quizList.size());
			WordQuizDTO rDTO = quizList.get(randomIdx);
			rDTO.setIdx(randomIdx);
			rDTO.setAnsweredQCount(0);
			rDTO.setTotalQCount(quizList.size());
			return rDTO;
		}

		// getting left questions
		List<Integer> leftIndex = new ArrayList<Integer>();
		for (int i = 0; i < quizList.size(); i++) {
			leftIndex.add(i);
		}
		Set<Integer> answeredIndex = qiDTO.getAnsweredQs().keySet();
		// all questions - answered questions
		leftIndex.removeAll(answeredIndex);
		log.info("leftIndex : " + leftIndex);

		// returns null if all questions answered
		if (leftIndex.isEmpty()) {
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
	public List<String> getTodayWrongWords(String user_seq) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(QuizInfoDTO.class));

		String key = QUIZ_INFO_PREFIX + user_seq;
		QuizInfoDTO qDTO = (QuizInfoDTO) redisDB.opsForValue().get(key);
		if(qDTO==null)
			qDTO = new QuizInfoDTO();
		
		Map<Integer, Boolean> answeredQs = qDTO.getAnsweredQs();

		log.info("answeredQs : " + answeredQs);
		List<String> rList = new ArrayList<String>();
		answeredQs.forEach((k, v) -> {
			log.info("v : " + v);
			log.info("k : " + k);
			if (!v) {
				
				rList.add(quizList.get(k).getLemma());
			}
		});

		return rList;
	}

	@Override
	public void saveTodayTTS() throws Exception {
		
		log.info(this.getClass().getName() + ".saveTodayTTS start");
		redisDB.setKeySerializer(new StringRedisSerializer());

		if (quizList == null) {
			redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
			quizList = (List) redisDB.opsForList().range(COL_NM, 0, -1);
		}
		
		String newsUrl = getTodayNewsUrl();
		
		int i = 0;
		String lastSentence = "";
		for(WordQuizDTO wqDTO : quizList) {
			String sentence = wqDTO.getOriginalSentence();
			if(!sentence.equals(lastSentence)) {
				TTSUtil.saveTTS(i, sentence, newsUrl);
			}
			lastSentence = sentence;
			i++;
		}
	}

	@Override
	public List<Map<String, Object>> getTodaySentences() throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());

		if (quizList == null) {
			redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordQuizDTO.class));
			quizList = (List) redisDB.opsForList().range(COL_NM, 0, -1);
		}
		Set<String> rSet = new LinkedHashSet<String>();
		List<Map<String, Object>> rList = new ArrayList<>();
		int i = 0;
		for(WordQuizDTO e : quizList){
			if(rSet.add(e.getOriginalSentence())) {
				Map<String, Object> rMap = new HashMap<String, Object>();
				rMap.put("sentence", e.getOriginalSentence());
				rMap.put("index", i);
				rList.add(rMap);
			}
			i++;
		};
		
		return rList;
	}

	@Override
	public String getTodayNewsUrl() throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		return (String)redisDB.opsForValue().get("todayNewsUrl");
	}

	@Override
	public void saveTodayNewsUrl(String headlineUrl) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		if (redisDB.hasKey("todayNewsUrl")) {
			redisDB.delete("todayNewsUrl");
		}
		
		redisDB.opsForValue().getAndSet("todayNewsUrl", headlineUrl);

		redisDB.expireAt("todayNewsUrl", getTomorrow());
		redisDB.expireAt(COL_NM, getTomorrow());
		
	}

	@Override
	public void saveTodayNews(NewsDTO headline) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(NewsDTO.class));
		
		final String redisKey = "todayNews";
		
		redisDB.opsForValue().set(redisKey, headline);
	}

	@Override
	public void resetQuiz(String user_seq) throws Exception {
		redisDB.setKeySerializer(new StringRedisSerializer());
		String key = QUIZ_INFO_PREFIX + user_seq;
		redisDB.delete(key);
		
	}

}
