package poly.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestInfoDTO {
	
	// 유저 번호
	private String userNo;
	
	// 답한 문제들 인덱스
	private Set<Integer> answeredQs;
	
	// 첫 번째 숫자는 맞은 갯수, 두 번째 숫자는 푼 갯수임
	private int[][] correctAnswers;
	
	public TestInfoDTO() {
		answeredQs = new HashSet<Integer>();
		
		correctAnswers = new int[7][2];
	}
	
	public TestInfoDTO(String userNo) {
		this();
		this.userNo = userNo;
	}
	
	public Set<Integer> getAnsweredQs() {
		return answeredQs;
	}
	public void setAnsweredQs(Set<Integer> answeredQs) {
		this.answeredQs = answeredQs;
	}
	
	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public int[][] getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(int[][] correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	
	
	
	
}
