package poly.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

public class NLPDTO {

	private List<List<String>> lemmas;
	private List<String> originalSentence;
	private List<List<String>> pos;
	private List<List<CoreLabel>> tokens;
	
	public NLPDTO() {}
	
	public NLPDTO(CoreDocument doc) {
		
		// attributes initialization
		tokens = new ArrayList<List<CoreLabel>>();
		pos = new ArrayList<List<String>>();
		originalSentence = new ArrayList<String>();
		lemmas = new ArrayList<List<String>>();
		
		
	    for(Iterator<CoreSentence> it = doc.sentences().iterator(); it.hasNext();) {
	    	CoreSentence sentence = it.next();
	    	originalSentence.add(sentence.text());
	    	
	    }
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

	public List<List<String>> getPos() {
		return pos;
	}

	public void setPos(List<List<String>> pos) {
		this.pos = pos;
	}

	public List<List<CoreLabel>> getTokens() {
		return tokens;
	}

	public void setTokens(List<List<CoreLabel>> tokens) {
		this.tokens = tokens;
	}
	
	
	
}
