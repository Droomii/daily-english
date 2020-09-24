package poly.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.persistance.mongo.IMongoTfIdfMapper;
import poly.service.ITfIdfService;

@Service("TfIdfService")
public class TfIdfService implements ITfIdfService {

	@Resource(name = "MongoTfIdfMapper")
	IMongoTfIdfMapper mongoTfIdfMapper;

	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void getDf() throws Exception {
		log.info("service.getAllArticles start");
		long start = System.currentTimeMillis();
		DBCursor articles = mongoNewsMapper.getAllArticles();
		log.info("article size : " + articles.size());
		long end = System.currentTimeMillis();
		log.info("elapsed : " + (end - start));
		int i = 1;
		List<NewsDTO> articleList = new ArrayList<>();
		while(articles.hasNext()) {
//		for(int i = 0 ;i < 100; i++) {
			DBObject obj = articles.next();
			NewsDTO nDTO = new NewsDTO();
			nDTO.setLemmas((List<List<String>>)obj.get("lemmas"));
			nDTO.setNewsTitle((String)obj.get("newsTitle"));
			nDTO.setNewsUrl((String)obj.get("newsUrl"));
			log.info("adding "+ i++ +": " + nDTO.getNewsTitle());
			articleList.add(nDTO);
		}
		// 기사별 단어 빈도수 계산
		List<DBObject> tfList = new ArrayList<>();
		Map<String, Long> df = new HashMap<>();
		// df 담을 맵
		i = 1;
		for(NewsDTO nDTO : articleList) {
			int wordTot = 0;
			log.info("counting " + i++ + ": " + nDTO.getNewsTitle());
			Map<String, Integer> wc = new HashMap<String, Integer>();
			for (List<String> words : nDTO.getLemmas()) {
				for (String word : words) {
					word = word.replaceAll("[^A-Za-z-]", "");
					if (word.length() > 0) {
						wordTot++;
						wc.compute(word.toLowerCase(), (k, v) -> (v == null) ? 1 : v + 1);
					}
				}
			}
			if(wordTot==0) continue;
			
			final int WORD_TOT = wordTot;
			Map<String, Double> tf = new HashMap<>();
			wc.forEach((k, v)->{
				tf.put(k, v.doubleValue() / WORD_TOT);
			});
			String newsUrl = nDTO.getNewsUrl();
			DBObject insertObj = new BasicDBObject("newsUrl", newsUrl)
					.append("tf", tf);
			tfList.add(insertObj);
			// Document Frequency
			for(String word : wc.keySet()) {
				df.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
			}
			if(i%100 ==0) {
				log.info("hit " + i + ": inserting");
				mongoNewsMapper.insertTfAll(tfList);
				tfList.clear();
				
			}
		}
		mongoNewsMapper.insertTfAll(tfList);
//		mongoNewsMapper.insertDf(df);
		
	}

	@Override
	public void insertIdf() throws Exception {
		mongoNewsMapper.insertIdf();
		
	}

	@Override
	public void getTfIdf() throws Exception {
		mongoNewsMapper.insertTfIdf();
	}

	@Override
	public List<DBObject> getTop10() throws Exception {
		DBCursor top10 = mongoNewsMapper.getTfIdf().limit(10);
		return top10.toArray();
	}
}
