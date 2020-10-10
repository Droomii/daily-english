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

		// 기사별 단어 빈도수 계산
		
		// df 담을 맵
		List<DBObject> tfList = new ArrayList<>();
		Map<String, Long> df = new HashMap<>();
		
		while(articles.hasNext()) {
			DBObject obj = articles.next();
			NewsDTO nDTO = new NewsDTO();
			nDTO.setLemmas((List<List<String>>) obj.get("lemmas"));
			nDTO.setNewsTitle((String) obj.get("newsTitle"));
			nDTO.setNewsUrl((String) obj.get("newsUrl"));
			
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
			if (wordTot == 0)
				continue;

			final int WORD_TOT = wordTot;
			Map<String, Double> tf = new HashMap<>();
			wc.forEach((k, v) -> {
				tf.put(k, v.doubleValue() / WORD_TOT);
			});
			String newsUrl = nDTO.getNewsUrl();
			DBObject insertObj = new BasicDBObject("newsUrl", newsUrl).append("tf", tf);
			tfList.add(insertObj);
			// Document Frequency
			for (String word : wc.keySet()) {
				df.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
			}
			if (i % 100 == 0) {
				log.info("hit " + i + ": inserting");
				mongoNewsMapper.insertNewsTfAll(tfList);
				tfList.clear();

			}
		}
		mongoNewsMapper.insertNewsTfAll(tfList);
		mongoNewsMapper.insertDf(df);

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

	@Override
	public void insertNewArticles(List<NewsDTO> newArticles) throws Exception {

		// 기사별 단어 빈도수 계산
		List<DBObject> tfList = new ArrayList<>();
		Map<String, Long> df = new HashMap<>();
		// df 담을 맵
		int i = 1;
		for (NewsDTO nDTO : newArticles) {
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
			if (wordTot == 0)
				continue;

			final int WORD_TOT = wordTot;
			Map<String, Double> tf = new HashMap<>();
			wc.forEach((k, v) -> {
				tf.put(k, v.doubleValue() / WORD_TOT);
			});
			String newsUrl = nDTO.getNewsUrl();
			DBObject insertObj = new BasicDBObject("newsUrl", newsUrl).append("tf", tf);
			tfList.add(insertObj);
			// Document Frequency
			for (String word : wc.keySet()) {
				df.compute(word, (k, v) -> (v == null) ? 1 : v + 1);
			}
			if (i % 100 == 0) {
				log.info("hit " + i + ": inserting");
				mongoNewsMapper.insertNewsTfAll(tfList);
				tfList.clear();

			}
		}
		mongoNewsMapper.updateDf(df);
		log.info("inserting remaining tf");
		mongoNewsMapper.insertNewsTfAll(tfList);
		log.info("inserting remaining tf done");
		Map<String, Double> idf = mongoNewsMapper.getIdf(df.keySet());
		for(DBObject obj : tfList) {
			Map<String, Double> tf = (Map<String, Double>)obj.get("tf");
			Map<String, Double> tfIdf = new HashMap<>();
			tf.forEach((k, v)->{
				log.info("calc tfidf : " + k);
				tfIdf.put(k, v * idf.get(k));
			});
			obj.put("tfIdf", tfIdf);
			obj.removeField("tf");
		}
		mongoNewsMapper.insertNewsTfIdfAll(tfList);
		
		
	}
}
