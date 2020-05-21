package poly.service.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.dto.NLPDTO;
import poly.service.INLPService;

@Service("NLPService")
public class NLPService implements INLPService{

	@Override
	public NLPDTO process(String inputText) throws Exception {
		
		// set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
	    props.setProperty("coref.algorithm", "neural");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(inputText);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    NLPDTO rDTO = new NLPDTO(document);
	    
	    return rDTO;
		
	}

	
}
