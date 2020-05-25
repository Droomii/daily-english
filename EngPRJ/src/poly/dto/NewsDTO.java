package poly.dto;

import static poly.util.CmmUtil.nvl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.DBObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
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

	
	Logger log = Logger.getLogger(this.getClass());
	
	public NewsDTO() {
	}

	public NewsDTO(String newsTitle, CoreDocument doc, String newsUrl) throws Exception {

		this.newsTitle = newsTitle;
		this.newsUrl = newsUrl;

		// attributes initialization
		this.tokens = new ArrayList<List<String>>();
		this.pos = new ArrayList<List<String>>();
		this.originalSentences = new ArrayList<String>();
		this.lemmas = new ArrayList<List<String>>();

		// iterating through sentence
		for (Iterator<CoreSentence> it = doc.sentences().iterator(); it.hasNext();) {
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
		this.insertDate = new Date();
	}

	public void translate() throws Exception {
		this.translation = TranslateUtil.translateNews(this.originalSentences);
		this.translatedTitle = TranslateUtil.translateTitle(this.newsTitle);
	}

	@SuppressWarnings("unchecked")
	public NewsDTO(DBObject firstNews) {

		this.newsTitle = nvl((String) firstNews.get("newsTitle"));
		this.translatedTitle = nvl((String) firstNews.get("translatedTitle"));
		this.originalSentences = (List<String>) firstNews.get("originalSentence");
		this.translation = (List<String>) firstNews.get("translation");
		this.tokens = (List<List<String>>) firstNews.get("tokens");
		this.lemmas = (List<List<String>>) firstNews.get("lemmas");
		this.pos = (List<List<String>>) firstNews.get("pos");
		this.insertDate = (Date) firstNews.get("insertDate");
		this.newsUrl = nvl((String) firstNews.get("newsUrl"));
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
	public void highlightWords(List<Map<String, Object>> extractedWords) {
		
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

	
}
