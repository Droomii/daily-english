package poly.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class thread_jsoup extends Thread {
	public void run()
	{
		try 
		{
			hash_title ha = new hash_title();
			String url = "https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=105&oid=001&aid=0011556091";
			
			Document doc = Jsoup.connect(url).header("Accept", "text/html, application/xhtml+xml,*/*").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
					.header("Accept-Encoding", "gzip,delate").header("Accept-Language", "ko").header("Connection", "Keep-Alive").get();
			
			
			Elements head = doc.select("h3#articleTitle");
			String str_head = head.text();
			System.out.println(str_head);
			System.out.println(ha.hash_start(str_head));
			
			Elements body = doc.select("div#articleBodyContents");
			String str_body = body.text();
			System.out.println(str_body);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}