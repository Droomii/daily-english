package poly.persistance.mongo.impl;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import config.Mapper;
import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;

@Mapper("MongoNewsMapper")
public class MongoNewsMapper implements IMongoNewsMapper {

	@Autowired
	private MongoTemplate mongodb;
	
	private static final String COL_NM = "news";

	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public void createCollection() throws Exception {
		log.info(this.getClass().getName() + ".createCollection start");


		if (!mongodb.collectionExists(COL_NM)) {
			mongodb.createCollection(COL_NM).createIndex(new BasicDBObject("insertDate", -1), "dateIdx");
		}

		log.info(this.getClass().getName() + ".createCollection end");

	}

	@Override
	public boolean insertNews(NewsDTO rDTO, boolean translate) throws Exception {

		createCollection();
		DBCollection col = mongodb.getCollection(COL_NM);
		BasicDBObject query = new BasicDBObject();
		query.put("newsUrl", rDTO.getNewsUrl());
		DBCursor res = col.find(query);
		if(res.hasNext()) {
			log.info("news already crawled");
			return false;
		}else {
			if(translate)
				rDTO.translate();
			mongodb.insert(rDTO.toMap(), COL_NM);
			return true;
		}
	}

	@Override
	public NewsDTO getLatestNews() throws Exception {
		
		DBObject firstNews = mongodb.getCollection(COL_NM).find().sort(new BasicDBObject("insertDate", -1)).next();
		NewsDTO rDTO = new NewsDTO(firstNews);
		log.info("rDTO.getNewsTitle : " + rDTO.getNewsTitle());
		return rDTO;
	}

	@Override
	public NewsDTO getNews(int i) throws Exception {
		DBCursor cursor = mongodb.getCollection(COL_NM).find();
		for(int j = 0; j < i; j++) {
			cursor.next();
		}
		return new NewsDTO(cursor.next());
	}

	@Override
	public DBCursor getAllArticles() throws Exception {
		return mongodb.getCollection(COL_NM).find();
	}

	@Override
	public void addNp(int np) throws Exception {
		DBObject obj = new BasicDBObject("np", np);
		mongodb.insert(obj, "np");
	}

	@Override
	public int getNp() throws Exception {
		return (Integer)mongodb.getCollection("np").find().sort(new BasicDBObject("np", 1)).next().get("np");
	}

	@Override
	public void incrementDf(Set<String> keySet) throws Exception {
		if(!mongodb.collectionExists("dfCol")) {
			mongodb.createCollection("dfCol").createIndex(new BasicDBObject("word", 1), new BasicDBObject("unique", true));
		}
		for(String word : keySet) {
			DBObject query = new BasicDBObject("word", word);
			DBObject update = new BasicDBObject("$inc", new BasicDBObject("cnt", 1L));
			mongodb.getCollection("dfCol").update(query, update, true, false);
		}
		
		
	}

}
