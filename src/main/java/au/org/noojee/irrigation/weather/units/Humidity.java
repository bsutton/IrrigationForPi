package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Humidity {


	BigDecimal humidity;

	public Humidity(String humidity) {
		this.humidity = new BigDecimal(humidity);
	}
	
	@Override
	public String toString() {
		return "Humidity=" + humidity;
	}

}
