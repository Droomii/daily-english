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
	
	private Map<String, List<String>> WORD_POOL = new HashMap<>();
	
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

	/** @return : [{lemma : (String)"단어원형", pool : (List of String)"속한 풀", sntncIdx : (Integer)"문장 인덱스", wordIdx : (Integer)"단어 인덱스"}]
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
					pMap.put("pool", WORD_POOL.get(lemma.toLowerCase()));
					pMap.put("sntncIdx", sntncIdx);
					pMap.put("wordIdx", wordIdx);
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
		redisNewsWordMapper.saveTodayWordToRedis(rList);
	}


	@Override
	public WordQuizDTO getRandomTodayQuiz(String user_seq) throws Exception {
		log.info(this.getClass().getName() + ".getTodayQuiz start");
		
		return redisNewsWordMapper.getTodayQuiz(user_seq);
	}


	@Override
	public Map<String, String> submitTodayQuizAnswer(String user_seq, String quizIdx, String answer) throws Exception {

		Map<String, String> rMap =redisNewsWordMapper.submitTodayQuizAnswer(user_seq, quizIdx, answer);
		rMap.put("user_seq", user_seq);
		mongoNewsWordMapper.insertQuizRecord(rMap);
		if(rMap.get("result").equals("0")) {
			mongoNewsWordMapper.insertReviewWord(rMap);
		}
		return rMap;
		
	}


	@Override
	public void putReviewWordToRedis(String user_seq) throws Exception {

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
		
	}

	
	
	

}
