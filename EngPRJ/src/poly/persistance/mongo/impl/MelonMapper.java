package poly.persistance.mongo.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import config.Mapper;
import poly.dto.MelonDTO;
import poly.persistance.mongo.IMelonMapper;
import static poly.util.CmmUtil.nvl;

@Mapper("MelonMapper")
public class MelonMapper implements IMelonMapper {

	@Autowired
	private MongoTemplate mongodb;

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public boolean createCollection(String colNm) throws Exception {
		log.info(this.getClass().getName() + ".createCollection start");

		boolean res = false;

		if (mongodb.collectionExists(colNm)) {
			mongodb.dropCollection(colNm);
		}

		mongodb.createCollection(colNm).createIndex(new BasicDBObject("collect_time", 1).append("rank", 1), "rankIdx");

		res = true;

		log.info(this.getClass().getName() + ".createCollection end");

		return res;
	}

	@Override
	public int insertRank(List<MelonDTO> pList, String colNm) throws Exception {
		log.info(this.getClass().getName() + ".insertRank start");

		int res = 0;

		mongodb.insert(pList, colNm);

		log.info(this.getClass().getName() + ".insertRank end");

		return res;
	}

	@Override
	public List<MelonDTO> getRank(String colNm) throws Exception {
		log.info(this.getClass().getName() + ".getRank start");

		DBCollection rCol = mongodb.getCollection(colNm);

		Iterator<DBObject> cursor = rCol.find();

		List<MelonDTO> rList = new ArrayList<MelonDTO>();

		MelonDTO rDTO = null;

		while (cursor.hasNext()) {
			rDTO = new MelonDTO();

			final DBObject current = cursor.next();

			String collect_time = nvl((String) current.get("collect_time"));
			String rank = nvl((String) current.get("rank"));
			String song = nvl((String) current.get("song"));
			String singer = nvl((String) current.get("singer"));
			String album = nvl((String) current.get("album"));

			rDTO.setCollect_time(collect_time);
			rDTO.setRank(rank);
			rDTO.setSong(song);
			rDTO.setSinger(singer);
			rDTO.setAlbum(album);
			
			rList.add(rDTO);
			
			rDTO = null;

		}

		log.info(this.getClass().getName() + ".getRank end");

		return rList;
	}

}
