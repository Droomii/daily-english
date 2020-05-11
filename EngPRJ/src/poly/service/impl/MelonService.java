package poly.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import poly.dto.MelonDTO;
import poly.persistance.mongo.IMelonMapper;
import poly.service.IMelonService;
import poly.util.DateUtil;

@Service("MelonService")
public class MelonService implements IMelonService{

	@Resource(name = "MelonMapper")
	IMelonMapper melonMapper;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public int collectMelonRank() throws Exception {
		
		log.info(this.getClass().getName() + ".collectMelonRank start");
		
		int res = 0;
		
		List<MelonDTO> pList = new ArrayList<MelonDTO>();
		
		String url = "https://www.melon.com/chart/index.htm";
		
		Document doc = null;
		
		doc = Jsoup.connect(url).get();
		
		Elements element = doc.select("div.service_list_song");
		
		Iterator<Element> rank50List = element.select("tr.lst50").iterator();
		
		while(rank50List.hasNext()) {
			
			Element songInfo = rank50List.next();
			
			log.info("songinfo : " + songInfo.text());
			String rank = songInfo.select("span.rank").text();  // 순위
			String song = songInfo.select("div.ellipsis a").eq(0).text();  // 노래
			String singer = songInfo.select("div.ellipsis a").eq(1).text();  // 노래
			String album = songInfo.select("div.ellipsis a").eq(3).text();  // 노래
			
			songInfo = null;
			
			MelonDTO pDTO = new MelonDTO();
			pDTO.setCollect_time(DateUtil.getDateTime("yyyyMMddhhss"));
			pDTO.setRank(rank);
			pDTO.setSinger(singer);
			pDTO.setSong(song);
			pDTO.setAlbum(album);
			
			pList.add(pDTO);
			
		}
		
		String colNm = "MelonTOP100_" + DateUtil.getDateTime("yyyyMMdd");
		
		melonMapper.createCollection(colNm);
		
		melonMapper.insertRank(pList, colNm);
		
		log.info(this.getClass().getName() + ".collectMelonRank end");
		
		return res;
	}

	@Override
	public List<MelonDTO> getRank() throws Exception {
		log.info(this.getClass().getName() + ".getRank start");
		
		String colNm = "MelonTOP100_" + DateUtil.getDateTime("yyyyMMdd");
		
		List<MelonDTO> rList = melonMapper.getRank(colNm);
		
		if(rList == null) {
			rList = new ArrayList<MelonDTO>();
		}
		
		log.info(this.getClass().getName() + ".getRank end");
		return rList;
	}

}
