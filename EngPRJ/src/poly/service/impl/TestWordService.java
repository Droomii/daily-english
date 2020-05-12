package poly.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.dto.TestWordDTO;
import poly.persistance.redis.IRedisTestWordMapper;
import poly.service.ITestWordService;

@Service("TestWordService")
public class TestWordService implements ITestWordService {

	@Resource(name = "RedisTestWordMapper")
	IRedisTestWordMapper redisTestWordMapper;

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void saveTestWords() throws Exception {
		log.info(this.getClass().getName() + ".saveTestWords start");

		redisTestWordMapper.saveTestWords();

		log.info(this.getClass().getName() + ".saveTestWords end");

	}

	@Override
	public TestWordDTO getRandomWord() throws Exception {

		return redisTestWordMapper.getRandomWord();
	}

	@Override
	public TestWordDTO submitTestAnswer(String index, String answer) throws Exception {
		
		return redisTestWordMapper.getRandomWord(index, answer);
	}

}
