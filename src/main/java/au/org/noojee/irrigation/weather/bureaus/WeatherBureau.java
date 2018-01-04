package au.org.noojee.irrigation.weather.bureaus;

import java.util.List;

public interface WeatherBureau	

{
	abstract void setDefaultStation(WeatherStation station);
	
	abstract String getCountryName();

	abstract List<WeatherStation> getStations();
	
}
