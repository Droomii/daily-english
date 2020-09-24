package poly.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class UrlUtil {

	public static String request(String requestURL, boolean post) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(requestURL);
		HttpGet httpPost = new HttpGet(requestURL);
		HttpResponse response;
		if(post) {
			response = httpClient.execute(httpPost);	
		}else {
			response = httpClient.execute(httpGet);
		}
		
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());
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
		return sb.toString();
	}
}
