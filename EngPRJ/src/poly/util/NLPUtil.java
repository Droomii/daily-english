package poly.util;

import java.util.Arrays;
import java.util.List;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.NewlineSentenceDetector;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

public class NLPUtil {
	public static void main(String[] args) {
		

		Tokenizer t = SimpleTokenizer.INSTANCE;
		String sample = "this is a sample sentence. What are you doing?";
		List<String> rList = Arrays.asList(t.tokenize(sample));
		System.out.println(rList);
		
		SentenceDetector sd = new NewlineSentenceDetector();
		System.out.println(Arrays.asList(sd.sentDetect(sample)));
		
	}
}