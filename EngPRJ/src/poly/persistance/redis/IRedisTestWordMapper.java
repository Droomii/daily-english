package poly.persistance.redis;

import poly.dto.TestWordDTO;

public interface IRedisTestWordMapper {
	
	/**
	 * 레디스에 실력측정 단어 업로드함
	 * @throws Exception
	 */
	public void saveTestWords() throws Exception;

	public TestWordDTO getRandomWord() throws Exception;

	public TestWordDTO getRandomWord(String index, String answer) throws Exception;

}
