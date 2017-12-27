package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

public class Millimetres {
	BigDecimal millimetres;

	public Millimetres(String millimetres) {
		this.millimetres = new BigDecimal(millimetres);
	}
	
	@Override
	public String toString() {
		return "Millimeters=" + millimetres;
	}

}
