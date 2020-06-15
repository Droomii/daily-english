package poly.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.UnsupportedAudioFileException;

//Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

/**
 * Google Cloud TextToSpeech API sample application. Example usage: mvn package
 * exec:java -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class TTSUtil {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
	public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
	public static final String TTS_PATH = IS_WINDOWS ? "C:\\Users\\DATALAB_3\\tts\\" : "/daily-english/tts/";
	public static final String SLASH = IS_WINDOWS ? "\\" : "/";
	public static final String FFMPEG_PATH = IS_WINDOWS ? "C:\\ffmpeg\\bin\\ffmpeg.exe" : "ffmpeg";

	public static void saveTTS(int index, String sentence) throws IOException, UnsupportedAudioFileException {
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
			// Set the text input to be synthesized
			SynthesisInput input = SynthesisInput.newBuilder().setText(sentence).build();

			// Build the voice request, select the language code ("en-US") and the ssml
			// voice gender
			// ("neutral")
			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("en-US")
					.setName("en-US-Wavenet-D").build();

			// Select the type of audio file you want returned
			AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

			// Perform the text-to-speech request on the text input with the selected voice
			// parameters and
			// audio file type
			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

			// Get the audio contents from the response
			ByteString audioContents = response.getAudioContent();

			Date d = new Date();
			String dateFormat = sdf.format(d);

			File dir = new File(TTS_PATH + dateFormat + SLASH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String finalDir = dir + SLASH + Integer.toString(index);

			// Write the response to the output file.
			try (OutputStream out = new FileOutputStream(finalDir)) {
				out.write(audioContents.toByteArray());
				System.out.println("Audio content written to " + finalDir + ".wav");
			}

			// wav to ogg
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(FFMPEG_PATH, "-i", finalDir + ".wav", "-acodec", "libvorbis", finalDir + ".ogg");
			pb.redirectErrorStream(true);
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			String fullLine = "";
			while ((line = reader.readLine()) != null) {
				fullLine += line;
			}
			System.out.println(fullLine);
		}
	}

}