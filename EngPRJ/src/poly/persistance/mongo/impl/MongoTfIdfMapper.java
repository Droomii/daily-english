package poly.persistance.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import poly.persistance.mongo.IMongoTfIdfMapper;

@Component("MongoTfIdfMapper")
public class MongoTfIdfMapper implements IMongoTfIdfMapper{

	@Autowired
	private MongoTemplate mongodb;
	
	Logger log = Logger.getLogger(this.getClass());
}
