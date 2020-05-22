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

import org.json.JSONObject;

import poly.dto.NewsDTO;

public class TranslateUtil extends Thread{

	
	public static void main(String[] args) throws Exception {
		
		List<String> sentences = new ArrayList<String>();
		sentences.add("Hello, my name is John.");
		
		List<String> rList = translateNews(sentences);
		System.out.println(rList);
		
	}
	public static List<String> translateNews(NewsDTO pDTO) throws Exception{
		
		String clientId = "BFiXV63sQ6j0zvANvxkT";
		String clientSecret = "kFXNB8i0F8";
		
		
		List<String> originalSentences = pDTO.getOriginalSentence();
		List<String> translatedSentences = new ArrayList<String>();
		
		String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
		for(String originalSentence : originalSentences) {
			String sent = URLEncoder.encode(originalSentence, "UTF-8");

			Map<String, String> requestHeaders = new HashMap<>();
	        requestHeaders.put("X-Naver-Client-Id", clientId);
	        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

	        String responseBody = post(apiURL, requestHeaders, sent);
			
	        // getting translated text
	        String translated = new JSONObject(responseBody)
	        		.getJSONObject("message")
	        		.getJSONObject("result")
	        		.getString("translatedText");
	        
			System.out.println(translated);
			translatedSentences.add(translated);
		}
		
		return translatedSentences;
		
	}
	
	private static String post(String apiUrl, Map<String, String> requestHeaders, String text){
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=en&target=ko&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
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
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
	
	private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
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
    
    public static List<String> translateNews(List<String> sentences) throws Exception{
    	NewsDTO pDTO = new NewsDTO();
    	pDTO.setOriginalSentence(sentences);
    	return translateNews(pDTO);
    }
	
}
