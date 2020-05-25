package poly.persistance.mongo.impl;

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
import poly.util.TranslateUtil;

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
	public void insertNews(NewsDTO rDTO) throws Exception {

		createCollection();
		DBCollection col = mongodb.getCollection(COL_NM);
		BasicDBObject query = new BasicDBObject();
		query.put("newsUrl", rDTO.getNewsUrl());
		DBCursor res = col.find(query);
		if(res.hasNext()) {
			log.info("news already crawled");
		}else {
			rDTO.translate();
			mongodb.insert(rDTO, COL_NM);
		}
		
	}

	@Override
	public NewsDTO getLatestNews() throws Exception {
		
		DBObject firstNews = mongodb.getCollection(COL_NM).find().sort(new BasicDBObject("insertDate", -1)).next();
		NewsDTO rDTO = new NewsDTO(firstNews);
		log.info("rDTO.getNewsTitle : " + rDTO.getNewsTitle());
		return rDTO;
	}

}
