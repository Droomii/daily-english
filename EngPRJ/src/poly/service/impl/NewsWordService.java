package poly.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import poly.dto.WordDTO;
import poly.persistance.mongo.IMongoNewsWordMapper;
import poly.service.INewsWordService;

@Service("NewsWordService")
public class NewsWordService implements INewsWordService{
	
	@Resource(name = "MongoNewsWordMapper")
	IMongoNewsWordMapper MongoNewsWordMapper;

	@Override
	public void insertWords(List<WordDTO> words) throws Exception {
		
		MongoNewsWordMapper.insertWords(words);
		
	}
	
	

}
