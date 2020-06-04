package poly.persistance.mongo.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import config.Mapper;
import poly.dto.WordDTO;
import poly.dto.WordQuizDTO;
import poly.persistance.mongo.IMongoNewsWordMapper;
import poly.util.WebCrawler;

import static poly.util.CmmUtil.nvl;

@Mapper("MongoNewsWordMapper")
public class MongoNewsWordMapper implements IMongoNewsWordMapper {

	private static final String WORD_POOL = "wordPool";
	private static final String WORD_USAGE = "wordUsage";
	private static final String QUIZ_RECORD = "quizRecord";
	private static final String REVIEW_WORDS = "reviewWords";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	private static final Random R = new Random();

	Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private MongoTemplate mongodb;

	@Override
	public void insertWords(List<WordDTO> words) throws Exception {

		createCollection(WORD_POOL);
		mongodb.insert(words, WORD_POOL);

	}

	public void createCollection(String colNm) throws Exception {
		log.info(this.getClass().getName() + ".createCollection start");

		if (!mongodb.collectionExists(colNm)) {
			mongodb.createCollection(colNm).createIndex(new BasicDBObject("word", 1), "wordIdx");
		}

		log.info(this.getClass().getName() + ".createCollection end");

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> getWordPool() throws Exception {
		log.info(this.getClass().getName() + ".getWordPool start");

		DBObject query = new BasicDBObject("pool",
				new BasicDBObject("$in", Arrays.asList("TOEIC", "Academic", "Business")));
		DBCursor cursor = mongodb.getCollection(WORD_POOL).find(query);

		Map<String, List<String>> rMap = new HashMap<>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			rMap.put((String) obj.get("word"), (List<String>) obj.get("pool"));
		}

		log.info(this.getClass().getName() + ".getWordPool end");
		return rMap;
	}

	// saving word usage to mongo
	@Override
	public void saveWordUsage(List<WordQuizDTO> rList) throws Exception {

		if (!mongodb.collectionExists(WORD_USAGE)) {
			mongodb.createCollection(WORD_USAGE).createIndex(new BasicDBObject("lemma", 1), "lemmaIdx");
		}
		List<Map<String, String>> pList = new ArrayList<Map<String, String>>();
		Map<String, String> pMap = null;

		for (WordQuizDTO wDTO : rList) {

			// check for duplicate
			DBObject query = new BasicDBObject().append("lemma", wDTO.getLemma()).append("sentence",
					wDTO.getOriginalSentence());
			DBCursor queryRes = mongodb.getCollection(WORD_USAGE).find(query);

			// if not duplicate
			if (!queryRes.hasNext()) {
				pMap = new HashMap<String, String>();
				pMap.put("lemma", wDTO.getLemma());
				pMap.put("word", wDTO.getAnswer());
				pMap.put("sentence", wDTO.getOriginalSentence());
				pMap.put("translation", wDTO.getTranslation());
				pList.add(pMap);
				pMap = null;
			}

		}
		mongodb.insert(pList, WORD_USAGE);
	}

	@Override
	public void insertQuizRecord(Map<String, String> rMap) throws Exception {
		if (!mongodb.collectionExists(QUIZ_RECORD)) {
			mongodb.createCollection(QUIZ_RECORD).createIndex(new BasicDBObject("word", 1), "wordIdx");
		}
		DBObject obj = new BasicDBObject()
				.append("user_seq", Integer.parseInt(rMap.get("user_seq")))
				.append("user_lvl", Integer.parseInt(rMap.get("user_lvl")))
				.append("word", rMap.get("lemma"))
				.append("correct", Integer.parseInt(rMap.get("result")));

		mongodb.insert(obj, QUIZ_RECORD);
	}

	@Override
	public void insertReviewWord(Map<String, String> rMap) throws Exception {
		if (!mongodb.collectionExists(REVIEW_WORDS)) {
			mongodb.createCollection(REVIEW_WORDS).createIndex(new BasicDBObject("word", 1), "wordIdx");
		}
		int user_seq = Integer.parseInt(rMap.get("user_seq"));

		DBObject query = new BasicDBObject().append("user_seq", user_seq).append("word", rMap.get("lemma"));

		DBCursor queryRes = mongodb.getCollection(REVIEW_WORDS).find(query);
		if (queryRes.hasNext()) {
			log.info("word already exists in review collection");
		} else {
			// correctCounter : user needs to answer this amount of review questions
			// correctly
			// in order to remove this word from the review word collection
			DBObject obj = new BasicDBObject().append("user_seq", user_seq).append("word", rMap.get("lemma"))
					.append("correctCounter", 2).append("updDt", Integer.parseInt(SDF.format(new Date())));

			mongodb.insert(obj, REVIEW_WORDS);

		}

	}

	@Override
	public List<Map<String, Object>> getReviewWords(String user_seq) throws Exception {
		DBObject query = new BasicDBObject().append("user_seq", Integer.parseInt(user_seq)).append("updDt",
				new BasicDBObject("$lt", Integer.parseInt(SDF.format(new Date()))));

		DBCursor queryRes = mongodb.getCollection(REVIEW_WORDS).find(query);
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		while (queryRes.hasNext()) {
			DBObject obj = queryRes.next();
			Map<String, Object> pMap = new HashMap<String, Object>();
			log.info("reviewWord : " + nvl((String) obj.get("word")));
			pMap.put("word", nvl((String) obj.get("word")));
			pMap.put("correctCounter", (Integer) obj.get("correctCounter"));
			rList.add(pMap);
		}
		return rList;
	}

	@Override
	public WordQuizDTO getRandomUsage(String word) throws Exception {
		DBObject query = new BasicDBObject("lemma", word);
		DBCursor queryRes = mongodb.getCollection(WORD_USAGE).find(query);
		int count = queryRes.count();
		int randomIdx = R.nextInt(count);
		for (int i = 0; i < randomIdx; i++) {
			queryRes.next();
		}
		DBObject resObj = queryRes.next();
		if (resObj != null) {
			String originalWord = (String) resObj.get("word");
			String originalSentence = (String) resObj.get("sentence");
			String translation = (String) resObj.get("translation");
			String sentence = originalSentence.replace(originalWord, originalWord.substring(0, 2) + "_____");
			String answerSentence = originalSentence.replace(originalWord,
					"<span class='hl'>" + originalWord + "</span>");

			WordQuizDTO rDTO = new WordQuizDTO();

			rDTO.setAnswer(originalWord);
			rDTO.setSentence(sentence);
			rDTO.setOriginalSentence(originalSentence);
			rDTO.setAnswerSentence(answerSentence);
			rDTO.setLemma(word);
			rDTO.setTranslation(translation);
			return rDTO;
		} else {
			return null;
		}

	}

	@Override
	public void updateCorrectCounter(Map<String, String> rMap) throws Exception {
		int user_seq = Integer.parseInt(rMap.get("user_seq"));

		DBObject query = new BasicDBObject().append("user_seq", user_seq).append("word", rMap.get("lemma"));

		DBObject queryRes = mongodb.getCollection(REVIEW_WORDS).find(query).one();

		int res = Integer.parseInt(rMap.get("result"));

		int correctCounter = (Integer) queryRes.get("correctCounter");
		if (!(res == 0 && correctCounter == 2)) {

			correctCounter = res == 1 ? correctCounter - 1 : 2;

			if (correctCounter < 1) {
				mongodb.getCollection(REVIEW_WORDS).remove(queryRes);
			} else {
				queryRes.put("correctCounter", correctCounter);
				mongodb.getCollection(REVIEW_WORDS).update(query, queryRes);
			}
		}

	}

	// for inserting meaning
//	@Override
//	public void insertMeaning() throws Exception {
//
//		DBObject wordPoolQuery = new BasicDBObject("pool",
//				new BasicDBObject("$in", Arrays.asList("TOEIC", "Academic", "Business")));
//		wordPoolQuery.put("meaning", null);
//		DBCursor words = mongodb.getCollection(WORD_POOL).find(wordPoolQuery);
//		int i = 0;
//		while (words.hasNext()) {
//			DBObject wordObj = words.next();
//			String word = (String) wordObj.get("word");
//			log.info(i + ". inserting meaning : " + word);
//			DBObject query = new BasicDBObject("word", word);
//
//			String meaning = (String) wordObj.get("meaning");
//			wordObj.put("meaning", meaning);
//			mongodb.getCollection(WORD_POOL).update(query, wordObj);
//
//			i++;
//		}
//
//	}
	
	// for splitting meaning, limiting up to three meanings
	@Override
	public void insertMeaning() throws Exception {

		DBObject wordPoolQuery = new BasicDBObject("pool",
				new BasicDBObject("$in", Arrays.asList("TOEIC", "Academic", "Business")));
		DBCursor words = mongodb.getCollection(WORD_POOL).find(wordPoolQuery);
		int i = 0;
		while (words.hasNext()) {
			DBObject wordObj = words.next();
			String word = (String) wordObj.get("word");
			log.info(i + ". inserting meaning : " + word);
			DBObject query = new BasicDBObject("word", word);
			String meaning = (String) wordObj.get("meaning");
			String meaningSplit = meaning.split("\\. ")[0].replaceAll("<br>", "<br><br>");
			wordObj.put("meaning", meaningSplit);
			mongodb.getCollection(WORD_POOL).update(query, wordObj);

			i++;
		}
	}

	@Override
	public List<Map<String, String>> getWrongWordMeaning(List<String> wrongWords) throws Exception {

		DBObject query = new BasicDBObject("word", new BasicDBObject("$in", wrongWords));

		DBCursor cursor = mongodb.getCollection(WORD_POOL).find(query);
		List<Map<String, String>> rList = new ArrayList<Map<String, String>>();

		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			Map<String, String> pMap = new HashMap<>();
			pMap.put("word", (String) obj.get("word"));
			pMap.put("meaning", (String) obj.get("meaning"));
			rList.add(pMap);
		}
		return rList;
	}

}
