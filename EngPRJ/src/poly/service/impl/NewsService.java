package poly.service.impl;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.dto.NLPDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.service.INewsService;

@Service("NewsService")
public class NewsService implements INewsService{
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;
	
	@Override
	public NLPDTO nlpAndSaveNews(String newsTitle, String inputText) throws Exception {
		
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
	    
	    NLPDTO rDTO = new NLPDTO(newsTitle, document);
	    
	    mongoNewsMapper.insertNews(rDTO);
	    
	    return rDTO;
		
	}

	
}
