package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Temperature {

	BigDecimal temperature;
	
	public Temperature(String temperature)
	{
		this.temperature = new BigDecimal(temperature); 
	}
	

	@Override
	public String toString() {
		return "Temperature=" + temperature + " C";
	}

}
