package poly.service;

import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;

public interface ITfIdfService {
	
	void getDf() throws Exception;

	void insertIdf() throws Exception;

	void getTfIdf() throws Exception;

	List<DBObject> getTop10() throws Exception;

}
