package au.org.noojee.irrigation.types;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.util.Delay;

/**
 * We need special logic for a Master Values as a master valve must be on if any if its Valves are on.
 * 
 * @author bsutton
 */

public class MasterValveController
{

	private static Logger logger = LogManager.getLogger();

	// The master vale we are controlling.
	private EndPoint masterValve;

	// List of GardenBeds that belong to the master valve.
	private final List<GardenBed> controlledBeds;

	// the GardenBed we are currently using to bleed out the line.
	// This will be null if we are not currently bleeding a line.
	private GardenBed bleedOutVia = null;
	private Future<Void> bleedOutFuture = null;

	MasterValveController(EndPoint masterValve)
	{
		this.masterValve = masterValve;
		
		GardenBedDao daoGardenBed = new GardenBedDao();
		
		this.controlledBeds = daoGardenBed.getControlledBy(masterValve);
	}

	/**
	 * use this method to request that a master valve is turned off. If no child valves are in operation then the master
	 * valve will actually be turned off.
	 * 
	 * @param masterValue
	 */
	public synchronized void softOff(GardenBed gardenBed)
	{
		logger.error("Turning " + gardenBed.getName() + " Off.");

		assert (gardenBed.getMasterValve().equals(this.masterValve));


		EndPoint gardenBedValve = gardenBed.getValve();

		if (this.masterValve.isBleedLine())
		{
			// If we are turning off a valve we should never be in bleed mode
			// i.e. we can't bleed a line when a valve owned by this master valve is also running.
			assert (bleedOutVia == null);

			if (!isOtherValveRunning(gardenBed))
			{
				// No other valve is running so we need to go into bleed mode
				this.bleedOutVia = gardenBed;

				// Turn off the master valve which will start the line bleed out.
				this.masterValve.hardOff();

				
				// let the line bleed for 30 seconds then turn of the garden beds valve.
				logger.error("Bleeding Line via Valve: " + gardenBedValve);
				bleedOutFuture = Delay.delay(Duration.ofSeconds(30), gardenBedValve, v -> bleedLine(v));
			}
			else
			{
				// some other valve is running so no point going into bleed mode.
				gardenBedValve.hardOff();
			}

		}
		else
		{
			// The master valve doesn't need to be bleed
			// But we only turn the master valve off if no other
			// valves down stream of this master valve are running.
			if (!isOtherValveRunning(gardenBed))
				this.masterValve.hardOff();

			// We always try to create a gap between tranistioning valves
			// to avoid heavy power draw.
			// Delay.delay(Duration.ofSeconds(1), gardenBedValve, v -> v.setOff());

			// We have eliminated the delay for the moment as it makes this logic MUCH
			// more complex as a valve now has three states: ON, OFF, PENDING_OFF
			gardenBedValve.hardOff();
		}

	}

	private synchronized Void bleedLine(EndPoint bleedOutValve)
	{
		bleedOutValve.hardOff();
		
		assert(bleedOutValve.equals(this.bleedOutVia.getValve()));
		logger.error("Setting bleedOutVia to null");
		this.bleedOutVia = null;
		this.bleedOutFuture = null;
		
		return null;
	}

	private boolean isOtherValveRunning(GardenBed gardenBed)
	{
		boolean foundRunningValve = false;
		
		for (GardenBed current : this.controlledBeds)
		{
			if (!current.equals(gardenBed))
			{
				if (current.isOn())
				{
					foundRunningValve = true;
					break;
				}
			}
		}
		return foundRunningValve;
	}

	public synchronized void softOn(GardenBed gardenBed)
	{
		logger.error("Turning " + gardenBed.getName() + " On.");

		assert (gardenBed.getMasterValve().equals(this.masterValve));

		EndPoint gardenBedValve = gardenBed.getValve();

		// We always turn the bed valve on first
		// so we don't get pressure build up between the master valve and the down stream bed valve.
		gardenBedValve.hardOn();

		
		if (!this.masterValve.isOn())
		{
			if (this.masterValve.isBleedLine() && this.bleedOutVia != null)
			{
				// So we are currently bleeding out via another line.
				// We must turn that line off before we turn the master valve on
				// or that bed will suddenly start to be watered again.
				this.bleedOutVia.getValve().hardOff();
				
				logger.error("Setting bleedOutVia to null");
				// We need to cancel the outstanding bleed 
				this.bleedOutFuture.cancel(true);
				this.bleedOutVia = null;
			}
			this.masterValve.hardOn();

			// No other garden beds associated with this master valve
			// are being watered so we need to actually turn the
			// master valve on. (If another associated bed was being watered then
			// the master valve would already be on.

			// We wait two seconds before turning on the master valve
			// to ensure that the bed valve is on so that we
			// keep the line de-pressurised.
			// Delay.delay(Duration.ofSeconds(2), masterValve, m -> m.setOn());
		}

	}

	public EndPoint getMasterValve()
	{
		return this.masterValve;
	}
	
	@Override
	public String toString()
	{
		return "MasterValveController [masterValve=" + masterValve + ", controlledBeds=" + controlledBeds
				+ ", bleedOutVia=" + bleedOutVia + "]";
	}


}