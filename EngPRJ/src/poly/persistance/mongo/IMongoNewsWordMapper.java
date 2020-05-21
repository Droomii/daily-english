package poly.persistance.mongo;

import java.util.List;

public interface IMongoNewsWordMapper {

	void insertWords(List<String> words) throws Exception;
	
	void createCollection() throws Exception;

}
