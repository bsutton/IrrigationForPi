package au.org.noojee.irrigation.weather.bureaus;

import java.time.LocalDate;

import au.org.noojee.irrigation.weather.WeatherForecast;

public interface WeatherStation {

	WeatherForecast fetchForecast(LocalDate date);
	
	

}
