package poly.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	public String getTfIdf(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getTfIdf start");

		tfIdfService.getTfIdf();
		
		
		log.info(this.getClass().getName() + ".getTfIdf end");
		return "success";
	}
	@ResponseBody
	@RequestMapping(value = "getDf")
	public String getDf(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getTfIdf start");
		
		tfIdfService.getDf();
		
		
		log.info(this.getClass().getName() + ".getTfIdf end");
		return "success";
	}
	
	@ResponseBody
	@RequestMapping(value = "getIdf")
	public String getIdf(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getIdf start");
		
		tfIdfService.insertIdf();
		
		
		log.info(this.getClass().getName() + ".getIdf end");
		return "success";
	}
	
	@ResponseBody
	@RequestMapping(value = "tfIdfExample")
	public Map<String, Object> tfIdfExample(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getIdf start");
		List<Map<String, Object>> rList = new ArrayList<>();
		
		tfIdfService.getTop10().forEach(obj ->{
			Map<String, Object> map = new HashMap<>();
			map.put("newsUrl", (String)obj.get("newsUrl"));
			map.put("tfIdf", (Map<String, Double>)obj.get("tfIdf"));
			rList.add(map);
		});
		Map<String, Object> first = rList.get(1);
		Map<String, Double> tfIdf = (Map<String, Double>)first.get("tfIdf");
		Map<String, Double> sortedTfIdf = new LinkedHashMap<>();
		List<Entry<String, Double>> tfIdfList = new ArrayList<>(tfIdf.entrySet());
		tfIdfList.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
		tfIdfList.forEach(a -> sortedTfIdf.put(a.getKey(), a.getValue()));
		first.put("tfIdf", sortedTfIdf);
		
		return first;
	}
}
