package poly.persistance.redis.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import poly.persistance.redis.IMyRedisMapper;

@Component("MyRedisMapper")
public class MyRedisMapper implements IMyRedisMapper{

	@Autowired
	public RedisTemplate<String, Object> redisDB;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	
	@Override
	public void doSaveData() throws Exception {
		
		log.info(this.getClass().getName() + ".getCacheData service start!!");
		
		String redisKey = "1920110005";
		String saveData = "김도우";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		
		if(redisDB.hasKey(redisKey)) {
			String res = (String) redisDB.opsForValue().get(redisKey);
			
			log.info("res : " + res);
			
		}else {
			redisDB.opsForValue().getAndSet(redisKey, saveData);
			
			log.info("No data!!");
		}
		
	}

	
}
