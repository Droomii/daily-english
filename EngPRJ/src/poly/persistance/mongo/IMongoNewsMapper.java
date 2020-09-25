package poly.persistance.mongo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import poly.dto.NewsDTO;

public interface IMongoNewsMapper {

	boolean insertNews(NewsDTO rDTO, boolean translate) throws Exception;
	
	void createCollection() throws Exception;

	NewsDTO getLatestNews() throws Exception;

	NewsDTO getNews(int i) throws Exception;

	DBCursor getAllArticles() throws Exception;

	void addNp(int np) throws Exception;

	int getNp() throws Exception;

	void incrementDf(Set<String> keySet) throws Exception;

	void insertTf(String newsUrl, Map<String, Double> tf) throws Exception;

	void insertNewsTfAll(List<DBObject> tfList) throws Exception;

	void insertDf(Map<String, Long> df) throws Exception;

	void insertIdf() throws Exception;

	void insertTfIdf() throws Exception;

	DBCursor getTfIdf() throws Exception;

	void insertNews(List<NewsDTO> newArticles, boolean translate) throws Exception;

	void updateDf(Map<String, Long> df) throws Exception;

	Map<String, Double> getIdf(Set<String> keySet) throws Exception;

	void insertNewsTfIdfAll(List<DBObject> tfList) throws Exception;;


}
