package poly.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import poly.dto.NewsDTO;
import poly.dto.WordDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.persistance.mongo.IMongoNewsWordMapper;
import poly.service.INewsWordService;

@Service("NewsWordService")
public class NewsWordService implements INewsWordService{
	
	@Resource(name = "MongoNewsWordMapper")
	IMongoNewsWordMapper mongoNewsWordMapper;
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;

	private Map<String, List<String>> WORD_POOL = new HashMap<>();
	
	Logger log = Logger.getLogger(this.getClass());

	/** 단어 데이터 초반에 불러오기
	 *
	 */
	@Override
	@PostConstruct
	public void loadWordPool() throws Exception {
		
		log.info(this.getClass().getName() + ".loadWordPool start");
		
		WORD_POOL = mongoNewsWordMapper.getWordPool();
		
		log.info(this.getClass().getName() + ".loadWordPool end");
		
	}
	
	
	@Override
	public void insertWords(List<WordDTO> words) throws Exception {
		
		mongoNewsWordMapper.insertWords(words);
		
	}

	/** @return : [{word : (String)"단어", pool : (List of String)"속한 풀", sntncIdx : (Integer)"문장 인덱스", wordIdx : (Integer)"단어 인덱스"}]
	 *
	 */
	@Override
	public List<Map<String, Object>> extractWords(NewsDTO pDTO) throws Exception {
		
		log.info(this.getClass().getName() + ".extractWords start");
		
		List<Map<String, Object>> rList = new ArrayList<>();
		Map<String, Object> pMap = null;
		
		int sntncIdx = 0;
		for(List<String> wordsBySentence : pDTO.getLemmas()) {
			int wordIdx = 0;
			for(String word : wordsBySentence) {
				if(WORD_POOL.containsKey(word.toLowerCase())) {
					pMap = new HashMap<String, Object>();
					pMap.put("word", word);
					pMap.put("pool", WORD_POOL.get(word.toLowerCase()));
					pMap.put("sntncIdx", sntncIdx);
					pMap.put("wordIdx", wordIdx);
					rList.add(pMap);
					pMap = null;
				}
				wordIdx++;
			}
			sntncIdx++;
		}
		
		log.info(this.getClass().getName() + ".extractWords end");
		return rList;
	}


	@Override
	public void highlightWords(NewsDTO news) throws Exception {
		
		List<Map<String, Object>> extractedWords = extractWords(news);
		
		news.highlightWords(extractedWords);
		
	}

	
	
	

}
