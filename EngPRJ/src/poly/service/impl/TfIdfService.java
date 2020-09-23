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
	public List<Map<String, Double>> getAllArticles() throws Exception {
		log.info("service.getAllArticles start");
		long start = System.currentTimeMillis();
		DBCursor articles = mongoNewsMapper.getAllArticles();
		
		log.info("article size : " + articles.size());
		long end = System.currentTimeMillis();
		log.info("elapsed : " + (end - start));
		
		// 기사별 단어 빈도수 계산
		List<Map<String, Integer>> wordCount = new ArrayList<>();
		// df 담을 맵
		SortedMap<String, Long> df = new TreeMap<>();
		int i = 1;
		while(articles.hasNext()) {
			NewsDTO nDTO = new NewsDTO(articles.next());
			log.info("counting " + i++ + ": " + nDTO.getNewsTitle());
			Map<String, Integer> wc = new HashMap<String, Integer>();
			for (List<String> words : nDTO.getLemmas()) {
				for (String word : words) {
					word = word.replaceAll("[^A-Za-z0-9-]", "");
					if (word.length() > 0) {
						wc.compute(word.toLowerCase(), (k, v) -> (v == null) ? 1 : v + 1);
					}
					df.compute(word, (k, v)->(v==null) ? 1 : v+1);
				}
			}
			// Document Frequency
			mongoNewsMapper.incrementDf(wc.keySet());
			wordCount.add(wc);
		}

		if(true) {
			throw new Exception();
		}
		/*
		// TF 계산
		List<Map<String, Double>> tf = wordCount.stream().map(a->{
			Map<String, Double> tfMap = new HashMap<>();
			int wordCnt = a.values().stream().mapToInt(i -> i).sum();
			a.forEach((k, v)->{
				tfMap.put(k, v.doubleValue()/wordCnt);
			});
			return tfMap;
		}).collect(Collectors.toList());
		Map<String, Double> last = tf.get(0); 
		System.out.println(last);
		
		
		System.out.println("df of coronavirus : " + df.get("coronavirus"));
//		df.forEach((k, v)->{System.out.print(k + " ");System.out.println(v);});
		// IDF
		SortedMap<String, Double> idf = new TreeMap<>();
		df.forEach((k, v)->{
			idf.put(k, Math.log(docCnt / v.doubleValue()));
		});
		System.out.println("idf of coronavirus : " + idf.get("coronavirus"));
		double tfIdfTest = last.get("coronavirus") * idf.get("coronavirus");
		System.out.println("tf-idf of coronavirus : " + tfIdfTest);
//		idf.forEach((k, v)->{System.out.print(k + " : ");System.out.println(v);});
		
		mongoTfIdfMapper.insertIdf(idf);
		
		// TF-IDF
		List<Map<String, Double>> tfIdf = tf.stream().map(a ->{
			Map<String, Double> tfIdfMap = new HashMap<>();
			a.forEach((k, v)->{
				tfIdfMap.put(k, v * idf.get(k));
			});
			return tfIdfMap;	
		}).collect(Collectors.toList());
		List<Entry<String, Double>> tfIdfLast = new ArrayList<>(tfIdf.get(0).entrySet());
		tfIdfLast.sort((o1, o2)-> Double.compare(o2.getValue(), o1.getValue()));
		System.out.println(tfIdfLast);
		*/
		return null;
	}

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
}
