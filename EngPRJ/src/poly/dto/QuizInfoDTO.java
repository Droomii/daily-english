package poly.dto;

import java.util.HashMap;
import java.util.Map;

public class QuizInfoDTO {
	private String user_seq;
	private Map<Integer, Boolean> answeredQs;
	
	public QuizInfoDTO() {
		answeredQs = new HashMap<Integer, Boolean>();
	}
	
	public String getUser_seq() {
		return user_seq;
	}
	public void setUser_seq(String user_seq) {
		this.user_seq = user_seq;
	}
	public Map<Integer, Boolean> getAnsweredQs() {
		return answeredQs;
	}
	public void setAnsweredQs(Map<Integer, Boolean> answeredQs) {
		this.answeredQs = answeredQs;
	}
	
	
	
	
	
}
