package au.org.noojee.irrigation.weather.bureaus.australia;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import au.org.noojee.irrigation.weather.WeatherForecast;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureau;
import au.org.noojee.irrigation.weather.units.Humidity;
import au.org.noojee.irrigation.weather.units.Latitude;
import au.org.noojee.irrigation.weather.units.Longitude;
import au.org.noojee.irrigation.weather.units.Millimetres;
import au.org.noojee.irrigation.weather.units.Pressure;
import au.org.noojee.irrigation.weather.units.Speed;
import au.org.noojee.irrigation.weather.units.Temperature;
import au.org.noojee.irrigation.weather.units.WindDirection;

/**
 * Implementation for access to the Australian Bureau of Meteorology.
 * 
 * @author bsutton
 */
public class BureauOfMeterology implements WeatherBureau
{ 

	// JSON data being fetched for view bank from. 
	// http://reg.bom.gov.au/products/IDV60901/IDV60901.95874.shtml

	
	BOMWeatherStation station;

	static private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	WeatherForecast fetchForecast(LocalDate date)
	{
		return station.fetchForecast(date);

	}

	public static Gson getGson()
	{

		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>()
		{
			@Override
			public LocalDateTime deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				LocalDateTime data = LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dateFormat);
				return data;

			}
		}).registerTypeAdapter(Latitude.class, new JsonDeserializer<Latitude>()
		{
			@Override
			public Latitude deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Latitude latitude = new Latitude(json.getAsJsonPrimitive().getAsString());
				return latitude;

			}
		}).registerTypeAdapter(Longitude.class, new JsonDeserializer<Longitude>()
		{
			@Override
			public Longitude deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Longitude data = new Longitude(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(Pressure.class, new JsonDeserializer<Pressure>()
		{
			@Override
			public Pressure deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Pressure data = new Pressure(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(Temperature.class, new JsonDeserializer<Temperature>()
		{
			@Override
			public Temperature deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Temperature data = new Temperature(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(Speed.class, new JsonDeserializer<Speed>()
		{
			@Override
			public Speed deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Speed data = new Speed(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(Humidity.class, new JsonDeserializer<Humidity>()
		{
			@Override
			public Humidity deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Humidity data = new Humidity(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(WindDirection.class, new JsonDeserializer<WindDirection>()
		{
			@Override
			public WindDirection deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				WindDirection data = WindDirection.fromAbbreviation(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).registerTypeAdapter(Millimetres.class, new JsonDeserializer<Millimetres>()
		{
			@Override
			public Millimetres deserialize(JsonElement json, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{

				Millimetres data = new Millimetres(json.getAsJsonPrimitive().getAsString());
				return data;

			}
		}).create();

		return gson;
	}

}
