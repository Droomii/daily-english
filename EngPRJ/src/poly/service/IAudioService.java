package poly.service;

import java.util.Map;

public interface IAudioService {

	byte[] getTodaySentenceAudio(String idx) throws Exception;

	Map<String, Object> analyzeAudio(String data, String sentenceAudioIdx) throws Exception;
	
}