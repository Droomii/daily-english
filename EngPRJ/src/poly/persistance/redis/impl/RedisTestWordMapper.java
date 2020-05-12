package poly.persistance.redis.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

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
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestWordDTO.class));

		// from 0 to 6
		int lvl = Integer.parseInt(index) / 20;
		log.info("lvl : " + lvl);
		TestWordDTO tDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, Integer.parseInt(index)-1);
		String correctAnswer = tDTO.getAnswer();
		log.info("answer : " + answer);
		log.info("correctAnswer : " + correctAnswer);
		if(answer.equals(correctAnswer)) {
			// capping level to 6
			lvl = lvl + 1 < 7 ? lvl + 1 : 6;
		}else {
			lvl = lvl - 1 > -1 ? lvl - 1 : 0;
		}
		
		tDTO = null;
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(TestWordDTO.class));
		int nextIndex = lvl * 20 + R.nextInt(20);
		log.info("nextIndex : " + nextIndex);
		TestWordDTO rDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, nextIndex);
		return rDTO;
	}
	
	

}
