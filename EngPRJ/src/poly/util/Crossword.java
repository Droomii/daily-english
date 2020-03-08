package poly.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crossword {
	
	private List<String> wordsList;
	private List<List<Character>> matrix;
	int colSize = 0;
	int rowSize = 0;
	
	public Crossword(String...strings) {
		this.wordsList = Arrays.asList(strings);
		this.matrix = new ArrayList<>();
		
		for (int i = 0; i < 5; i++) {
			List<Character> row = new ArrayList<>();
			for (int j = 0; j < 5; j++) {
				row.add('-');
			}
			this.matrix.add(row);
		}
	}

	
	// 행 추가
	public void addCol() {
		for(List<Character> row : matrix) {
			row.add('-');
		}
	}
	
	public void addCol(int i) {
		for (int j = 0; j < i; j++) {
			addCol();
		}
	}
	
	
	// 열 추가
	public void addRow() {
		List<Character> row = new ArrayList<>();
		
		for(int i = 0; i < matrix.get(0).size(); i++)
			row.add('-');
		this.matrix.add(row);
	}
	
	public void addRow(int i) {
		for (int j = 0; j < i; j++) {
			addRow();
		}
	}
	
	public void addWordHorizontally(int row, int col, String word) {
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(List<Character> row : matrix) {
			for(char s : row) {
				sb.append(s);
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	
	
	/**
	 * 행, 열에 위치한 문자를 출력해줌
	 * @param row : 행
	 * @param col : 열
	 * @return
	 */
	public char get(int row, int col) {
		return this.matrix.get(row).get(col);
	}
	
	public void set(int row, int col, char pChar) {
		pChar = Character.toUpperCase(pChar);
		this.matrix.get(row).set(col, pChar);
	}

	public static void main(String[] args) {
		Crossword cw = new Crossword("hello", "name");
		System.out.println(cw);
		cw.addCol();
		System.out.println(cw);
		cw.addRow();
		System.out.println(cw);
		cw.set(1, 2, 'a');
		System.out.println(cw);
	}
}