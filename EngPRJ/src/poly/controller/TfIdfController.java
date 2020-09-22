package poly.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import poly.service.ITfIdfService;

@Controller
public class TfIdfController {

	
	@Resource(name = "TfIdfService")
	ITfIdfService tfIdfService;
	Logger log = Logger.getLogger(this.getClass());
	
	@ResponseBody
	@RequestMapping(value = "getTfIdf")
	public List<Map<String, Double>> getTfIdf(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getTfIdf start");

		List<Map<String, Double>> articles = tfIdfService.getAllArticles();
		
		
		log.info(this.getClass().getName() + ".getTfIdf end");
		return articles.subList(0, 10);
	}
	
}
