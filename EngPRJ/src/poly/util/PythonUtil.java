package poly.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import it.sauronsoftware.jave.AudioAttributes;

public class PythonUtil {
	public static void main(String[] args) throws IOException {
		
		String requestURL = "http://192.168.88.129:5000/score";
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(requestURL);
		
		File example = new File("C:\\Users\\DATALAB_3\\word-stress-analysis\\sample.wav");
		File answer = new File("C:\\Users\\DATALAB_3\\word-stress-analysis\\sample.m4a");
		
		FileBody exampleFileBody = new FileBody(example);
		FileBody answerFileBody = new FileBody(answer);
		
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("example", exampleFileBody);
		reqEntity.addPart("answer", answerFileBody);
		httpPost.setEntity(reqEntity);
		
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity resEntity = response.getEntity();
		System.out.println(response.getStatusLine());
		List<String> responseContent = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
                resEntity.getContent()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            responseContent.add(line);
        }
        System.out.println(responseContent);
        reader.close();
		
		/*
		String charset = "UTF-8";
		
		// wavefiles
		w
		
		try {
			MultipartUtil mp = new MultipartUtil(requestURL, charset);
			mp.addHeaderField("User-Agent", "Droomii");
			mp.addHeaderField("Test-Header", "Header-Value");
			
			mp.addFormField("description", "wavefiles");
			mp.addFormField("keywords", "wavava");
			
			mp.addFilePart("example", example);
			mp.addFilePart("answer", answer);
			
			List<String> response = mp.finish();
			System.out.println("SERVER_REPLIED:");
			for(String line : response) {
				System.out.println(line);
			}
		}catch (IOException e) {
			System.err.println(e);
		}
		
		*/
		
	}
	
}
