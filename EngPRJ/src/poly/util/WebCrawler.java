package poly.util;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	static final Pattern ARTICLE_P = Pattern.compile("\\/view\\.php\\?ud=([0-9]+)");
	static final Pattern CAT_P = Pattern.compile("\\/list\\.php\\?ct=([0-9]+)");

	public static Document connectToArticle(String article) throws IOException {
		String url = "http://www.koreaherald.com/view.php?ud=" + article;
		Document doc = null;
		doc = Jsoup.connect(url).get();
		return doc;
	}

	public static String[] crawlHerald() throws Exception {

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

		href = href.split("ud=")[1].split("&")[0];
		return crawlHerald(href);

	}
	
	public static String getImageUrl(String newsUrl) throws Exception{
		Document article = connectToArticle(newsUrl);
		Element articleText = article.selectFirst("div#articleText");
		Element img = articleText.selectFirst("img");
		String url = img.attr("src");
		return url;
	}

	public static String[] crawlHerald(String article) throws Exception {
		return crawlHerald(connectToArticle(article), article);
	}

	public static String[] crawlHerald(Document doc, String article) throws Exception {
		// 기사 내용 추출
		StringBuilder articleSb = new StringBuilder();

		Iterator<Element> it = doc.select("#articleText div.view_con_t").iterator();

		while (it.hasNext()) {
			articleSb.append(it.next().ownText());
		}

		Element element = doc.selectFirst("h1.view_tit");

		String title = element.text();

		return new String[] { title, articleSb.toString(), article };
	}

	public static Set<String> bfs() throws Exception {
		Set<String> categories = categoryBFS();
		System.out.println("category size : " + categories.size());
		Set<String> articleSet = new HashSet<>();
		for (String category : categories) {
			try {
				articleBFS(articleSet, category);
			} catch (IOException e) {
				continue;
			}

		}
		System.out.println("article count : " + articleSet.size());
		return null;
	}

	public static Set<String> articleBFS(Set<String> articleSet, String category) throws Exception {
		String url = "http://www.koreaherald.com/list.php?ct=" + category;
		System.out.println("searching : " + url);
		// 페이지 내용 담을 객체
		Document doc = null;
		Set<String> newSet = new HashSet<>();
		// 홈페이지 정보 가져오기
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			return newSet;
		}

		String html = doc.toString();
		Matcher m = ARTICLE_P.matcher(html);
		while (m.find()) {
			if (articleSet.add(m.group(1)))
				newSet.add(m.group(1));
		}
		return newSet;
	}
	
	public static Set<String> getArticleInPage(Set<String> articleSet, int np) throws Exception{
		String url = "http://www.koreaherald.com/list.php?ct=020000000000&np=" + np;
		Document doc = null;
		Set<String> newSet = new HashSet<>();
		try {
			doc = Jsoup.connect(url).get();
		}catch(IOException e) {
			return newSet;
		}
		String main = doc.select("div.main_sec").first().toString();
		Matcher m = ARTICLE_P.matcher(main);
		while (m.find()) {
			if (articleSet.add(m.group(1)))
				newSet.add(m.group(1));
		}
		return newSet;
	}

	public static Set<String> categoryBFS() throws Exception {
		String url = "http://www.koreaherald.com/index.php";
		// 페이지 내용 담을 객체
		Document doc = null;
		// 홈페이지 정보 가져오기
		doc = Jsoup.connect(url).get();
		String html = doc.toString();
		Matcher m = CAT_P.matcher(html);
		Set<String> catSet = new HashSet<>();
		Queue<String> catQueue = new ArrayDeque<>();
		while (m.find()) {
			catSet.add(m.group(1));
		}
		catQueue.addAll(catSet);
		while (!catQueue.isEmpty()) {
			String cat = catQueue.poll();
			System.out.println("category : " + cat);
			Set<String> links = null;
			try {
				links = getCats(cat);
			} catch (IOException e) {
				continue;
			}
			links.removeAll(catSet);
			catSet.addAll(links);
			catQueue.addAll(links);
		}
		return catSet;
	}

	public static Set<String> getCats(String article) throws Exception {
		String url = "http://www.koreaherald.com/list.php?ct=" + article;
		Document doc = null;
		doc = Jsoup.connect(url).get();
		String html = doc.toString();
		Matcher m = CAT_P.matcher(html);
		Set<String> catSet = new HashSet<>();
		while (m.find()) {
			catSet.add(m.group(1));
		}
		return catSet;
	}

	public static void main(String[] args) throws Exception {
		getArticleInPage(null, 1);
	}

	public static Set<String> getArticles(Set<String> articleSet, String article) throws Exception {
		Document doc = connectToArticle(article);
		return getArticles(articleSet, doc);
	}
	
	public static Set<String> getArticles(Set<String> articleSet, Document doc) throws Exception{
		String html = doc.toString();
		Matcher m = ARTICLE_P.matcher(html);
		Set<String> newArticle = new HashSet<>();
		while (m.find()) {
			if(articleSet.add(m.group(1))) {
				newArticle.add(m.group(1));
			}
		}
		return newArticle;
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
		while (it.hasNext()) {
			e.select("span.HQToggle").remove();
			e.select("ul").remove();
			e.select("dl").remove();
			if (e.text().trim().equals("")) {
				continue;
			} else {
				meaning = e.text().trim();
				break;
			}
		}

		return meaning;

	}
}
