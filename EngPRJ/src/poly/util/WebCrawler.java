package poly.util;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	public static String[] crawlHerald() throws IOException {

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
		StringBuilder articleSb = new StringBuilder();

		Iterator<Element> it = doc.select("#articleText div.view_con_t").iterator();

		while (it.hasNext()) {
			articleSb.append(it.next().ownText());
		}

		element = doc.selectFirst("h1.view_tit");

		String title = element.text();

		return new String[] { title, articleSb.toString(), url + href };

	}

	public static String getMeaning(String word) throws IOException {

		// 코리아헤럴드 주소
		String url = "https://en.wiktionary.org/wiki/" + word;

		// 페이지 내용 담을 객체
		Document doc = null;

		// 홈페이지 정보 가져오기
		doc = Jsoup.connect(url).get();

		// 첫번째 단어 가져오기
		Elements e = doc.select("ol > li");
		Iterator<Element> it = e.iterator();
		String meaning = null;
		while(it.hasNext()) {
			e.select("span.HQToggle").remove();
			e.select("ul").remove();
			e.select("dl").remove();
			if(e.text().trim().equals("")) {
				continue;
			}else {
				meaning = e.text().trim();
				break;
			}
		}
		
		return meaning;

	}
}
