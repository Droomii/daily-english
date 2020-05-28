package poly.dto;

public class WordQuizDTO {

	private String sentence;
	private String answerSentence;
	private String answer;
	private String lemma;
	private String tranlation;
	
	public String getTranlation() {
		return tranlation;
	}
	public void setTranslation(String tranlation) {
		this.tranlation = tranlation;
	}
	public String getAnswerSentence() {
		return answerSentence;
	}
	public void setAnswerSentence(String hiddenSentence) {
		this.answerSentence = hiddenSentence;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	
}
