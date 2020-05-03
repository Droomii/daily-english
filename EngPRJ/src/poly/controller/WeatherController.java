package poly.controller;

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

import poly.dto.WeatherDTO;
import poly.service.IWeatherService;
import poly.util.CmmUtil;

@Controller
public class WeatherController {

	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "WeatherService")
	IWeatherService weatherService;
	
	@RequestMapping(value = "weather/index")
	public String weather(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".weather start");
		
		
		
		log.info(this.getClass().getName() + ".weather end");
		return "/weather/index";
	}
	
	@RequestMapping(value = "weather/getWeather")
	@ResponseBody
	public WeatherDTO getWeather(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model)
			throws Exception {
		log.info(this.getClass().getName() + ".getWeather start");

		WeatherDTO wDTO = null;
		
		String send_msg = CmmUtil.nvl(request.getParameter("send_msg"));
		
		String regionUrl = "";
		if(send_msg.indexOf("날씨") > -1) {
			if(send_msg.indexOf("서울") > -1 || send_msg.indexOf("인천") > -1 || send_msg.indexOf("경기도") > -1) {
				regionUrl = "http://www.weather.go.kr/weather/forecast/summary.jsp?stnId=109&x=18&y=9";
			}else {
				regionUrl = "http://www.weather.go.kr/weather/forecast/summary.jsp?stnId=108&x=39&y=19";
			}
			
			wDTO = weatherService.getWeather(regionUrl);
			
		}
		
		log.info(this.getClass().getName() + ".getWeather end");
		return wDTO;
	}
	
}
