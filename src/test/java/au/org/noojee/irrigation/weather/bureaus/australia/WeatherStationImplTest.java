package au.org.noojee.irrigation.weather.bureaus.australia;

import java.net.MalformedURLException;
import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import au.org.noojee.irrigation.weather.WeatherForecast;

public class WeatherStationImplTest {

	@Test
	public void test() throws MalformedURLException {
	
		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		
		
		BOMWeatherStation station = BOMWeatherStation.ViewBank;
		
		BOMObservations observations = station.fetchObservations(LocalDate.now());
		System.out.println(observations.toString());
		WeatherForecast forecast = station.fetchForecast(LocalDate.now());
		
		System.out.println(forecast.toString());
		
		
		
		
		
		
		
		
	}

}
