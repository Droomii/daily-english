package poly.service;

import java.util.List;

import poly.dto.WordDTO;

public interface INewsWordService {

	void insertWords(List<WordDTO> wordList) throws Exception;

}
