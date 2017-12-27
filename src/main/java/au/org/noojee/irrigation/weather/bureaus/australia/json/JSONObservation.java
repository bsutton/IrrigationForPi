
package au.org.noojee.irrigation.weather.bureaus.australia.json;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.org.noojee.irrigation.weather.WeatherInterval;
import au.org.noojee.irrigation.weather.WeatherIntervalType;
import au.org.noojee.irrigation.weather.units.Humidity;
import au.org.noojee.irrigation.weather.units.Latitude;
import au.org.noojee.irrigation.weather.units.Longitude;
import au.org.noojee.irrigation.weather.units.Pressure;
import au.org.noojee.irrigation.weather.units.Speed;
import au.org.noojee.irrigation.weather.units.Temperature;
import au.org.noojee.irrigation.weather.units.WindDirection;

public class JSONObservation implements WeatherInterval
{

	@SerializedName("sort_order")
	@Expose
	private long sortOrder;
	@SerializedName("wmo")
	@Expose
	private long wmo;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("history_product")
	@Expose
	private String historyProduct;
	@SerializedName("local_date_time")
	@Expose
	private String localDateTime;
	@SerializedName("local_date_time_full")
	@Expose
	private LocalDateTime localDateTimeFull;
	@SerializedName("aifstime_utc")
	@Expose
	private LocalDateTime aifstimeUtc;
	@SerializedName("lat")
	@Expose
	private Latitude lat;
	@SerializedName("lon")
	@Expose
	private Longitude lon;
	@SerializedName("apparent_t")
	@Expose
	private Temperature apparentT;
	@SerializedName("cloud")
	@Expose
	private String cloud;
	@SerializedName("cloud_base_m")
	@Expose
	private Object cloudBaseM;
	@SerializedName("cloud_oktas")
	@Expose
	private Object cloudOktas;
	@SerializedName("cloud_type")
	@Expose
	private String cloudType;
	@SerializedName("cloud_type_id")
	@Expose
	private Object cloudTypeId;
	@SerializedName("delta_t")
	@Expose
	private Temperature deltaT;
	@SerializedName("gust_kmh")
	@Expose
	private Speed gustKmh;
	@SerializedName("gust_kt")
	@Expose
	private Speed gustKt;
	@SerializedName("air_temp")
	@Expose
	private Temperature airTemp;
	@SerializedName("dewpt")
	@Expose
	private Temperature dewpt;
	@SerializedName("press")
	@Expose
	private Pressure press;
	@SerializedName("press_msl")
	@Expose
	private Pressure pressMsl;
	@SerializedName("press_qnh")
	@Expose
	private Pressure pressQnh;
	@SerializedName("press_tend")
	@Expose
	private String pressTend;
	
	@SerializedName("rain_trace")
	@Expose
	private Integer rainTrace;
	
	@SerializedName("rel_hum")
	@Expose
	private Humidity relHum;
	@SerializedName("sea_state")
	@Expose
	private String seaState;
	@SerializedName("swell_dir_worded")
	@Expose
	private String swellDirWorded;
	@SerializedName("swell_height")
	@Expose
	private Object swellHeight;
	@SerializedName("swell_period")
	@Expose
	private Object swellPeriod;
	@SerializedName("vis_km")
	@Expose
	private String visKm;
	@SerializedName("weather")
	@Expose
	private String weather;
	@SerializedName("wind_dir")
	@Expose
	private WindDirection windDir; // S, SSW
	@SerializedName("wind_spd_kmh")
	@Expose
	private Speed windSpdKmh;
	@SerializedName("wind_spd_kt")
	@Expose
	private Speed windSpdKt;

	public long getSortOrder()
	{
		return sortOrder;
	}

	public long getWmo()
	{
		return wmo;
	}

	public String getName()
	{
		return name;
	}

	public String getHistoryProduct()
	{
		return historyProduct;
	}

	public String getLocalDateTime()
	{
		return localDateTime;
	}

	public LocalDateTime getLocalDateTimeFull()
	{
		return localDateTimeFull;
	}

	public LocalDateTime getAifstimeUtc()
	{
		return aifstimeUtc;
	}

	public Latitude getLat()
	{
		return lat;
	}

	public Longitude getLon()
	{
		return lon;
	}

	public Temperature getApparentT()
	{
		return apparentT;
	}

	public String getCloud()
	{
		return cloud;
	}

	public Object getCloudBaseM()
	{
		return cloudBaseM;
	}

	public Object getCloudOktas()
	{
		return cloudOktas;
	}

	public String getCloudType()
	{
		return cloudType;
	}

	public Object getCloudTypeId()
	{
		return cloudTypeId;
	}

	public Temperature getDeltaT()
	{
		return deltaT;
	}

	public Speed getGustKmh()
	{
		return gustKmh;
	}

	public Speed getGustKt()
	{
		return gustKt;
	}

	public Temperature getAirTemp()
	{
		return airTemp;
	}

	public Temperature getDewpt()
	{
		return dewpt;
	}

	public Pressure getPress()
	{
		return press;
	}

	public Pressure getPressMsl()
	{
		return pressMsl;
	}

	public Pressure getPressQnh()
	{
		return pressQnh;
	}

	public String getPressTend()
	{
		return pressTend;
	}

	public Integer getRainTrace()
	{
		return rainTrace;
	}

	public Humidity getRelHum()
	{
		return relHum;
	}

	public String getSeaState()
	{
		return seaState;
	}

	public String getSwellDirWorded()
	{
		return swellDirWorded;
	}

	public Object getSwellHeight()
	{
		return swellHeight;
	}

	public Object getSwellPeriod()
	{
		return swellPeriod;
	}

	public String getVisKm()
	{
		return visKm;
	}

	public String getWeather()
	{
		return weather;
	}

	public WindDirection getWindDir()
	{
		return windDir;
	}

	public Speed getWindSpdKmh()
	{
		return windSpdKmh;
	}

	public Speed getWindSpdKt()
	{
		return windSpdKt;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this).append("sortOrder", sortOrder).append("wmo", wmo).append("name", name)
				.append("historyProduct", historyProduct).append("localDateTime", localDateTime)
				.append("localDateTimeFull", localDateTimeFull).append("aifstimeUtc", aifstimeUtc).append("lat", lat)
				.append("lon", lon).append("apparentT", apparentT).append("cloud", cloud)
				.append("cloudBaseM", cloudBaseM).append("cloudOktas", cloudOktas).append("cloudType", cloudType)
				.append("cloudTypeId", cloudTypeId).append("deltaT", deltaT).append("gustKmh", gustKmh)
				.append("gustKt", gustKt).append("airTemp", airTemp).append("dewpt", dewpt).append("press", press)
				.append("pressMsl", pressMsl).append("pressQnh", pressQnh).append("pressTend", pressTend)
				.append("rainTrace", rainTrace).append("relHum", relHum).append("seaState", seaState)
				.append("swellDirWorded", swellDirWorded).append("swellHeight", swellHeight)
				.append("swellPeriod", swellPeriod).append("visKm", visKm).append("weather", weather)
				.append("windDir", windDir).append("windSpdKmh", windSpdKmh).append("windSpdKt", windSpdKt).append("\n")
				.toString();
	}

	/** Weather Interval inteface methods.
	 * 
	 */
	@Override
	public WeatherIntervalType getWeatherIntervalType()
	{
		return WeatherIntervalType.OBSERVATION;
	}

	@Override
	public Temperature getTemperature()
	{
		return this.airTemp;
	}
	
	@Override
	public Temperature getApparentTemperature()
	{
		return this.apparentT;
	}


	@Override
	public Integer getRainFail()
	{
		return this.rainTrace;
	}

	@Override
	public Pressure getPressure()
	{
		return this.press;
	}

	@Override
	public Humidity getHumidity()
	{
		return this.relHum;
	}

	@Override
	public Speed getWindSpeed()
	{
		return this.windSpdKmh;
	}

	@Override
	public Latitude getLatitude()
	{
		return this.lat;
	}

	@Override
	public Longitude getLongitude()
	{
		return this.lon;
	}

	@Override
	public WindDirection getWindDirection()
	{
		return this.windDir;
	}

	@Override
	public LocalDateTime getStartOfInterval()
	{
		return this.localDateTimeFull;
	}

	@Override
	public LocalDateTime getEndofInterval()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Duration getIntervalDuration()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
