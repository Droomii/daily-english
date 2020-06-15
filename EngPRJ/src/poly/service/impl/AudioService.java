package poly.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import poly.service.IAudioService;
import poly.util.TTSUtil;

@Service("AudioService")
public class AudioService implements IAudioService{

	@Override
	public byte[] getTodaySentenceAudio(String idx) throws Exception {
		
		String today = TTSUtil.sdf.format(new Date());
		String finalPath = TTSUtil.TTS_PATH + today + TTSUtil.SLASH + idx + ".ogg";
		
		File f = new File(finalPath);
		InputStream in = new FileInputStream(f);
		return IOUtils.toByteArray(in);
	}

}
