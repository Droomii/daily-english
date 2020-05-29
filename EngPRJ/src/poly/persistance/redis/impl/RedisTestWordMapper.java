package poly.persistance.redis.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import poly.dto.TestInfoDTO;
import poly.dto.TestWordDTO;
import poly.persistance.redis.IRedisTestWordMapper;

@Component("RedisTestWordMapper")
public class RedisTestWordMapper implements IRedisTestWordMapper{

	private static final String COL_NM = "testWords";
	
	private static final Random R = new Random();
	
	@Autowired
	private RedisTemplate<String, Object> redisDB;
	
	@Autowired
	private MongoTemplate mongodb;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public void saveTestWords() throws Exception {
		
		log.info(this.getClass().getName() + ".saveTestWords start");
		
		// 실력 측정 단어 컬렉션 이름 - testWords
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestWordDTO.class));
		
		if(redisDB.hasKey(COL_NM)) {
			log.info("testWords key exists!!");
			redisDB.delete(COL_NM);
		}
		
		
		
		DBCollection rCol = mongodb.getCollection(COL_NM);
		
		Iterator<DBObject> word = rCol.find();
		
		TestWordDTO tDTO = null;
		
		while(word.hasNext()) {
			
			DBObject currentWord = word.next();
			
			tDTO = new TestWordDTO(currentWord);
			log.info("inserting : " + tDTO.getWord());
			
			redisDB.opsForList().rightPush(COL_NM, tDTO);
		}
		
		log.info(this.getClass().getName() + ".saveTestWords end");
	}

	@Override
	public TestWordDTO getRandomWord() throws Exception {
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestWordDTO.class));
		
		int rand = R.nextInt(140);
		TestWordDTO tDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, rand);
		
		return tDTO;
	}

	@Override
	public TestWordDTO getRandomWord(String index, String answer) throws Exception {
		
		log.info(this.getClass().getName() + ".getRandomWord start");
		
		// 사용자 번호
		String userNo = "1";
		
		
		// get user test info
		TestInfoDTO tiDTO = getTestInfo(userNo);
		tiDTO.getAnsweredQs().add(Integer.parseInt(index));
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestWordDTO.class));

		// from 0 to 6
		int lvl = Integer.parseInt(index) / 20;
		log.info("lvl : " + lvl);
		
		// 레벨에 따른 정답률 정보
		int[][] lvlCorrectInfo = tiDTO.getCorrectAnswers();
		
		TestWordDTO tDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, Integer.parseInt(index));
		String correctAnswer = tDTO.getAnswer();
		log.info("answer : " + answer);
		log.info("correctAnswer : " + correctAnswer);
		
		// increment answered questions by 1
		lvlCorrectInfo[lvl][1]++;
		int nextLvl = 0;
		if(answer.equals(correctAnswer)) {
			
			// increment correct answer by 1
			lvlCorrectInfo[lvl][0]++;
			// capping level to 6
			nextLvl = lvl + 1 < 7 ? lvl + 1 : 6;
			
			boolean passedCurrLvl = lvlCorrectInfo[lvl][0] > 6; 
			
			// if passed lvl 6, return skill level as 7.
			if(passedCurrLvl && lvl==6) {
				TestWordDTO finalDTO = new TestWordDTO();
				finalDTO.setFinalLevel("8");
				redisDB.delete("testInfo_"+userNo);
				return finalDTO;
				
			}
			
			int nextLvlWrongCnt = lvlCorrectInfo[nextLvl][1] - lvlCorrectInfo[nextLvl][0];
			boolean failedNextLvl = nextLvlWrongCnt > 2;
			
			
			if(passedCurrLvl && failedNextLvl) {
				TestWordDTO finalDTO = new TestWordDTO();
				finalDTO.setFinalLevel(Integer.toString(lvl+1));
				redisDB.delete("testInfo_"+userNo);
				return finalDTO;
			}
			
			if(failedNextLvl) {
				nextLvl--;
			}

		}else {
			nextLvl = lvl - 1 > -1 ? lvl - 1 : 0;
			
			int currLvlWrongCnt = lvlCorrectInfo[lvl][1] - lvlCorrectInfo[lvl][0];
			boolean failedCurrLvl = currLvlWrongCnt > 2;
			
			
			// if failed level 0
			if(failedCurrLvl && lvl==0) {
				TestWordDTO finalDTO = new TestWordDTO();
				finalDTO.setFinalLevel("0");
				redisDB.delete("testInfo_"+userNo);
				return finalDTO;
			}
			
			boolean passedNextLvl = lvlCorrectInfo[nextLvl][0] > 6; 
			
			if(failedCurrLvl && passedNextLvl) {
				TestWordDTO finalDTO = new TestWordDTO();
				finalDTO.setFinalLevel(Integer.toString(nextLvl));
				redisDB.delete("testInfo_"+userNo);
				return finalDTO;
			}
			
			if(passedNextLvl) {
				nextLvl++;
			}
			
		}
		
		tDTO = null;
		
		// all answered questions
		Set<Integer> answeredQs = new HashSet<>(tiDTO.getAnsweredQs());
		log.info("answeredQs : " + answeredQs);
		// Question numbers of corresponding level 
		List<Integer> allQsofLvl = new ArrayList<>();
		for(int i=0; i<20; i++) {
			allQsofLvl.add(i+nextLvl*20);
		}
		
		allQsofLvl.removeAll(answeredQs);
		log.info("remaining Questions : " + allQsofLvl);
		
		int nextIndex = allQsofLvl.get(R.nextInt(allQsofLvl.size()));
		
		
		log.info("nextIndex : " + nextIndex);
		TestWordDTO rDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, nextIndex);
		
		updateTestInfo(tiDTO);
		
		log.info(this.getClass().getName() + ".getRandomWord end");
		return rDTO;
	}

	/**returns user's test info
	 * if not found, creates new test info and stores it in redis
	 *
	 */
	@Override
	public TestInfoDTO getTestInfo(String userNo) throws Exception {
		
		log.info(this.getClass().getName() + ".getTestInfo start");
		
		String redisKey = "testInfo_" + userNo;
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestInfoDTO.class));
		
		
		TestInfoDTO rDTO = (TestInfoDTO) redisDB.opsForValue().get(redisKey);
		
		if(rDTO==null) {
			rDTO = new TestInfoDTO(userNo);
			redisDB.opsForValue().set(redisKey, rDTO);
		}
		
		log.info(this.getClass().getName() + ".getTestInfo end");
		return rDTO;
		
	}

	@Override
	public void updateTestInfo(TestInfoDTO pDTO) throws Exception {
		
		String redisKey = "testInfo_" + pDTO.getUserNo();
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestInfoDTO.class));
		
		redisDB.opsForValue().set(redisKey, pDTO);
		redisDB.expire(redisKey, 10, TimeUnit.MINUTES);
		
	}
	

}
