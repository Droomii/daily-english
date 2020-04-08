package poly.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.persistance.redis.IMyRedisMapper;
import poly.service.IMyRedisService;

@Service("MyRedisService")
public class MyRedisService implements IMyRedisService{

	@Resource(name = "MyRedisMapper")
	IMyRedisMapper myRedisMapper;
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void doSaveData() throws Exception {
		
		log.info(this.getClass().getName() + ".doSaveData Start!!");
		
		myRedisMapper.doSaveData();
		
		log.info(this.getClass().getName() + ".doSaveData End!!");
	}
	
	
}
