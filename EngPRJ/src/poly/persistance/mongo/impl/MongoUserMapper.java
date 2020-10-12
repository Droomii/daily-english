package poly.persistance.mongo.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import poly.persistance.mongo.IMongoUserMapper;

@Component("MongoUserMapper")
public class MongoUserMapper implements IMongoUserMapper{

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private MongoTemplate mongodb;

	@Override
	public void insertAttend(String user_seq) throws Exception {
		if(!mongodb.collectionExists("attend")) {
			mongodb.createCollection("attend").createIndex("userSeq");
			mongodb.getCollection("attend").createIndex(new BasicDBObject("date", -1));
		}
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, -7);
		DBObject obj = new BasicDBObject("user_seq", Integer.parseInt(user_seq))
				.append("date", sdf.format(c.getTime()));
		
		mongodb.insert(obj, "attend");
	}

	@Override
	public Set<Integer> getAttend(String user_seq) throws Exception {
		DBObject query = new BasicDBObject("user_seq", Integer.parseInt(user_seq));
		
		DBCursor cursor = mongodb.getCollection("attend").find(query).sort(new BasicDBObject("date", -1));
		String thisMonth = sdf.format(new Date()).substring(0, 6);
		Set<Integer> attendDays = new HashSet<>();
		while(cursor.hasNext()) {
			String date = (String)cursor.next().get("date");
			if(date.startsWith(thisMonth)) {
				attendDays.add(Integer.parseInt(date.substring(6)));
			}else break;
		}
		return attendDays;
	}
}
