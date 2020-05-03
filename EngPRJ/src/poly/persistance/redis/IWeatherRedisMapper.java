package poly.persistance.redis;

import java.util.List;

import poly.dto.WeatherDTO;

public interface IWeatherRedisMapper {

	public boolean getExists(String key) throws Exception;
	
	public List<WeatherDTO> getWeather(String key) throws Exception;
	
	public int setWeather(String key, List<WeatherDTO> pList) throws Exception;
	
	public boolean setTimeOutHour(String key, int hours) throws Exception;
	
	
}
