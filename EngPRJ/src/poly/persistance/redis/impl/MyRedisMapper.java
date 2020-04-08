package poly.persistance.redis.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import poly.dto.MyJsonDTO;
import poly.persistance.redis.IMyRedisMapper;
import static poly.util.CmmUtil.nvl;

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
			
			redisDB.expire(redisKey, 2, TimeUnit.DAYS);
			
			log.info("No data!!");
		}
		
	}


	@Override
	public void doSaveDataforList() throws Exception {
		
		log.info(this.getClass().getName() + ".doSaveDataForList service start!!");
		
		String redisKey = "Test02-reverseList";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		
		if(redisDB.hasKey(redisKey)) {
			
			List<String> pList = (List) redisDB.opsForList().range(redisKey, 0, -1);
			
			for(Iterator<String> it = pList.iterator(); it.hasNext();) {
				String data = (String)it.next();
				
				log.info("data : " + data);
			}
			
		}else {
			
			for(int i = 0; i < 10; i++) {
				
				redisDB.opsForList().leftPush(redisKey, String.format("[%d]번째 데이터입니다." , i));
				
			}
			
			redisDB.expire(redisKey, 5, TimeUnit.HOURS);
			
			log.info("Save data!!");
		}
	}


	@Override
	public void doSaveDataforListJSON() throws Exception {
		
		String redisKey = "Test03-List-JSON";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(MyJsonDTO.class));
		
		MyJsonDTO pDTO = null;
		
		if(redisDB.hasKey(redisKey)) {
			
			List<MyJsonDTO> pList = (List) redisDB.opsForList().range(redisKey, 0, -1);
			
			for(Iterator<MyJsonDTO> it = pList.iterator(); it.hasNext();) {
				
				MyJsonDTO rDTO = (MyJsonDTO) it.next();
				
				if(rDTO == null) {
					rDTO = new MyJsonDTO();
				}
				
				log.info("name : " + nvl(rDTO.getName()));
				log.info("email : " + nvl(rDTO.getEmail()));
				log.info("addr : " + nvl(rDTO.getAddr()));
			}
			
		}else {
			
			pDTO = new MyJsonDTO();
			
			pDTO.setName("김도우");
			pDTO.setEmail("dowoo2594@naver.com");
			pDTO.setAddr("서울시 동작구");
			
			redisDB.opsForList().rightPush(redisKey, pDTO);
			
			pDTO= null;
			
			pDTO = new MyJsonDTO();
			
			pDTO.setName("이순신");
			pDTO.setEmail("leesunshin@naver.com");
			pDTO.setAddr("광화문");
			
			redisDB.opsForList().rightPush(redisKey, pDTO);
			
			redisDB.expire(redisKey, 100, TimeUnit.MINUTES);
		}
		
	}


	@Override
	public void doSaveDataforHashTable() throws Exception {
		
		String redisKey = "Test04-HashTable";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		
		if(redisDB.hasKey(redisKey)) {
			
			String name = nvl((String) redisDB.opsForHash().get(redisKey, "name"));
			String email = nvl((String) redisDB.opsForHash().get(redisKey, "email"));
			String addr = nvl((String) redisDB.opsForHash().get(redisKey, "addr"));
			
			log.info("name : " + name);
			log.info("email : " + email);
			log.info("addr : " + addr);
			
		}else {
			
			
			redisDB.opsForHash().put(redisKey, "name", "김도우");
			redisDB.opsForHash().put(redisKey, "email", "dowoo2594@naver.com");
			redisDB.opsForHash().put(redisKey, "addr", "서울시 동작구");
			
			redisDB.expire(redisKey, 100, TimeUnit.MINUTES);
			
			log.info("save data!!");
		}
		
	}


	@Override
	public void doSaveDataforSet() throws Exception {
		
		String redisKey = "Test05-Set";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		
		if(redisDB.hasKey(redisKey)) {
			
			Set rSet = (Set)redisDB.opsForSet().members(redisKey);

			for(Iterator<String> it = rSet.iterator(); it.hasNext();) {

				String value = nvl((String)it.next());
				
				log.info("value : " + value);
				
			}

		}else {
			
			redisDB.opsForSet().add(redisKey, "1번째 저장");
			redisDB.opsForSet().add(redisKey, "2번째 저장");
			redisDB.opsForSet().add(redisKey, "3번째 저장");
			redisDB.opsForSet().add(redisKey, "4번째 저장");
			
			redisDB.expire(redisKey, 100, TimeUnit.MINUTES);
			
			log.info("save data!!");
		}
	}


	@Override
	public void doSaveDataforZSet() throws Exception {
		
		String redisKey = "Test06-ZSet";
		
		redisDB.setKeySerializer(new StringRedisSerializer());
		redisDB.setValueSerializer(new StringRedisSerializer());
		
		
		if(redisDB.hasKey(redisKey)) {
			
			long cnt = redisDB.opsForZSet().size(redisKey);
			
			Set rSet = (Set)redisDB.opsForZSet().range(redisKey, 0, cnt);

			if(rSet==null) {
				rSet = new LinkedHashSet<String>();
			}
			
			for(Iterator<String> it = rSet.iterator(); it.hasNext();) {

				String value = nvl((String)it.next());
				
				log.info("value : " + value);
				
			}

		}else {
			
			redisDB.opsForZSet().add(redisKey, "1번째 저장", 1);
			redisDB.opsForZSet().add(redisKey, "2번째 저장", 2);
			redisDB.opsForZSet().add(redisKey, "3번째 저장", 3);
			redisDB.opsForZSet().add(redisKey, "4번째 저장, 2번째 위치", 1.1);
			
			redisDB.expire(redisKey, 100, TimeUnit.MINUTES);
			
			log.info("save data!!");
		}
		
	}

	
}
