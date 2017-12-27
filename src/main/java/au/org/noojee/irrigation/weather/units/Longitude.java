package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Longitude {

	BigDecimal longitude;
	
	public Longitude(String longitude)
	{
		this.longitude = new BigDecimal(longitude); 
	}
	
	@Override
	public String toString() {
		return "Longitude=" + longitude;
	}


}
