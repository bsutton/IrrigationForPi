package au.org.noojee.irrigation.weather;

import java.util.List;

public interface WeatherHistory
{
	List<WeatherInterval> getIntervals();
}
