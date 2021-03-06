package poly.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.dto.NewsDTO;
import poly.dto.WordDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.persistance.mongo.IMongoNewsWordMapper;
import poly.persistance.mongo.IMongoUserMapper;
import poly.persistance.redis.IRedisNewsWordMapper;
import poly.service.INewsWordService;

@Service("NewsWordService")
public class NewsWordService implements INewsWordService{
	
	@Resource(name = "MongoNewsWordMapper")
	IMongoNewsWordMapper mongoNewsWordMapper;
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;

	@Resource(name = "RedisNewsWordMapper")
	IRedisNewsWordMapper redisNewsWordMapper;
	
	@Resource(name = "MongoUserMapper")
	IMongoUserMapper mongoUserMapper;
	
	private Map<String, Integer> WORD_POOL = new HashMap<>();
	
	Logger log = Logger.getLogger(this.getClass());

	/** 단어 데이터 초반에 불러오기
	 *
	 */
	@Override
	@PostConstruct
	public void loadWordPool() throws Exception {
		
		log.info(this.getClass().getName() + ".loadWordPool start");
		
		WORD_POOL = mongoNewsWordMapper.getWordPool();
		
		log.info(this.getClass().getName() + ".loadWordPool end");
		
	}
	
	
	@Override
	public void insertWords(List<WordDTO> words) throws Exception {
		
		mongoNewsWordMapper.insertWords(words);
		
	}

	/** @return : [{lemma : (String)"단어원형", level : (Integer)단어 중요도, sntncIdx : (Integer)"문장 인덱스", wordIdx : (Integer)"단어 인덱스"}]
	 *
	 */
	@Override
	public List<Map<String, Object>> extractWords(NewsDTO pDTO) throws Exception {
		
		log.info(this.getClass().getName() + ".extractWords start");
		
		List<Map<String, Object>> rList = new ArrayList<>();
		Map<String, Object> pMap = null;
		
		int sntncIdx = 0;
		// 각 단어의 동사원형으로 for each
		for(List<String> lemmasBySentence : pDTO.getLemmas()) {
			int wordIdx = 0;
			for(String lemma : lemmasBySentence) {
				if(WORD_POOL.containsKey(lemma.toLowerCase())) {
					pMap = new HashMap<String, Object>();
					pMap.put("lemma", lemma);
					pMap.put("word", pDTO.getTokens().get(sntncIdx).get(wordIdx));
					pMap.put("sntncIdx", sntncIdx);
					pMap.put("wordIdx", wordIdx);
					pMap.put("level", WORD_POOL.get(lemma.toLowerCase()));
					rList.add(pMap);
					pMap = null;
				}
				wordIdx++;
			}
			sntncIdx++;
		}
		
		log.info(this.getClass().getName() + ".extractWords end");
		return rList;
	}


	@Override
	public void highlightWords(NewsDTO news) throws Exception {
		
		List<Map<String, Object>> extractedWords = extractWords(news);
		
		news.highlightAllWords(extractedWords);
		
	}


	@Override
	public void saveTodayWordToRedis(NewsDTO pDTO) throws Exception {
		log.info("pDTO : " + pDTO);
		List<Map<String, Object>> extractedWords = extractWords(pDTO);
		
		List<WordQuizDTO> rList = pDTO.generateProblems(extractedWords);
		
		mongoNewsWordMapper.saveWordUsage(rList);
		redisNewsWordMapper.saveTodayWordToRedis(rList, pDTO.getNewsUrl());
	}


	@Override
	public WordQuizDTO getRandomTodayQuiz(String user_seq) throws Exception {
		log.info(this.getClass().getName() + ".getTodayQuiz start");
		WordQuizDTO rDTO = redisNewsWordMapper.getTodayQuiz(user_seq);
		return rDTO;
	}


	@Override
	public Map<String, String> submitTodayQuizAnswer(String user_seq, String user_lvl, String quizIdx, String answer) throws Exception {

		Map<String, String> rMap =redisNewsWordMapper.submitTodayQuizAnswer(user_seq, quizIdx, answer);
		rMap.put("user_seq", user_seq);
		rMap.put("user_lvl", user_lvl);
		mongoNewsWordMapper.insertQuizRecord(rMap);
		if(rMap.get("result").equals("0")) {
			mongoNewsWordMapper.insertReviewWord(rMap);
		}
		return rMap;
		
	}


	@Override
	public void putReviewWordToRedis(String user_seq) throws Exception {

		log.info(this.getClass().getName() + ".putReviewWordToRedis start");
		boolean hasKey = redisNewsWordMapper.hasKey("reviewWord_" + user_seq);
		if(!hasKey) {
			// pList keys: {word, correctCounter}
			List<Map<String, Object>> pList = mongoNewsWordMapper.getReviewWords(user_seq);
			
			List<WordQuizDTO> quizList = new ArrayList<WordQuizDTO>();
			for(Map<String, Object> pMap : pList) {
				String word = (String)pMap.get("word");
				WordQuizDTO wqDTO = mongoNewsWordMapper.getRandomUsage(word);
				wqDTO.setCorrectCounter((Integer)pMap.get("correctCounter"));
				quizList.add(wqDTO);
			}
			
			redisNewsWordMapper.putReviewWordToRedis(user_seq, quizList);
		}
		
		log.info(this.getClass().getName() + ".putReivewWordToRedis end");
		
	}


	@Override
	public Map<String, String> submitReviewQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception {

		
		Map<String, String> rMap =redisNewsWordMapper.submitReviewQuizAnswer(user_seq, quizIdx, answer);
		rMap.put("user_seq", user_seq);
		mongoNewsWordMapper.updateCorrectCounter(rMap);
		
		return rMap;
	}


	@Override
	public WordQuizDTO getRandomReviewQuiz(String user_seq) throws Exception {
		
		return redisNewsWordMapper.getReviewQuiz(user_seq);
	}


	@Override
	public List<Map<String, String>> getTodayWrongWords(String user_seq) throws Exception {
		
		List<String> wrongWords = redisNewsWordMapper.getTodayWrongWords(user_seq);
		List<Map<String, String>> rList = mongoNewsWordMapper.getWrongWordMeaning(wrongWords);
		return rList;
	}


	@Override
	public void insertMeaning() throws Exception {
		
		mongoNewsWordMapper.insertMeaning();
		
	}


	@Override
	public void saveTodayTTS() throws Exception {
		redisNewsWordMapper.saveTodayTTS();
		
	}


	@Override
	public List<Map<String, Object>> getTodaySentences() throws Exception {
		return redisNewsWordMapper.getTodaySentences();
	}


	@Override
	public void resetQuiz(String user_seq) throws Exception {
		redisNewsWordMapper.resetQuiz(user_seq);
	}

}
