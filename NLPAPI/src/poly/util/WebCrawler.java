package poly.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebCrawler {
	
	public static String crawlHerald() throws IOException{
		
		// 코리아헤럴드 주소
		String url = "http://www.koreaherald.com";
		
		// 페이지 내용 담을 객체
		Document doc = null;
		
		// 홈페이지 정보 가져오기
		doc = Jsoup.connect(url).get();
		
		// 홈페이지의 가운데 기사 뽑아오기
		Element element = doc.select("a.main_c_art_main").first();
		
		// 가운데 기사의 링크 가져오기
		String href = element.attr("href");
		
		// 기사 링크로 들어가기
		doc = Jsoup.connect(url + href).get();
		
		// 기사 내용 추출
		element = doc.select("#articleText div.view_con_t").last();
		
		String article = element.text();
		
		return article;

	}
}
