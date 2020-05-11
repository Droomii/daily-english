package poly.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.persistance.mongo.IMongoTestMapper;
import poly.service.IMongoTestService;

@Service("MongoTestService")
public class MongoTestService implements IMongoTestService{

	@Resource(name = "MongoTestMapper")
	private IMongoTestMapper mongoTestMapper;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public boolean createCollection() throws Exception {
		
		log.info(this.getClass().getName() + ".createCollection start");
		
		String colNm = "MyTestCollection";
		
		return mongoTestMapper.createCollection(colNm);
	}

	
}
