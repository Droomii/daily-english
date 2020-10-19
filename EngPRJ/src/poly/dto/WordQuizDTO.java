package poly.dto;

public class WordQuizDTO {

	private int lvl;
	private int idx;
	private String originalSentence;
	private String sentence;
	private String answerSentence;
	private String answer;
	private String lemma;
	private String translation;
	private int correctCounter;
	private int totalQs;
	private int answeredQCount;
	
	public int getLvl() {
		return lvl;
	}
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	public int getCorrectCounter() {
		return correctCounter;
	}
	public void setCorrectCounter(int correctCounter) {
		this.correctCounter = correctCounter;
	}
	public void setTotalQs(int totalQs) {
		this.totalQs = totalQs;
	}
	public int getTotalQs() {
		return totalQs;
	}
	public void setTotalQCount(int totalQs) {
		this.totalQs = totalQs;
	}
	public int getAnsweredQCount() {
		return answeredQCount;
	}
	public void setAnsweredQCount(int answeredQCount) {
		this.answeredQCount = answeredQCount;
	}
	public String getOriginalSentence() {
		return originalSentence;
	}
	public void setOriginalSentence(String originalSentence) {
		this.originalSentence = originalSentence;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
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
				+ ", lemma=" + lemma + ", translation=" + translation + "]";
	}
	
	
}
