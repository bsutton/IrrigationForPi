package au.org.noojee.irrigation.types;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.Delay;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;

/**
 * We need special logic for a Master Values as a master valve must be on if any if its Valves are on.
 * 
 * @author bsutton
 */

public class ValveController
{
	private static Logger logger = LogManager.getLogger();
	
	// The map has a list of GardenBeds that belong to the master valve.
	// The map is built as GardenBeds are turned on.

	private static final Map<EndPoint, List<GardenBed>> runningGardenBeds = new HashMap<>();

	/**
	 * use this method to request that a master valve is turned off. If no child valves are in operation then the master
	 * valve will actually be turned off.
	 * 
	 * @param masterValue
	 */
	public static synchronized void turnOff(GardenBed gardenBed)
	{

		Duration delay = Duration.ofSeconds(0);

		EndPoint valve = gardenBed.getValve();
		EndPoint masterValve = gardenBed.getMasterValve();

		if (gardenBed.getMasterValve() != null)
		{
			List<GardenBed> gardenBeds = runningGardenBeds.get(masterValve);
			if (gardenBeds == null || gardenBeds.size() == 1)
			{
				// No other beds are running so we can turn the master valve off.
				masterValve.setOff();

				// If we are turning the master valve off and the bed is configured to bleed the line,
				// then we turn the master valve off first
				// and let the line de-pressurise before we turn off the
				// bed's own valve.
				if (gardenBed.isBleedLine())
					delay = Duration.ofSeconds(30);
			}
			// else if (gardenBeds.size() > 1)
			// Other beds are running off this master valve so we can't turn the master valve off.
			// Just remove this garden bed from the list of running garden beds.
				
			
			runningGardenBeds.remove(gardenBed);

		}
		Delay.delay(delay, valve, v -> v.setOff());

	}

	public static synchronized void turnOn(GardenBed gardenBed)
	{
		EndPoint valve = gardenBed.getValve();
		EndPoint masterValve = gardenBed.getMasterValve();
		
		// If the garden bed has a master valve then add the garden to the list of running beds.
		if (gardenBed.getMasterValve() != null)
		{
			List<GardenBed> gardenBeds = runningGardenBeds.get(masterValve);
			if (gardenBeds == null)
				gardenBeds = new ArrayList<>();
			
			if (gardenBeds.size() == 0)
			{
				// No other garden beds associated with this master valve
				// are being watered so we need to actually turn the
				// master valve on. (If another associated bed was being watered then
				// the master valve would already be on.

				// We wait two seconds before turning on the master valve
				// to ensure that the bed valve is on so that we
				// keep the line de-pressurised.
				Delay.delay(Duration.ofSeconds(2), masterValve, m -> m.setOn());
			}

			if (gardenBeds.indexOf(gardenBed) != -1)
			{
				logger.error("A garden bed is in the list twice!!!");
			}
			
			gardenBeds.add(gardenBed);
			runningGardenBeds.put(masterValve, gardenBeds);
		}

		valve.setOn();
	}

}
