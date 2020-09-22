package poly.persistance.mongo;

import java.util.SortedMap;

public interface IMongoTfIdfMapper {

	void insertIdf(SortedMap<String, Double> idf) throws Exception;
}
