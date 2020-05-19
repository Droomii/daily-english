package poly.dto;

import static poly.util.CmmUtil.nvl;

import com.mongodb.DBObject;

/**
 * 실력 측정을 위한 단어를 담는 DTO
 * @author Droomii
 *
 */
public class TestWordDTO {

	private String no;
	private String word;
	private String sentence;
	private String a;
	private String b;
	private String c;
	private String d;
	private String answer;
	private String finalLevel;
	
	public String getFinalLevel() {
		return finalLevel;
	}

	public void setFinalLevel(String finalLevel) {
		this.finalLevel = finalLevel;
	}

	/**
	 * @param rWord : MongoDB로부터 받은 단어 정보를 DBObject로 전달하면 알아서 속성에 넣어줌
	 */
	public TestWordDTO(DBObject rWord) {
		setNo(nvl((String) rWord.get("no")));
		setWord(nvl((String) rWord.get("word")));
		setSentence(nvl((String) rWord.get("sentence")));
		setA(nvl((String) rWord.get("a")));
		setB(nvl((String) rWord.get("b")));
		setC(nvl((String) rWord.get("c")));
		setD(nvl((String) rWord.get("d")));
		setAnswer(nvl((String) rWord.get("answer")));

	}
	
	public TestWordDTO() {
		
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
