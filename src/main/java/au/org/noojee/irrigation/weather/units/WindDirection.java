package au.org.noojee.irrigation.weather.units;

public enum WindDirection {

	Calm("Calm"), South("S"), SouthWest("SW"), SouthSouthWest("SSW"), SouthEast("SE"), SouthSouthEast("SSE"), North(
			"North"), NorthWest("NW"), NorthNorthWest("NNW"), NorthEast("NE"), NorthNorthEast("NNE")
	, West("W"), WestNorthWest("WNW"), WestSouthWest("WSW")
	, East("E"), EastNorthEast("ENE"), EastSouthEast("ESE");
	
	
	private String abbreviation;

	WindDirection(String abbreviation)
	{
		this.abbreviation = abbreviation;
	}
	
	public static WindDirection fromAbbreviation(String abbreviation)
	{
		WindDirection match = Calm;
		for (WindDirection direction : WindDirection.values())
		{
			if (direction.abbreviation.equals(abbreviation))
				match = direction;
		}
		return match;
	}
	
	
}
