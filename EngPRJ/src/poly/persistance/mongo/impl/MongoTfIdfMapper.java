package poly.persistance.mongo.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

import poly.persistance.mongo.IMongoTfIdfMapper;

@Component("MongoTfIdfMapper")
public class MongoTfIdfMapper implements IMongoTfIdfMapper {

	@Autowired
	private MongoTemplate mongodb;

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void insertIdf(SortedMap<String, Double> idf) throws Exception {

		if (mongodb.collectionExists("idfCol")) {
			mongodb.dropCollection("idfCol");
		}
		mongodb.createCollection("idfCol").createIndex(new BasicDBObject("word", 1));
		idf.forEach((k, v) -> {
			Map<String, Object> idfMap = new HashMap<>();
			idfMap.put("word", k);
			idfMap.put("idf", v);
			mongodb.insert(idfMap, "idfCol");
		});
	}
}
