package poly.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.service.INewsWordService;

@Controller
public class NewsWordController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "NewsWordService")
	INewsWordService newsWordService;
	
	@RequestMapping(value = "newsWord/insertWords")
	@ResponseBody
	public String insertWords(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".insertWords start");

		File f = new File("c:/word.txt");
		
		BufferedReader bf = new BufferedReader(new FileReader(f));
		
		List<String> words = new ArrayList<String>();
		
		String line;
		while((line = bf.readLine()) != null) {
			
			words.add(line);
			
		}
		
		newsWordService.insertWords(words);
		
		log.info(this.getClass().getName() + ".insertWords end");
		return "success";
	}
	
}
