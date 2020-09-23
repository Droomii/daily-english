package poly.service;

import java.util.List;
import java.util.Map;

public interface ITfIdfService {

	List<Map<String, Double>> getAllArticles() throws Exception;

	void getDf() throws Exception;

}
