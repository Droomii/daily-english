package poly.persistance.redis.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisNewsWordMapper {

	@Autowired
	private RedisTemplate<String, Object> redisDB;
	
	
	
	
}
