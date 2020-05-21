package poly.dto;

import java.util.HashSet;
import java.util.Set;

public class WordDTO {

	private String word;
	private Set<String> pool;
	
	public WordDTO() {}
	
	public WordDTO(String word, String pool) {
		
		this.word = word;
		this.pool = new HashSet<String>();
		this.pool.add(pool);
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Set<String> getPool() {
		return pool;
	}
	public void setPool(Set<String> pool) {
		this.pool = pool;
	}
	
	public void addPool(String pool) {
		this.pool.add(pool);
	}
	
	
}
