package poly.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import poly.dto.NewsDTO;

public class TranslateUtil extends Thread {

	public static void main(String[] args) throws Exception {

		List<String> sentences = new ArrayList<String>();
		sentences.add("Hello, my name is John.");

		String res = translateKakao("Self-employed high earners' tax evasion totals W10tr in decade");
		System.out.println(res);

	}

	public static List<String> translateNews(NewsDTO pDTO) throws Exception {

		String clientId = "BFiXV63sQ6j0zvANvxkT";
		String clientSecret = "kFXNB8i0F8";

		List<String> originalSentences = pDTO.getOriginalSentences();
		List<String> translatedSentences = new ArrayList<String>();

		String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
		for (String originalSentence : originalSentences) {
			String sent = URLEncoder.encode(originalSentence, "UTF-8");

			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("X-Naver-Client-Id", clientId);
			requestHeaders.put("X-Naver-Client-Secret", clientSecret);

			String responseBody = post(apiURL, requestHeaders, sent);

			// getting translated text
			String translated = new JSONObject(responseBody).getJSONObject("message").getJSONObject("result")
					.getString("translatedText");

			System.out.println(translated);
			translatedSentences.add(translated);
		}

		return translatedSentences;

	}

	public static String translateKakao(String sentence) throws Exception {

		String apiKey = "1c33024aebc041b21eca04855e493eb0";

		String postParams = "src_lang=en&target_lang=kr&query=";
		String apiURL = "https://kapi.kakao.com/v1/translation/translate?" + postParams;
		String sent = URLEncoder.encode(sentence, "UTF-8");
		String queryURL = apiURL + sent;
		URL url = new URL(queryURL);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
		if (responseCode == 200) {
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}

		String inputLine;
		StringBuffer res = new StringBuffer();
		while ((inputLine = br.readLine()) != null) {
			res.append(inputLine);
		}
		br.close();
		JSONObject json = new JSONObject(res.toString());

		String translated = ((JSONArray) json.getJSONArray("translated_text").get(0)).getString(0);

		return translated;

	}

	private static String post(String apiUrl, Map<String, String> requestHeaders, String text) {
		HttpURLConnection con = connect(apiUrl);
		String postParams = "source=en&target=ko&text=" + text; // 원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
		try {
			con.setRequestMethod("POST");
			for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
				con.setRequestProperty(header.getKey(), header.getValue());
			}

			con.setDoOutput(true);
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.write(postParams.getBytes());
				wr.flush();
			}

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
				return readBody(con.getInputStream());
			} else { // 에러 응답
				return readBody(con.getErrorStream());
			}
		} catch (IOException e) {
			throw new RuntimeException("API 요청과 응답 실패", e);
		} finally {
			con.disconnect();
		}
	}

	private static HttpURLConnection connect(String apiUrl) {
		try {
			URL url = new URL(apiUrl);
			return (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
		} catch (IOException e) {
			throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
		}
	}

	private static String readBody(InputStream body) {
		InputStreamReader streamReader = new InputStreamReader(body);

		try (BufferedReader lineReader = new BufferedReader(streamReader)) {
			StringBuilder responseBody = new StringBuilder();

			String line;
			while ((line = lineReader.readLine()) != null) {
				responseBody.append(line);
			}

			return responseBody.toString();
		} catch (IOException e) {
			throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
		}
	}

	public static List<String> translateNews(List<String> sentences) throws Exception {
		NewsDTO pDTO = new NewsDTO();
		pDTO.setOriginalSentences(sentences);
		return translateNews(pDTO);
	}

	public static String translateTitle(String newsTitle) throws Exception {
		List<String> pList = new ArrayList<String>();
		pList.add(newsTitle);
		return translateNews(pList).get(0);
	}

}
