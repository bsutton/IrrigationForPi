package au.org.noojee.irrigation.weather.bureaus;

import java.util.ArrayList;
import java.util.List;

import au.org.noojee.irrigation.weather.bureaus.australia.BureauOfMeterologyAustralia;

public class WeatherBureaus
{
	static private final List<WeatherBureau> bureaus = new ArrayList<>();
	private static WeatherBureau defaultBureau;
	
	
	static {
		// register your bureau here.
		// Note this a static code block so will run as the application loads so don't
		// do anything in you ctor that will break due to it being run very early.
		// Your WeatherBureau shouldn't do anything until its actually used!
		
		register(new BureauOfMeterologyAustralia());
	}
	
	static void register(WeatherBureau bureau)
	{
		bureaus.add(bureau);
	}
	
	static public void setDefaultBureau(WeatherBureau defaultBureau)
	{
		WeatherBureaus.defaultBureau = defaultBureau;
	}
	
	static public WeatherBureau getDefaultBureau()
	{
		return defaultBureau;
	}

	public static List<WeatherBureau> getBureaus()
	{
		return bureaus;
	}


}
