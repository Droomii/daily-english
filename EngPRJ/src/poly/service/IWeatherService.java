package poly.service;

import java.util.List;

import poly.dto.WeatherDTO;

public interface IWeatherService {

	
	WeatherDTO getWeather(String url) throws Exception;
}
