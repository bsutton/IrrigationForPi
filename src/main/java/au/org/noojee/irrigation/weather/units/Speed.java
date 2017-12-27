package au.org.noojee.irrigation.weather.units;

import java.math.BigDecimal;

/**
 * Speed of an measurement.
 * 
 * @author bsutton
 *
 */
public class Speed {

	BigDecimal speed;

	public Speed(String speed) {
		this.speed = new BigDecimal(speed);
	}
	
	@Override
	public String toString() {
		return "Speed=" + speed + " km";
	}

}
