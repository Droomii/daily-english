package poly.service;

public interface IMongoTestService {
	
	
	/**
	 * MongoDB 컬렉션 생성
	 * @param colNm 생성하는 컬렉션 이름
	 */
	public boolean createCollection() throws Exception;

	public void insertWords() throws Exception;
}
