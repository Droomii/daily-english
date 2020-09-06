package poly.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			tfMap.entrySet().stream().sorted((o1, o2)-> Double.compare(o2.getValue(), o1.getValue())).limit(tfMap.size()-10).forEach(e ->tfMap.remove(e.getKey())
			);
			return tfMap;
		}).collect(Collectors.toList());
		
		// IDF 계산
		
		
		Map<String, Integer> wordInDocCnt = new HashMap<>();
		wordCount.stream().flatMap(a -> a.entrySet().stream()).forEach(a -> {
			wordInDocCnt.compute(a.getKey(), (k, v) -> (v == null) ? a.getValue() : v + a.getValue());
		});
		return tf;
	}
}
