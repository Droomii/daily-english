package poly.persistance.redis.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import poly.dto.WeatherDTO;
import poly.persistance.redis.IWeatherRedisMapper;

@Component("WeatherRedisMapper")
public class WeatherRedisMapper implements IWeatherRedisMapper{
	
	@Autowired
	public RedisTemplate<String, Object> redisDB;
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public boolean getExists(String key) throws Exception {
		log.info(this.getClass().getName() + ".getExists start!!");
		
		return redisDB.hasKey(key);
	}

	@Override
	public List<WeatherDTO> getWeather(String key) throws Exception {
		log.info(this.getClass().getName() + ".getWeather start!!");
		
		List<WeatherDTO> rList = null;
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WeatherDTO.class));
		
		if(redisDB.hasKey(key)) {
			rList = (List) redisDB.opsForList().range(key, 0, -1);
		}
		
		log.info(this.getClass().getName() + ".getWeather end!!");
		
		return rList;
	}

	@Override
	public int setWeather(String key, List<WeatherDTO> pList) throws Exception {
		
		int res = 0;
		
		log.info(this.getClass().getName() + ".setWeather start!!");
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(WeatherDTO.class));
		
		if(this.getExists(key)) {
			redisDB.delete(key);
		}
		
		for(Iterator<WeatherDTO> it = pList.iterator(); it.hasNext();) {
			WeatherDTO pDTO = (WeatherDTO) it.next();
			
			redisDB.opsForList().rightPush(key, pDTO);
			
			pDTO = null;
			
		}
		
		res = 1;
		log.info(this.getClass().getName() + ".setWeather end!!");
		
		return res;
	}

	@Override
	public boolean setTimeOutHour(String key, int hours) throws Exception {
		log.info(this.getClass().getName() + ".setTimeOutHour start!!");
		return redisDB.expire(key, hours, TimeUnit.HOURS);
	}
	
	
}
