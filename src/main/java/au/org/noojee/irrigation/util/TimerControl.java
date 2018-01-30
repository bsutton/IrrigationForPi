package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

	public synchronized static void startTimer(GardenFeature feature, Duration duration,
			TimerNotification timerNotifaction)
	{
		// Stop any existing timer on this feature first.
		stopTimer(feature);

		Timer timer = new Timer(feature, duration, timerNotifaction);

		timers.put(feature.getId(), timer);

		timer.start();

	}

	public synchronized static void stopTimer(GardenFeature feature)
	{
		Timer timer = timers.get(feature.getId());
		if (timer != null)
			timer.stop();
	}

	public synchronized static boolean isTimerRunning(GardenFeature feature)
	{
		Timer timer = timers.get(feature.getId());
		return timer != null && timer.isTimerRunning();
	}

	public static Duration timeRemaining(GardenFeature feature)
	{
		Timer timer = timers.get(feature.getId());
		return (timer == null ? Duration.ZERO : timer.timeRemaining());
	}
}
