package poly.util;

public class NLPUtil {
	/*
	public static String text = "Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day.";

	public static void main(String[] args) {
		// set up pipeline properties
		Properties props = new Properties();
		// set the list of annotators to run
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref,kbp");
		// set a property for an annotator, in this case the coref annotator is being
		// set to use the neural algorithm
		props.setProperty("coref.algorithm", "neural");
		// build pipeline
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create a document object
		CoreDocument document = new CoreDocument(text);
		// annnotate the document
		pipeline.annotate(document);
		// examples

		// 10th token of the document
		CoreLabel token = document.tokens().get(10);
		System.out.println("Example: token");
		System.out.println(token);
		System.out.println();

		// text of the first sentence
		String sentenceText = document.sentences().get(0).text();
		System.out.println("Example: sentence");
		System.out.println(sentenceText);
		System.out.println();

		// kbp relations found in fifth sentence
		List<RelationTriple> relations = document.sentences().get(0).relations();
		System.out.println("Example: relation");
		System.out.println(relations);
		System.out.println();
	}
	*/
}