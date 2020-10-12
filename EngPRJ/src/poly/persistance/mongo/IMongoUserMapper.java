package poly.persistance.mongo;

import java.util.Set;

public interface IMongoUserMapper {

	void insertAttend(String user_seq) throws Exception;

	Set<Integer> getAttend(String user_seq) throws Exception;

}
