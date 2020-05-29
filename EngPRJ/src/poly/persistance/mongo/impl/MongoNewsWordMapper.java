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
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import config.Mapper;
import poly.dto.WordDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.mongo.IMongoNewsWordMapper;

@Mapper("MongoNewsWordMapper")
public class MongoNewsWordMapper implements IMongoNewsWordMapper{

	private static final String WORD_POOL = "wordPool";
	private static final String WORD_USAGE = "wordUsage";
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private MongoTemplate mongodb;

	@Override
	public void insertWords(List<WordDTO> words) throws Exception {
		
		createCollection(WORD_POOL);
		mongodb.insert(words, WORD_POOL);
		
		
	}

	public void createCollection(String colNm) throws Exception {
		log.info(this.getClass().getName() + ".createCollection start");


		if (!mongodb.collectionExists(colNm)) {
			mongodb.createCollection(colNm).createIndex(new BasicDBObject("word", 1), "wordIdx");
		}

		log.info(this.getClass().getName() + ".createCollection end");

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getWordPool() throws Exception {
		log.info(this.getClass().getName() + ".getWordPool start");
		
		DBObject query = new BasicDBObject("pool", new BasicDBObject("$in", Arrays.asList("TOEIC", "Academic", "Business")));
		DBCursor cursor = mongodb.getCollection(WORD_POOL).find(query);
		
		Map<String, List<String>> rMap = new HashMap<>();
		while(cursor.hasNext()) {
			DBObject obj = cursor.next();
			rMap.put((String)obj.get("word"), (List<String>)obj.get("pool"));
		}
		
		log.info(this.getClass().getName() + ".getWordPool end");
		return rMap;
	}

	// saving word usage to mongo
	@Override
	public void saveWordUsage(List<WordQuizDTO> rList) throws Exception {
		
		if (!mongodb.collectionExists(WORD_USAGE)) {
			mongodb.createCollection(WORD_USAGE).createIndex(new BasicDBObject("lemma", 1), "lemmaIdx");
		}
		List<Map<String, String>> pList = new ArrayList<Map<String,String>>();
		
		Map<String, String> pMap = null;
		
		for(WordQuizDTO wDTO : rList) {
			
			// check for duplicate
			DBObject query = new BasicDBObject()
					.append("lemma", wDTO.getLemma())
					.append("sentence", wDTO.getOriginalSentence());
			DBCursor queryRes = mongodb.getCollection(WORD_USAGE).find(query);

			// if not duplicate
			if(!queryRes.hasNext()) {
				pMap = new HashMap<String, String>();
				pMap.put("lemma", wDTO.getLemma());
				pMap.put("word", wDTO.getAnswer());
				pMap.put("sentence", wDTO.getOriginalSentence());
				pList.add(pMap);
				pMap = null;
			}
			
		}
		mongodb.insert(pList, WORD_USAGE);
	}
	
	
}
