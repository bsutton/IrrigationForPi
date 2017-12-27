package au.org.noojee.irrigation.weather.bureaus.australia;

import au.org.noojee.irrigation.weather.bureaus.australia.json.JSONObservations;

public class BOMObservations {

	private JSONObservations observations;

	
	public BOMObservations(JSONObservations observations) {
		this.observations = observations;
	}
	
	@Override
	public String toString() {
		return "BOMForecast [observations=" + observations + "]";
	}


}
