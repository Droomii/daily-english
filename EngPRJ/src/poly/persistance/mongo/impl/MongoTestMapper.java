package poly.persistance.mongo.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

import poly.dto.TestWordDTO;
import poly.persistance.mongo.IMongoTestMapper;

@Component("MongoTestMapper")
public class MongoTestMapper implements IMongoTestMapper{
	
	@Autowired
	private MongoTemplate mongodb;
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public boolean createCollection(String colNm) throws Exception {
		
		log.info(this.getClass().getName() + ".createCollection start");
		
		boolean res = false;
		
		if(mongodb.collectionExists(colNm)) {
			mongodb.dropCollection(colNm);
		}
		
		mongodb.createCollection(colNm).createIndex(new BasicDBObject("user_id", 1), "testIdx");
		
		res = true;
		
		log.info(this.getClass().getName() + ".createCollection end");
		
		return res;
	}

	@Override
	public void insertWords() throws Exception {
		// TODO Auto-generated method stub
		
	}
	/*
	@Override
	public void insertWords() throws Exception {
		log.info(this.getClass().getName() + ".insertWords start");
		String colNm = "testWords";
		
		if(mongodb.collectionExists(colNm)) {
			mongodb.dropCollection(colNm);
		}
		
		mongodb.createCollection(colNm).createIndex(new BasicDBObject("no", 1), "wordIdx");
		
		List<TestWordDTO> pList = new ArrayList<TestWordDTO>();
		
		BufferedReader bf = new BufferedReader(new FileReader("c:/NGSLT_TEST_SEPARATED.TXT"));
		String line;
		TestWordDTO tDTO = null;
		while((line = bf.readLine()) != null) {
			tDTO = new TestWordDTO();
			String[] wordInfo = line.split("\\|");
			tDTO.setNo(wordInfo[0]);
			tDTO.setWord(wordInfo[1].split(":")[0].trim());
			
			Pattern p = Pattern.compile("(?i)" + tDTO.getWord()+"\\w*");
			
			String sentence = wordInfo[1].split(":")[1].trim();
			
			Matcher m = p.matcher(sentence);
			
			if(m.find()) {
				tDTO.setSentence(sentence.replaceAll(m.group(), "<span class='stress'>" + m.group() + "</span>"));
			}else {
				log.info("no match : " + tDTO.getNo() + " - " + tDTO.getWord());
				tDTO.setSentence(sentence);
			}
			
			
			tDTO.setA(wordInfo[2].trim());
			tDTO.setB(wordInfo[3].trim());
			tDTO.setC(wordInfo[4].trim());
			tDTO.setD(wordInfo[5].trim());
			tDTO.setAnswer(wordInfo[6].trim());
			
			pList.add(tDTO);
		}
		bf.close();
		mongodb.insert(pList, colNm);
		
	}
	
	*/

}
