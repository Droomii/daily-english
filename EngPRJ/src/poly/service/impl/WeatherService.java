package poly.service.impl;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import poly.dto.WeatherDTO;
import poly.service.IWeatherService;

@Service("WeatherService")
public class WeatherService implements IWeatherService{
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public WeatherDTO getWeather(String url) throws Exception {
		
		log.info("getWeather start!");
		
		int res = 0;
		
		Document doc = null;
		
		doc = Jsoup.connect(url).get();
		
		Iterator<Element> depth_1 = doc.select("span.depth_1").iterator();
		Iterator<Element> depth_2 = doc.select("span.depth_2").iterator();
		
		WeatherDTO wDTO = new WeatherDTO();
		while(depth_1.hasNext()) {
			String txt = depth_1.next().text();
			log.info("text : " + txt);
			if(txt.indexOf("(종합)")> -1)
				wDTO.setAll(txt.substring(txt.indexOf("(종합)")+4));
			if(txt.indexOf("(오늘)")> -1)
				wDTO.setToday(txt.substring(txt.indexOf("(오늘)")+4));
			if(txt.indexOf("(내일)")> -1)
				wDTO.setTomorrow(txt.substring(txt.indexOf("(내일)")+4));
			if(txt.indexOf("(모레)")> -1)
				wDTO.setTwodays(txt.substring(txt.indexOf("(모레)")+4));
		}
		
		while(depth_2.hasNext()) {
			String txt = depth_2.next().text();
			log.info("text : " + txt);
			if(txt.indexOf("(종합)")> -1)
				wDTO.setAll(txt.substring(txt.indexOf("(종합)")+4));
			if(txt.indexOf("(오늘)")> -1)
				wDTO.setToday(txt.substring(txt.indexOf("(오늘)")+4));
			if(txt.indexOf("(내일)")> -1)
				wDTO.setTomorrow(txt.substring(txt.indexOf("(내일)")+4));
			if(txt.indexOf("(모레)")> -1)
				wDTO.setTwodays(txt.substring(txt.indexOf("(모레)")+4));
		}
		
		return wDTO;
	}
	
}
