package poly.service;

import poly.dto.TestWordDTO;

public interface ITestWordService {

	void saveTestWords() throws Exception;

	TestWordDTO getRandomWord() throws Exception;

}
