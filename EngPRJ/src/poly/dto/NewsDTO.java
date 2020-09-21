package poly.dto;

import static poly.util.CmmUtil.nvl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mongodb.DBObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.util.TranslateUtil;

public class NewsDTO {

	private String newsTitle;
	private String translatedTitle;
	private List<String> originalSentences;
	private List<String> translation;
	private List<List<String>> tokens;
	private List<List<String>> lemmas;
	private List<List<String>> pos;
	private Date insertDate;
	private String newsUrl;
	private final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	
	Logger log = Logger.getLogger(this.getClass());
	
	public NewsDTO() {}
	
	public NewsDTO(String[] info, boolean translate) throws Exception {
		this(info[0], info[1], info[2], translate);
	}
	
	public NewsDTO(String newsTitle, String article, String newsUrl, boolean translate) throws Exception {
		// set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
	    props.setProperty("coref.algorithm", "neural");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(article);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    this.newsTitle = newsTitle;
		this.newsUrl = newsUrl;

		// attributes initialization
		this.tokens = new ArrayList<List<String>>();
		this.pos = new ArrayList<List<String>>();
		this.originalSentences = new ArrayList<String>();
		this.lemmas = new ArrayList<List<String>>();

		// iterating through sentence
		for (Iterator<CoreSentence> it = document.sentences().iterator(); it.hasNext();) {
			CoreSentence sentence = it.next();
			originalSentences.add(sentence.text());

			// adding tokens, lemmas by sentence
			List<String> token = new ArrayList<String>();
			List<String> lemma = new ArrayList<String>();
			List<String> posBySent = new ArrayList<String>();
			for (Iterator<CoreLabel> tokenIt = sentence.tokens().iterator(); tokenIt.hasNext();) {
				CoreLabel tempToken = tokenIt.next();
				token.add(tempToken.originalText());
				lemma.add(tempToken.lemma());
			}

			// adding pos tags by sentence
			posBySent.addAll(sentence.posTags());

			// adding nlp data split by sentence
			tokens.add(token);
			pos.add(posBySent);
			lemmas.add(lemma);
		}
		if(translate) {
			translate();
		}
		this.insertDate = SDF.parse(newsUrl.substring(0, 8));
	}
	
	

	public void translate() throws Exception {
		this.translation = new ArrayList<>();
		for(String origSent : this.originalSentences) {
			this.translation.add(TranslateUtil.translateKakao(origSent));
		}
		this.translatedTitle = TranslateUtil.translateKakao(this.newsTitle);
	}

	@SuppressWarnings("unchecked")
	public NewsDTO(DBObject firstNews) {

		this.newsTitle = nvl((String) firstNews.get("newsTitle"));
		this.translatedTitle = nvl((String) firstNews.get("translatedTitle"));
		this.originalSentences = (List<String>) firstNews.get("originalSentences");
		this.translation = (List<String>) firstNews.get("translation");
		this.tokens = (List<List<String>>) firstNews.get("tokens");
		this.lemmas = (List<List<String>>) firstNews.get("lemmas");
		this.pos = (List<List<String>>) firstNews.get("pos");
		this.insertDate = (Date) firstNews.get("insertDate");
		this.newsUrl = nvl((String) firstNews.get("newsUrl"));
	}

	@Override
	public String toString() {
		return "NewsDTO [newsTitle=" + newsTitle + ", translatedTitle=" + translatedTitle + ", originalSentences="
				+ originalSentences + ", translation=" + translation + ", tokens=" + tokens + ", lemmas=" + lemmas
				+ ", pos=" + pos + ", insertDate=" + insertDate + ", newsUrl=" + newsUrl + "]";
	}

	public List<List<String>> getLemmas() {
		return lemmas;
	}

	public void setLemmas(List<List<String>> lemmas) {
		this.lemmas = lemmas;
	}

	public List<String> getOriginalSentences() {
		return originalSentences;
	}

	public void setOriginalSentences(List<String> originalSentence) {
		this.originalSentences = originalSentence;
	}

	public List<String> getTranslation() {
		return translation;
	}

	public void setTranslation(List<String> translation) {
		this.translation = translation;
	}

	public List<List<String>> getPos() {
		return pos;
	}

	public void setPos(List<List<String>> pos) {
		this.pos = pos;
	}

	public List<List<String>> getTokens() {
		return tokens;
	}

	public void setTokens(List<List<String>> tokens) {
		this.tokens = tokens;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getTranslatedTitle() {
		return translatedTitle;
	}

	public void setTranslatedTitle(String translatedTitle) {
		this.translatedTitle = translatedTitle;
	}
	
	/**
	 * highlights essential words of sentences
	 * @param extractedWords : extracted essential words
	 */
	public void highlightAllWords(List<Map<String, Object>> extractedWords) {
		
		for(Map<String, Object> extractedWord : extractedWords) {
			
			// get index of sentence and token
			int sntncIdx = (Integer)extractedWord.get("sntncIdx");
			int wordIdx = (Integer)extractedWord.get("wordIdx");
			String originalWord = this.tokens.get(sntncIdx).get(wordIdx);
			String originalSentence = this.originalSentences.get(sntncIdx);
			String highlightedSentence = originalSentence.replace(originalWord, "<span class='hl'>" + originalWord + "</span>");
			this.originalSentences.set(sntncIdx, highlightedSentence);
			
		}
		
	}
	
	
	
	public void hideAllWords(List<Map<String, Object>> extractedWords) {
		
		for(Map<String, Object> extractedWord : extractedWords) {
			
			// get index of sentence and token
			int sntncIdx = (Integer)extractedWord.get("sntncIdx");
			int wordIdx = (Integer)extractedWord.get("wordIdx");
			String originalWord = this.tokens.get(sntncIdx).get(wordIdx);
			String originalSentence = this.originalSentences.get(sntncIdx);
			String highlightedSentence = originalSentence.replace(originalWord, "_______");
			this.originalSentences.set(sntncIdx, highlightedSentence);
			
		}
		
	}
	
	public void showOnlyFirstLetterAll(List<Map<String, Object>> extractedWords) {
		
		for(Map<String, Object> extractedWord : extractedWords) {
			
			// get index of sentence and token
			int sntncIdx = (Integer)extractedWord.get("sntncIdx");
			int wordIdx = (Integer)extractedWord.get("wordIdx");
			String originalWord = this.tokens.get(sntncIdx).get(wordIdx);
			String originalSentence = this.originalSentences.get(sntncIdx);
			String highlightedSentence = originalSentence.replace(originalWord, originalWord.substring(0, 2)+"_______");
			this.originalSentences.set(sntncIdx, highlightedSentence);
			
		}
		
	}
	
	public List<WordQuizDTO> generateProblems(List<Map<String, Object>> extractedWords){
		
		List<WordQuizDTO> rList = new ArrayList<WordQuizDTO>();
		
		WordQuizDTO pDTO = null;
		Set<String> wordSet = new HashSet<String>(); 
		
		for(Map<String, Object> extractedWord : extractedWords) {
			pDTO = new WordQuizDTO();
			// get index of sentence and token
			int sntncIdx = (Integer)extractedWord.get("sntncIdx");
			int wordIdx = (Integer)extractedWord.get("wordIdx");
			String lemma = this.lemmas.get(sntncIdx).get(wordIdx).toLowerCase();
			
			if(!wordSet.contains(lemma)) {
			
				String originalWord = this.tokens.get(sntncIdx).get(wordIdx);
				String originalSentence = this.originalSentences.get(sntncIdx);
				String translation = this.translation.get(sntncIdx);
				String sentence = originalSentence.replace(originalWord, originalWord.substring(0, 2)+"_____");
				String answerSentence = originalSentence.replace(originalWord, "<span class='hl'>" + originalWord + "</span>");
				pDTO.setAnswer(originalWord);
				pDTO.setSentence(sentence);
				pDTO.setOriginalSentence(originalSentence);
				pDTO.setAnswerSentence(answerSentence);
				pDTO.setLemma(lemma);
				pDTO.setTranslation(translation);
				rList.add(pDTO);
				wordSet.add(lemma);
				pDTO = null;
			}
		}
	
		return rList;
	}
	
	public Map<String, Object> toMap(){
		Map<String, Object> rMap = new HashMap<>();
		
		rMap.put("newsTitle", newsTitle);
		rMap.put("translatedTitle", translatedTitle);
		rMap.put("originalSentences",originalSentences);
		rMap.put("translation",translation);
		rMap.put("tokens", tokens);
		rMap.put("lemmas",lemmas);
		rMap.put("pos",pos);
		rMap.put("insertDate",insertDate);
		rMap.put("newsUrl",newsUrl);
		
		return rMap;
	}

	
}
