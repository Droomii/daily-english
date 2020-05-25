package poly.persistance.mongo.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import config.Mapper;
import poly.dto.WordDTO;
import poly.persistance.mongo.IMongoNewsWordMapper;

@Mapper("MongoNewsWordMapper")
public class MongoNewsWordMapper implements IMongoNewsWordMapper{

	private static final String COL_NM = "wordPool";
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private MongoTemplate mongodb;

	@Override
	public void insertWords(List<WordDTO> words) throws Exception {
		
		createCollection();
		mongodb.insert(words, COL_NM);
		
		
	}

	@Override
	public void createCollection() throws Exception {
		log.info(this.getClass().getName() + ".createCollection start");


		if (!mongodb.collectionExists(COL_NM)) {
			mongodb.createCollection(COL_NM).createIndex(new BasicDBObject("word", 1), "wordIdx");
		}

		log.info(this.getClass().getName() + ".createCollection end");

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getWordPool() throws Exception {
		log.info(this.getClass().getName() + ".getWordPool start");
		
		DBObject query = new BasicDBObject("pool", new BasicDBObject("$in", Arrays.asList("TOEIC", "Academic", "Business")));
		DBCursor cursor = mongodb.getCollection(COL_NM).find(query);
		
		Map<String, List<String>> rMap = new HashMap<>();
		while(cursor.hasNext()) {
			DBObject obj = cursor.next();
			rMap.put((String)obj.get("word"), (List<String>)obj.get("pool"));
		}
		
		log.info(this.getClass().getName() + ".getWordPool end");
		return rMap;
	}
	
}
