package poly.util;


import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class NLPUtil {
	public static String text = "He is a learned person";
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
			System.out.println("----------------------");
			System.out.println(sent.posTags());
		}

	}
}
