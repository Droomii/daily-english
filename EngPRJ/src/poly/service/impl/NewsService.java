package poly.service.impl;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import poly.dto.NewsDTO;
import poly.persistance.mongo.IMongoNewsMapper;
import poly.service.INewsService;
import poly.util.WebCrawler;

@Service("NewsService")
public class NewsService implements INewsService{
	
	@Resource(name = "MongoNewsMapper")
	IMongoNewsMapper mongoNewsMapper;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public NewsDTO nlpAndSaveNews(String newsTitle, String inputText, String newsUrl) throws Exception {
		
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
	    
	    NewsDTO rDTO = new NewsDTO(newsTitle, document, newsUrl);
	    
	    mongoNewsMapper.insertNews(rDTO);
	    
	    return rDTO;
		
	}

	@Override
	public NewsDTO getLatestNews() throws Exception {
		NewsDTO rDTO = mongoNewsMapper.getLatestNews();
		return rDTO;
	}
	
	
	@Override
	@Scheduled(cron="0 0 7 ? * *")
	public void scheduleCrawl() throws Exception{
		
		log.info(this.getClass().getName() + ".scheduleCrawl start");
		String[] crawlRes = WebCrawler.crawlHerald();
		String title = crawlRes[0];
		String inputText = crawlRes[1];
		String newsUrl = crawlRes[2];
		nlpAndSaveNews(title, inputText, newsUrl);
		log.info(this.getClass().getName() + ".scheduleCrawl end");
		
	}
	
}
