package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Pressure {
	BigDecimal pressure;

	public Pressure(String pressure) {
		this.pressure = new BigDecimal(pressure);
	}
	
	@Override
	public String toString() {
		return "Pressure=" + pressure;
	}

}
