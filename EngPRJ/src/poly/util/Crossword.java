package poly.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crossword {
	
	private List<String> wordsList;
	private List<List<String>> matrix;
	
	public Crossword(String...strings) {
		this.wordsList = Arrays.asList(strings);
		this.matrix = new ArrayList<>();
		
		for (int i = 0; i < 5; i++) {
			List<String> row = new ArrayList<>();
			for (int j = 0; j < 5; j++) {
				row.add("-");
			}
			this.matrix.add(row);
		}
	}

	public List<String> getWordsList() {
		return wordsList;
	}


	public void setWordsList(List<String> wordsList) {
		this.wordsList = wordsList;
	}


	public List<List<String>> getMatrix() {
		return matrix;
	}


	public void setMatrix(List<List<String>> matrix) {
		this.matrix = matrix;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(List<String> row : matrix) {
			for(String s : row) {
				sb.append(s);
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void addCol() {
		for(List<String> row : matrix) {
			row.add("-");
		}
	}
	
	public void addRow() {
		List<String> row = new ArrayList<>();
		
		for(int i = 0; i < matrix.get(0).size(); i++)
			row.add("-");
		
		this.matrix.add(row);
	}
	

	public static void main(String[] args) {
		Crossword cw = new Crossword("hello", "name");
		System.out.println(cw);
		cw.addCol();
		System.out.println(cw);
		cw.addRow();
		System.out.println(cw);
	}
}
