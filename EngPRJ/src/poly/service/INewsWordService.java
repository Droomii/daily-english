package poly.service;

import java.util.List;
import java.util.Map;

import poly.dto.NewsDTO;
import poly.dto.WordDTO;

public interface INewsWordService {

	void insertWords(List<WordDTO> wordList) throws Exception;

	List<Map<String, Object>> extractWords(NewsDTO pDTO) throws Exception;
	
	void loadWordPool() throws Exception;

	void highlightWords(NewsDTO news) throws Exception;

}
