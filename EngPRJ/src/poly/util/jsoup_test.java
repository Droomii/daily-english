package poly.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class jsoup_test {

	public static void main(String[] args) throws Exception {
		// testing git sync
		thread_jsoup jsoup_thread = new thread_jsoup();
		jsoup_thread.start();
		
		System.out.println("Thread ���ο��� ������ ó�� ��");
		
		String url2 = "https://www.koreabaseball.com/TeamRank/TeamRank.aspx";
		Document doc2 = Jsoup.connect(url2).get();
		
		Elements rowElements = doc2.select("div#cphContents_cphContents_cphContents_udpRecord > table.tData > tbody > tr");
		
		for(Element row : rowElements)
		{
			Elements tdElements = row.getElementsByTag("td");
			System.out.format("%d %s", Integer.valueOf(tdElements.get(0).text()), tdElements.get(1).text());
			System.out.println();
		}
		
	}

}
