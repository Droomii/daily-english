package poly.persistance.mongo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		if (!mongodb.collectionExists(COL_NM)) {
			mongodb.createCollection(COL_NM).createIndex(new BasicDBObject("newsUrl", -1), "newsIdx");
		}
	}
	@Override
	public boolean newsExists(NewsDTO nDTO) throws Exception {
		DBCollection col = mongodb.getCollection(COL_NM);
		BasicDBObject query = new BasicDBObject();
		query.put("newsUrl", nDTO.getNewsUrl());
		DBCursor res = col.find(query);
		if(res.hasNext()) {
			return true;
		}
		return false;
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
	public boolean insertNews(List<NewsDTO> newArticles, boolean translate) throws Exception {
		boolean res = true;
		Iterator<NewsDTO> it = newArticles.iterator();
		while(it.hasNext()) {
			if(!insertNews(it.next(), translate)) {
				it.remove();
				res = false;
			}
		}
		return res;
	}
	
	

	@Override
	public NewsDTO getLatestNews() throws Exception {
		
		DBObject firstNews = mongodb.getCollection(COL_NM).find().sort(new BasicDBObject("_id", -1)).next();
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
	public NewsDTO getNews(String todayNewsUrl) throws Exception {
		return new NewsDTO(mongodb.getCollection(COL_NM).findOne(new BasicDBObject("newsUrl", todayNewsUrl)));
	}
	
	
	@Override
	public DBCursor getAllArticles() throws Exception {
		return mongodb.getCollection(COL_NM).find().sort(new BasicDBObject("_id", -1));
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

	@Override
	public void insertTf(String newsUrl, Map<String, Double> tf) throws Exception {
		if(!mongodb.collectionExists("tfCol")) {
			mongodb.createCollection("tfCol").createIndex(new BasicDBObject("newsUrl", 1), new BasicDBObject("unique", true));
		}
		DBObject insertObj = new BasicDBObject("newsUrl", newsUrl)
				.append("tf", tf);
		mongodb.insert(insertObj, "tfCol");
		
	}

	@Override
	public void insertNewsTfAll(List<DBObject> tfList) throws Exception {
		log.info(this.getClass().getName() + ".insertTfAll start");
		if(!mongodb.collectionExists("tfCol")) {
			mongodb.createCollection("tfCol").createIndex(new BasicDBObject("newsUrl", 1), new BasicDBObject("unique", true));
		}
		mongodb.getCollection("tfCol").insert(tfList);
	}

	@Override
	public void insertDf(Map<String, Long> df) throws Exception {
		log.info(this.getClass().getName() + ".insertDf start");
		if(!mongodb.collectionExists("dfCol")) {
			mongodb.createCollection("dfCol").createIndex(new BasicDBObject("word", 1), new BasicDBObject("unique", true));
		}
		List<DBObject> list = new ArrayList<>();
		for(Entry<String, Long> word : df.entrySet()) {
			DBObject obj = new BasicDBObject("word", word.getKey())
					.append("cnt", word.getValue().longValue());
			list.add(obj);
		}
		mongodb.getCollection("dfCol").insert(list);
		log.info(this.getClass().getName() + ".insertDf end");
	}

	@Override
	public void insertIdf() throws Exception {
		long docCnt = mongodb.getCollection("news").count();
		DBCursor dfCursor = mongodb.getCollection("dfCol").find();
		DBCollection idfCol = mongodb.getCollection("idfCol");
		List<DBObject> idfList = new ArrayList<>();
		int i = 1;
		while(dfCursor.hasNext()) {
			
			DBObject df =  dfCursor.next();
			log.info(i + "/" + docCnt + " calculating idf : " + (String)df.get("word"));
			double cnt = ((Long)df.get("cnt")).doubleValue();
			double idf = Math.log(docCnt / cnt);
			DBObject insertObj = new BasicDBObject()
					.append("word", df.get("word"))
					.append("idf", idf);
			idfList.add(insertObj);
			i++;
			if(i % 1000 == 0) {
				log.info("hit " + i + " : inserting");
				idfCol.insert(idfList);
				idfList.clear();
			}
		}
		idfCol.insert(idfList);
		
	}

	@Override
	public void insertTfIdf() throws Exception {
		final Map<String, Double> idfMap = new HashMap<>();
		final DBCursor idfCursor = mongodb.getCollection("idfCol").find();
		
		if(!mongodb.collectionExists("tfIdfCol")) {
			mongodb.createCollection("tfIdfCol").createIndex(new BasicDBObject("newsUrl", -1), new BasicDBObject("unique", true));
		}
		
		final DBCollection tfIdfCol = mongodb.getCollection("tfIdfCol");
		int idfSize = idfCursor.size();
		int i = 1;
		while(idfCursor.hasNext()) {
			DBObject idf = idfCursor.next();
			log.info(i++ + "/" + idfSize + " getting idf : " + (String)idf.get("word"));
			idfMap.put((String)idf.get("word"), (Double)idf.get("idf"));
		}
		final DBCursor tfCursor = mongodb.getCollection("tfCol").find().sort(new BasicDBObject("newsUrl", -1));
		
		final List<DBObject> tfIdfList = new ArrayList<>();
		
		final int tfSize = tfCursor.size();
		i = 1;
		
		while(tfCursor.hasNext()) {
			DBObject news = tfCursor.next();
			String newsUrl = (String)news.get("newsUrl");
			log.info(newsUrl + " tf-idf "+ i++ + "/" + tfSize);
			Map<String, Double> tfMap = (Map<String, Double>)news.get("tf");
			Map<String, Double> tfIdfMap = new HashMap<>();
			tfMap.forEach((k, v)->{
				tfIdfMap.put(k, v * idfMap.get(k));
			});
			
			DBObject tfIdfObj = new BasicDBObject("newsUrl", newsUrl)
					.append("tfIdf", tfIdfMap);
			tfIdfList.add(tfIdfObj);
			
			if(i % 1000 == 0) {
				log.info("hit " + i + " : inserting");
				tfIdfCol.insert(tfIdfList);
				tfIdfList.clear();
			}
		}
		tfIdfCol.insert(tfIdfList);
		
	}
	
	
	

	@Override
	public DBCursor getTfIdf() throws Exception {
		return mongodb.getCollection("tfIdfCol").find().sort(new BasicDBObject("newsUrl", -1));
	}

	@Override
	public void updateDf(Map<String, Long> df) throws Exception {
		if(!mongodb.collectionExists("dfCol")) {
			mongodb.createCollection("dfCol").createIndex(new BasicDBObject("word", 1), new BasicDBObject("unique", true));
		}
		log.info("update df start");
		final long totalWords = mongodb.getCollection("news").count();
		
		int size = df.size();
		int i = 1;
		for(String k : df.keySet()) {
			Long v = df.get(k);
			
			// df update
			DBObject query = new BasicDBObject("word", k);
			log.info("incrementing : " + k);
			DBObject update = new BasicDBObject("$inc", new BasicDBObject("cnt", v.longValue()));
			mongodb.getCollection("dfCol").update(query, update, true, false);
			// idf update
			log.info(i++ + "/" + size + "updating idf : " + k);
			DBObject after = mongodb.getCollection("dfCol").findOne(query);
			double cnt = ((Long)after.get("cnt")).doubleValue();
			double idf = Math.log(totalWords / cnt);
			
			update = new BasicDBObject("word", k).append("idf", idf);
			mongodb.getCollection("idfCol").update(query, update, true, false);
		}
		
		
	}

	@Override
	public Map<String, Double> getIdf(Set<String> keySet) throws Exception {
		log.info(this.getClass().getName() + ".getIdf start");
		Map<String, Double> rMap = new HashMap<>();
		DBObject query = new BasicDBObject("word", new BasicDBObject("$in", keySet));
		DBCursor cursor = mongodb.getCollection("idfCol").find(query);
		int size = cursor.size();
		int i = 1;
		for(DBObject res : cursor) {
			rMap.put((String)res.get("word"), (Double)res.get("idf"));
			log.info(i++ + "/" + size + " getting idf : " + (String)res.get("word"));
		}
		return rMap;
	}

	@Override
	public void insertNewsTfIdfAll(List<DBObject> tfList) throws Exception {
		log.info(this.getClass().getName() + ".insertTfIdfAll start");
		if(!mongodb.collectionExists("tfIdfCol")) {
			mongodb.createCollection("tfIdfCol").createIndex(new BasicDBObject("newsUrl", 1), new BasicDBObject("unique", true));
		}
		mongodb.getCollection("tfIdfCol").insert(tfList);
	}
	



	

}
