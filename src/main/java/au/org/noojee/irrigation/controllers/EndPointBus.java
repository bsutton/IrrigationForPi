package au.org.noojee.irrigation.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.views.EndPointChangeListener;

/**
 * Manages notifications to listeners when and EndPoint changes start (from off to own).
 * 
 * When the end point models an PWM we only get a notification when the PWM starts and stops 
 * NOT each time that the pwm changes state.
 * 
 * @author bsutton
 *
 */
public class EndPointBus
{
	private static Logger logger = LogManager.getLogger();
	
	private Map<EndPoint, List<EndPointChangeListener>> listenerMap = new HashMap<>();
	
	/** 
	 * Singleton
	 */
	static EndPointBus self = new EndPointBus();
	
	public static EndPointBus getInstance()
	{
		return self;
	}
	
	
	public synchronized void addListener(EndPoint endPoint, EndPointChangeListener listener)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);
		
		if (listeners == null)
			listeners = new ArrayList<EndPointChangeListener>();
		
		if (listeners.contains(listener))
			logger.error("Potentional leak as listener added twice to EndPointBus:" + listener);
		
		listeners.add(listener); 
		
		listenerMap.put(endPoint, listeners);
	}
	
	synchronized void removeListener(EndPoint endPoint, EndPointChangeListener listener)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);
		
		if (listeners != null)
		{
			if (listeners.contains(listener))
				logger.error("Potentional leak as non-existant listener removed from EndPointBus:" + listener);

			listeners.remove(listener);
		}
		else
			logger.error("Potentional leak as listener remove from EndPointBus when no listeners exist:" + listener);
	}
	
	
	/**
	 * Removes the given listener from all end points.
	 * @param listener
	 */
	public void removeListener(EndPointChangeListener listener)
	{
		for (List<EndPointChangeListener> listeners : listenerMap.values())
		{
			listeners.remove(listener);
		}
	}

	
	public void notifyHardOn(EndPoint endPoint)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);

		if (listeners != null)
		{
			for (EndPointChangeListener listener : listeners)
			{
				listener.notifyHardOn(endPoint);
			}
		
		}
	}
	
	public void notifyHardOff(EndPoint endPoint)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);

		if (listeners != null)
		{
			for (EndPointChangeListener listener : listeners)
			{
				listener.notifyHardOff(endPoint);
			}
		
		}
	}


	public void timerStarted(EndPoint endPoint)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);

		if (listeners != null)
		{
			for (EndPointChangeListener listener : listeners)
			{
				listener.timerStarted(endPoint);
			}
		
		}
	}
	
	public void timerFinished(EndPoint endPoint)
	{
		List<EndPointChangeListener> listeners = listenerMap.get(endPoint);

		if (listeners != null)
		{
			for (EndPointChangeListener listener : listeners)
			{
				listener.timerFinished(endPoint);
			}
		
		}
	}

	




	
	

}
