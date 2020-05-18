package poly.persistance.redis;

import poly.dto.TestInfoDTO;
import poly.dto.TestWordDTO;

public interface IRedisTestWordMapper {
	
	/**
	 * 레디스에 실력측정 단어 업로드함
	 * @throws Exception
	 */
	public void saveTestWords() throws Exception;

	public TestWordDTO getRandomWord() throws Exception;

	public TestWordDTO getRandomWord(String index, String answer) throws Exception;

	public TestInfoDTO getTestInfo(String userNo) throws Exception;
	
	public void updateTestInfo(TestInfoDTO pDTO) throws Exception;
	

}
