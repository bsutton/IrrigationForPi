package au.org.noojee.irrigation.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.types.EndPointType;

/**
 * The Valve controller attempts to intelligently manage all vales.
 * 
 * Its key objectives are:
 * 
 * 1) power management. 
 * Ensure that we don't switch more than one valve at a time.
 * If we allowed all valves to switch simultaneously then we risk overloading the power
 * supply due to the high current load whilst the valves transition.
 * 
 * 2) pressure management.
 * For systems where one or more master valves are fitted we attempt to leave all water lines
 * down stream of a master valve in a low pressure state by two actions:
 *  a) When turning a valve off we drain the line of pressure. This is achieved by turning the master valve off
 *  first, waiting a number of seconds for the down stream line to drain pressure and then 
 *  turning the down stream valve off.
 *  b) When turning a valve on we turn the valve on and then turn the master valve on. This ensures that
 *  we minimize pressure in the line between the master valve and the down stream valve.
 *
 * Not all of the above is currently implemented as the problem is a lot more complex than it looks.
 * 
 * @author bsutton
 */

public class GardenBedController
{
	private static Logger logger = LogManager.getLogger();
	
	private static List<MasterValveController> masterValveControllers = new ArrayList<>();
	
	public static void init()
	{
		EndPointDao daoEndPoint = new EndPointDao();
		
		
		// Initialise the list of Master Valve Controllers.
		List<EndPoint> masterValves = daoEndPoint.getMasterValves(); 
		
		// If a valve is edited we get re-initialised so need to clear out the controllers.
		masterValveControllers.clear();
		
		for (EndPoint masterValve : masterValves)
		{
			MasterValveController controller = new MasterValveController(masterValve);
			masterValveControllers.add(controller);
		}
		
	}

	/**
	 * Use this method to turn a garden bed's valve off.
	 * 
	 * @param masterValue
	 */
	public static synchronized void softOff(GardenBed gardenBed)
	{
		logger.error("Turning " + gardenBed.getName() + " Off." );
		
		MasterValveController masterValveController = getMasterValveForBed(gardenBed);
		
		if (masterValveController != null)
			masterValveController.softOff(gardenBed);
		else
			gardenBed.getValve().hardOff();
	}
	
	
	public static synchronized void softOn(GardenBed gardenBed)
	{
		logger.error("Turning " + gardenBed.getName() + " On." );
				
		MasterValveController masterValveController = getMasterValveForBed(gardenBed);
		
		if (masterValveController != null)
			masterValveController.softOn(gardenBed);
		else
			gardenBed.getValve().hardOn();
	}
	
	private static MasterValveController getMasterValveForBed(GardenBed gardenBed)
	{
		MasterValveController masterController = null;
		
		for (MasterValveController current : masterValveControllers)
		{
			if (current.getMasterValve().equals(gardenBed.getMasterValve()))
			{
				masterController = current;
				break;
			}
		}
		return masterController;
	}

	public static boolean isAnyValveRunning()
	{
		boolean valveRunning = false;
		
		EndPointDao daoEndPoint = new EndPointDao();
		
		List<EndPoint> endPoints = daoEndPoint.getAll(); 
		
		for (EndPoint endPoint : endPoints)
		{
			if (endPoint.getEndPointType() == EndPointType.MasterValve
					|| endPoint.getEndPointType() == EndPointType.Valve)
			{
				if (endPoint.isOn())
				{
					valveRunning = true;
					break;
				}
			}
		}
		return valveRunning;
	}



}
