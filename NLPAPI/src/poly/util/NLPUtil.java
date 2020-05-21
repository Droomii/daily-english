package poly.util;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.dto.NLPDTO;

public class NLPUtil {
	
	public static String text = "Joe Smith was born in California. "
			+ "Take someone's soul out of here. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day.";
	
	public static void main(String[] args) {
	    // set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
	    props.setProperty("coref.algorithm", "neural");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	    // annnotate the document
	    pipeline.annotate(document);
	    // examples

	    // 10th token of the document
	    List<CoreLabel> token = document.tokens();
	    System.out.println("Example: token");
	    System.out.println("index: "  + token.get(6).index());
	    System.out.println("beginposition: "  + token.get(6).lemma());
	    System.out.println();

	    // text of the first sentence
	    String sentenceText = document.sentences().get(0).text();
	    System.out.println("Example: sentence");
	    System.out.println(sentenceText);
	    System.out.println();

	    // second sentence
	    CoreSentence sentence = document.sentences().get(1);

	    // list of the part-of-speech tags for the second sentence
	    List<String> posTags = sentence.posTags();
	    System.out.println("Example: pos tags");
	    System.out.println(posTags);
	    System.out.println();
	    
	    // list of lemmas
	    List<String> lemmaTags = sentence.lemmas();
	    System.out.println("Example: pos tags");
	    System.out.println(lemmaTags);
	    System.out.println();

	  }
	
	public static NLPDTO getNLP(String article) {
		
		// set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
	    props.setProperty("coref.algorithm", "neural");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    return null;
	}
	
}