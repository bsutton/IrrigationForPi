package au.org.noojee.irrigation.weather;

import java.time.Duration;
import java.time.LocalDateTime;

import au.org.noojee.irrigation.weather.units.Humidity;
import au.org.noojee.irrigation.weather.units.Latitude;
import au.org.noojee.irrigation.weather.units.Longitude;
import au.org.noojee.irrigation.weather.units.Pressure;
import au.org.noojee.irrigation.weather.units.Speed;
import au.org.noojee.irrigation.weather.units.Temperature;
import au.org.noojee.irrigation.weather.units.WindDirection;

public interface WeatherInterval
{
	/**
	 * The type of interval, FORECAST, or historic OBSERVATION.
	 * @return
	 */
	WeatherIntervalType getWeatherIntervalType();
	
	/**
	 * The air temperature.
	 * 
	 * @return
	 */
	Temperature getTemperature();
	
	
	/**
	 * The apparent temperate, some times referred to as the 'feels like' temperature.
	 * @return
	 */
	Temperature getApparentTemperature();

	/**
	 * Rain full in millimeters during this interval.
	 * 
	 * @return
	 */
	Integer getRainFail();

	/**
	 * The average pressure during the interval
	 * 
	 * @return
	 */
	Pressure getPressure();

	/**
	 * The average Humidity during the interval
	 * 
	 * @return
	 */

	Humidity getHumidity();

	/**
	 * The average Wind Speed during the interval
	 * 
	 * @return
	 */
	Speed getWindSpeed();

	Latitude getLatitude();

	Longitude getLongitude();

	/**
	 * The average WindDirection during the interval
	 * 
	 * @return
	 */
	WindDirection getWindDirection();

	LocalDateTime getStartOfInterval();

	LocalDateTime getEndofInterval();

	Duration getIntervalDuration();

}
