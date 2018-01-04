package au.org.noojee.irrigation.weather.bureaus.australia;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import au.org.noojee.irrigation.weather.WeatherForecast;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureau;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureaus;

public class WeatherStationImplTest
{
/*
	@Test
	public void test() throws MalformedURLException
	{

		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		
		List<WeatherBureau> bureaus = WeatherBureaus.getBureaus(); 
		
		// Find australia
		WeatherBureau australia = null;
		for (WeatherBureau bureau : bureaus)
		{
			if (bureau.getCountryName().equals(BureauOfMeterologyAustralia.COUNTRY_NAME))
			{
				australia = bureau;
				break;
			}
		}
		WeatherBureaus.setDefaultBureau(australia);

		WeatherBureau bureau = WeatherBureaus.getDefaultBureau();
		
		
		BOMWeatherStation station = BOMWeatherStation.ViewBank;

		bureau.setDefaultStation(station);
		
		BOMObservations observations = station.fetchObservations(LocalDate.now());
		System.out.println(observations.toString());
		WeatherForecast forecast = station.fetchForecast(LocalDate.now());

		System.out.println(forecast.toString());

	}
	*/

}
