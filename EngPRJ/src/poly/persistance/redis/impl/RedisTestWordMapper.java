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
		Random r = new Random();
		
		int rand = r.nextInt(140);
		TestWordDTO tDTO = (TestWordDTO) redisDB.opsForList().index(COL_NM, rand);
		
		return tDTO;
	}
	
	

}
