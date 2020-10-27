package poly.util;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
 
public class PronEvaluator {
 
    static public void main ( String[] args ) throws Exception{
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
//    	String openApiURL = "http://localhost:5000/apiTest";
        String accessKey = "";    // 발급받은 API Key
        String languageCode = "english";     // 언어 코드
        String script = "PRONUNCIATION_SCRIPT";    // 평가 대본
        String audioFilePath = "C:\\Users\\data-08\\Documents\\audio\\0.wav";  // 녹음된 음성 파일 경로
        
        String audioContents = null;
 
        try {
            Path path = Paths.get(audioFilePath);
            byte[] audioBytes = Files.readAllBytes(path);
            
            audioContents = Base64.getEncoder().encodeToString(audioBytes);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(openApiURL);
		
        JSONObject request = new JSONObject();
        JSONObject arg = new JSONObject();
        
        arg.put("language_code", "english");
        arg.put("audio", audioContents);
        request.put("argument", arg);
        request.put("access_key", accessKey);
        String requestStr = request.toString();
        FileWriter orig = new FileWriter("C:\\Users\\data-08\\Documents\\audio\\orig.txt");
        orig.write(requestStr);
        orig.close();
        
        HttpEntity httpEntity = new StringEntity(request.toString(), "utf-8");
        
        httpPost.setEntity(httpEntity);
        
        HttpResponse response = httpClient.execute(httpPost);
		HttpEntity resEntity = response.getEntity();
        
		StringBuffer sb = new StringBuffer();
		if(resEntity != null) {
			try(InputStream instream = resEntity.getContent()){
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	sb.append(line);
		        }
			}
		}
		
		System.out.println(sb);
//		FileWriter out = new FileWriter("C:\\Users\\data-08\\Documents\\audio\\out.txt");
//		out.write(sb.toString());
//		out.close();
    }
}
                     