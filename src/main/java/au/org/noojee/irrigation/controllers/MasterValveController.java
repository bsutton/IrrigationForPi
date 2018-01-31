package au.org.noojee.irrigation.controllers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;

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

	// the GardenBed we are currently using to drain out the line.
	// This will be null if we are not currently draining a line.
	private GardenBed drainOutVia = null;
	private Future<Void> drainOutFuture = null;

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

		if (this.masterValve.isDrainingLine())
		{
			// If we are turning off a valve we should never be in drain mode
			// i.e. we can't drain a line when a valve owned by this master valve is also running.
			assert (drainOutVia == null);

			if (!isOtherValveRunning(gardenBed))
			{
				// No other valve is running so we need to go into drain mode
				this.drainOutVia = gardenBed;

				// Turn off the master valve which will start the line drain out.
				this.masterValve.hardOff();

				
				// let the line drain for 30 seconds then turn of the garden beds valve.
				logger.error("Draining Line via Valve: " + gardenBedValve);
				TimerControl.startTimer(drainOutVia, "Draining", Duration.ofSeconds(30), v -> drainLineCompleted(), null);
				EndPointBus.getInstance().timerStarted(drainOutVia.getValve());
			}
			else
			{
				// some other valve is running so no point going into drain mode.
				gardenBedValve.hardOff();
			}

		}
		else
		{
			// The master valve doesn't need to be drain
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

	private synchronized Void drainLineCompleted()
	{
		EndPoint drainOutValve = this.drainOutVia.getValve();
		drainOutValve.hardOff();
		
		logger.error("Setting drainOutVia to null");
		this.drainOutVia = null;
		this.drainOutFuture = null;
		
		EndPointBus.getInstance().timerFinished(drainOutValve);
		
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
			if (this.masterValve.isDrainingLine() && this.drainOutVia != null)
			{
				// So we are currently draining out via another line.
				// We must turn that line off before we turn the master valve on
				// or that bed will suddenly start to be watered again.
				this.drainOutVia.getValve().hardOff();
				
				logger.error("Setting drainOutVia to null from SoftOn");
				// We need to cancel the outstanding drain 
				this.drainOutFuture.cancel(true);
				this.drainOutVia = null;
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
				+ ", drainOutVia=" + drainOutVia + "]";
	}


}
