package poly.util;


import java.util.*;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class NLPUtil {
	public static String text = "So far, 22 people, mostly ones with underlying illnesses, have died in South Korea from the respiratory virus that emerged in China.\r\n" + 
			"\r\n" + 
			"About 60 percent of confirmed cases have been linked to a branch of Shincheonji religious sect in the southeastern city of Daegu.\r\n" + 
			"\r\n" + 
			"Of the 476 new cases, 377 are in Daegu, 300 kilometers southeast of Seoul, and 68 are in neighboring North Gyeongsang Province, according to the Korea Centers for Disease Control and Prevention (KCDC). The total number of cases in Daegu and North Gyeongsang stood at 3,081 and 624, respectively.";
	
	public static void main(String[] args) {
//		Properties props = new Properties();
//
//		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//		List<String> lemmas = new LinkedList<String>();
//		
//		Annotation document = new Annotation(text);
//
//		pipeline.annotate(document);
//
//		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//
//		for(CoreMap sentence: sentences) {
//			for(CoreLabel token : sentence.get(TokensAnnotation.class)) {
//				lemmas.add(token.get(LemmaAnnotation.class));
//			}
//		}
//		
//		System.out.println(lemmas);
		
		Document doc = new Document(text);
		for(Sentence sent : doc.sentences()) {
			System.out.println(sent.lemmas());
		}

	}
}
