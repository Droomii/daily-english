package poly.dto;

import java.util.ArrayList;
import java.util.List;

public class TestInfoDTO {
	
	// 유저 번호
	private String userNo;
	
	// 답한 문제들 인덱스
	private List<Integer> answeredQs;
	
	// 첫 번째 숫자는 맞은 갯수, 두 번째 숫자는 푼 갯수임
	private int[] lvl1Correct;
	private int[] lvl2Correct;
	private int[] lvl3Correct;
	private int[] lvl4Correct;
	private int[] lvl5Correct;
	private int[] lvl6Correct;
	private int[] lvl7Correct;
	
	public TestInfoDTO() {
		answeredQs = new ArrayList<Integer>();
		lvl1Correct = new int[] {0,0};
		lvl2Correct = new int[] {0,0};
		lvl3Correct = new int[] {0,0};
		lvl4Correct = new int[] {0,0};
		lvl5Correct = new int[] {0,0};
		lvl6Correct = new int[] {0,0};
		lvl7Correct = new int[] {0,0};
	}
	
	public TestInfoDTO(String userNo) {
		this();
		this.userNo = userNo;
	}
	
	public List<Integer> getAnsweredQs() {
		return answeredQs;
	}
	public void setAnsweredQs(List<Integer> answeredQs) {
		this.answeredQs = answeredQs;
	}
	public int[] getLvl1Correct() {
		return lvl1Correct;
	}
	public void setLvl1Correct(int[] lvl1Correct) {
		this.lvl1Correct = lvl1Correct;
	}
	public int[] getLvl2Correct() {
		return lvl2Correct;
	}
	public void setLvl2Correct(int[] lvl2Correct) {
		this.lvl2Correct = lvl2Correct;
	}
	public int[] getLvl3Correct() {
		return lvl3Correct;
	}
	public void setLvl3Correct(int[] lvl3Correct) {
		this.lvl3Correct = lvl3Correct;
	}
	public int[] getLvl4Correct() {
		return lvl4Correct;
	}
	public void setLvl4Correct(int[] lvl4Correct) {
		this.lvl4Correct = lvl4Correct;
	}
	public int[] getLvl5Correct() {
		return lvl5Correct;
	}
	public void setLvl5Correct(int[] lvl5Correct) {
		this.lvl5Correct = lvl5Correct;
	}
	public int[] getLvl6Correct() {
		return lvl6Correct;
	}
	public void setLvl6Correct(int[] lvl6Correct) {
		this.lvl6Correct = lvl6Correct;
	}
	public int[] getLvl7Correct() {
		return lvl7Correct;
	}
	public void setLvl7Correct(int[] lvl7Correct) {
		this.lvl7Correct = lvl7Correct;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	
	
}
