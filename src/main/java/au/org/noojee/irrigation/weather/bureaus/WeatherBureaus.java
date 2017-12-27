package au.org.noojee.irrigation.weather.bureaus;

import au.org.noojee.irrigation.weather.bureaus.australia.BureauOfMeterology;

public enum WeatherBureaus
{
	// Add additional Weather Bureaus to this enum.
	Australia(new BureauOfMeterology());
	
	
	final WeatherBureau weatherBureau;
	
	WeatherBureaus(WeatherBureau weatherBureau)
	{
		this.weatherBureau = weatherBureau;
	}
}
