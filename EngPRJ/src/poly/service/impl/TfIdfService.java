package poly.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mongodb.DBCursor;

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
		DBCursor articles = mongoNewsMapper.getAllArticles();
		log.info("article size : " + articles.size());
		int docCnt = articles.size();
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
				}
			}
			// Document Frequency
			mongoNewsMapper.incrementDf(wc.keySet());
			wordCount.add(wc);
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
}
