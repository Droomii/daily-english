package poly.dto;

import java.util.ArrayList;
import static poly.util.CmmUtil.nvl;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mongodb.DBObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import poly.util.TranslateUtil;

public class NewsDTO {

	private String newsTitle;
	private List<String> originalSentence;
	private List<String> translation;
	private List<List<String>> tokens;
	private List<List<String>> lemmas;
	private List<List<String>> pos;
	private Date insertDate;
	private String newsUrl;

	public NewsDTO() {}
	
	public NewsDTO(String newsTitle, CoreDocument doc, String newsUrl) throws Exception {
		
		this.newsTitle = newsTitle;
		this.newsUrl = newsUrl;
		
		// attributes initialization
		this.tokens = new ArrayList<List<String>>();
		this.pos = new ArrayList<List<String>>();
		this.originalSentence = new ArrayList<String>();
		this.lemmas = new ArrayList<List<String>>();
		
		
		// iterating through sentence
	    for(Iterator<CoreSentence> it = doc.sentences().iterator(); it.hasNext();) {
	    	CoreSentence sentence = it.next();
	    	originalSentence.add(sentence.text());
	    	
	    	// adding tokens, lemmas by sentence
	    	List<String> token = new ArrayList<String>();
	    	List<String> lemma = new ArrayList<String>();
	    	List<String> posBySent = new ArrayList<String>();
	    	for(Iterator<CoreLabel> tokenIt = sentence.tokens().iterator();tokenIt.hasNext();) {
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

	@SuppressWarnings("unchecked")
	public NewsDTO(DBObject firstNews) {

		newsTitle = nvl((String) firstNews.get("newsTitle"));
		originalSentence = (List<String>) firstNews.get("originalSentence");
		tokens = (List<List<String>>) firstNews.get("tokens");
		lemmas = (List<List<String>>) firstNews.get("lemmas");
		pos = (List<List<String>>) firstNews.get("pos");
		insertDate = (Date) firstNews.get("insertDate");
		newsUrl = nvl((String) firstNews.get("newsUrl"));
	}

	public List<List<String>> getLemmas() {
		return lemmas;
	}

	public void setLemmas(List<List<String>> lemmas) {
		this.lemmas = lemmas;
	}

	public List<String> getOriginalSentence() {
		return originalSentence;
	}

	public void setOriginalSentence(List<String> originalSentence) {
		this.originalSentence = originalSentence;
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
	
	
	
}
