package poly.dto;

public class WordQuizDTO {

	private String sentence;
	private String answerSentence;
	private String answer;
	private String lemma;
	private String translation;
	
	public String getTranslation() {
		return translation;
	}
	public void setTranslation(String tranlation) {
		this.translation = tranlation;
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
	@Override
	public String toString() {
		return "WordQuizDTO [sentence=" + sentence + ", answerSentence=" + answerSentence + ", answer=" + answer
				+ ", lemma=" + lemma + ", tranlation=" + translation + "]";
	}
	
	
}