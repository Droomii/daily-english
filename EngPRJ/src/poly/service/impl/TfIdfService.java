package poly.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
		List<NewsDTO> articles = mongoNewsMapper.getAllArticles();

		int docCnt = articles.size();
		// 기사별 단어 빈도수 계산
		List<Map<String, Integer>> wordCount = articles.stream().map(a -> {
			Map<String, Integer> wc = new HashMap<String, Integer>();
			for (List<String> words : a.getLemmas()) {
				for (String word : words) {
					word = word.replaceAll("\\W+", "").replaceAll("[0-9]", "");
					if (word.length() > 0) {
						wc.compute(word.toLowerCase(), (k, v) -> (v == null) ? 1 : v + 1);
					}
				}
			}
			return wc;
		}).collect(Collectors.toList());

		// TF 계산
		List<Map<String, Double>> tf = wordCount.stream().map(a->{
			Map<String, Double> tfMap = new HashMap<>();
			int wordCnt = a.values().stream().mapToInt(i -> i).sum();
			a.forEach((k, v)->{
				tfMap.put(k, v.doubleValue()/wordCnt);
			});
			return tfMap;
		}).collect(Collectors.toList());
		
		// Document Frequency
		Map<String, Integer> df = new HashMap<>();
		wordCount.stream().flatMap(a -> a.entrySet().stream()).forEach(a -> {
			df.compute(a.getKey(), (k, v) -> (v == null) ? 1 : v + 1);
		});
		
		// IDF
		Map<String, Double> idf = new HashMap<>();
		df.forEach((k, v)->{
			idf.put(k, Math.log(docCnt / v.doubleValue()));
		});
		
		// TF-IDF
		List<Map<String, Double>> tfIdf = tf.stream().map(a ->{
			Map<String, Double> tfIdfMap = new HashMap<>();
			a.forEach((k, v)->{
				tfIdfMap.put(k, v * idf.get(k));
			});
			tfIdfMap.entrySet().stream().sorted((o1, o2)->Double.compare(o1.getValue(), o2.getValue())).limit(tfIdfMap.size()-20).forEach(kv ->{
				tfIdfMap.remove(kv.getKey());
			});
			return tfIdfMap;	
		}).collect(Collectors.toList());
		return tfIdf;
	}
}
