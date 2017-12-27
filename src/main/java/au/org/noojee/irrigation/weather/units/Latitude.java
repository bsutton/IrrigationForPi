package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Latitude {
	
	BigDecimal latitude;
	
	public Latitude(String latitude)
	{
		this.latitude = new BigDecimal(latitude); 
	}
	
	@Override
	public String toString() {
		return "Latitude=" + latitude;
	}


}
