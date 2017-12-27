package au.org.noojee.irrigation.weather.bureaus.australia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import au.org.noojee.irrigation.IrrigationException;
import au.org.noojee.irrigation.weather.WeatherForecast;
import au.org.noojee.irrigation.weather.bureaus.australia.json.JSONWeatherStationData;

public enum BOMWeatherStation
{
	ViewBank("IDCJAC0009", "http://www.bom.gov.au/fwo/IDV60801/IDV60801.95874.json", "");
	

	Logger logger = LogManager.getLogger();
	private URL observationURL;
	//private URL forecastURL;
	//private String identifier;


	BOMWeatherStation(String identifier, String observationSource, String forecastSource)
	{

		try
		{
		//	this.identifier = identifier;
			this.observationURL = new URL(observationSource);
			// this.forecastURL = new URL(forecastSource);

		}
		catch (MalformedURLException e)
		{
			logger.error(e, e);
			throw new IrrigationException(e);
		}


	}

	public WeatherForecast fetchForecast(LocalDate date)
	{

		WeatherForecast forecast = null;

		return forecast;
	}

	public BOMObservations fetchObservations(LocalDate date)
	{

		BOMObservations bomObeservations = null;
		String result;

		try
		{

			// Download the observation data
			InputStreamReader reader = new InputStreamReader(observationURL.openStream());
			result = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
			
			logger.error("Raw JSON data: " + result);

			Gson gson = BureauOfMeterology.getGson();

			JSONWeatherStationData data = gson.fromJson(result, JSONWeatherStationData.class);
			bomObeservations = new BOMObservations(data.getObservations());
			logger.error(data.toString());

		}
		catch (IOException e)
		{

			logger.error(e, e);
			throw new IrrigationException(e);
		}

		return bomObeservations;
	}

}
