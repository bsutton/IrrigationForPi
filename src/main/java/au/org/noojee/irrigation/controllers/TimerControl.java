package au.org.noojee.irrigation.controllers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.views.TimerNotification;

/**
 * Maintains a list of running timers for GardenBeds lighting etc
 * 
 * @author bsutton
 */
public class TimerControl
{
	private static Map<Long, Timer> timers = new HashMap<>();

	public synchronized static void startTimer(GardenFeature feature, String description, Duration duration,
			Function<GardenFeature, Void> completionAction, TimerNotification timerNotifaction)
	{
		// Stop any existing timer on this feature first.
		removeTimer(feature);

		Timer timer = new Timer(feature, description, duration, completionAction, timerNotifaction);

		timers.put(feature.getId(), timer);

		timer.start();
	}
	

	public synchronized static void removeTimer(GardenFeature feature)
	{
		Timer timer = getTimer(feature);

		if (timer != null)
		{
			timers.remove(timer.getFeature().getId());
			timer.cancel();
		}
	}

	public synchronized static Timer getTimer(GardenFeature feature)
	{
		return timers.get(feature.getId());
	}

	public synchronized static boolean isTimerRunning(GardenFeature feature)
	{
		Timer timer = getTimer(feature);
		return timer != null && timer.isTimerRunning();
	}

	public static Duration timeRemaining(GardenFeature feature)
	{
		Timer timer = getTimer(feature);
		return (timer == null ? Duration.ZERO : timer.timeRemaining());
	}

}
