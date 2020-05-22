package poly.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import poly.dto.NewsDTO;

public class TranslateUtil extends Thread{

	public static List<String> translateNews(NewsDTO pDTO) throws Exception{
		
		String apiKey = "1c33024aebc041b21eca04855e493eb0";
		
		
		List<String> originalSentences = pDTO.getOriginalSentence();
		List<String> translatedSentences = new ArrayList<String>();
		
		String postParams = "src_lang=en&target_lang=kr&query=";
		String apiURL = "https://kapi.kakao.com/v1/translation/translate?" + postParams;
		for(String originalSentence : originalSentences) {
			String sent = URLEncoder.encode(originalSentence, "UTF-8");
			String queryURL = apiURL + sent;
			URL url = new URL(queryURL);
			
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			String userCredentials = apiKey;
			String basicAuth = "KakaoAK " + userCredentials;
			con.setRequestProperty("Authorization", basicAuth);
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("charset", "utf-8");
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			int responseCode = con.getResponseCode();
			System.out.println("response code : " + responseCode);
			
			BufferedReader br;
			if(responseCode==200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			}else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer res = new StringBuffer();
			while((inputLine=br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			JSONObject json = new JSONObject(res.toString());
			
			String translated = ((JSONArray)json.getJSONArray("translated_text").get(0)).getString(0);
			System.out.println(translated);
			translatedSentences.add(translated);
		}
		
		return translatedSentences;
		
	}
}
