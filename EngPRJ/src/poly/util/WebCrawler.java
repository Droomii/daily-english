package poly.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawler {
	private Document doc;
	private String text;
	
	public WebCrawler() throws IOException {
		this.doc = Jsoup.connect("http://www.koreaherald.com/view.php?ud=20200309000043").get();
		Elements element = doc.select("div.view_con_t");
		String text = element.text();
		this.text = text;
		
		
	}
	
	
	public Document getDoc() {
		return doc;
	}


	public void setDoc(Document doc) {
		this.doc = doc;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public static void main(String[] args) throws IOException {
		WebCrawler a = new WebCrawler();
	}
}
