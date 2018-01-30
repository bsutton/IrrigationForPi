package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.views.TimerNotification;

public class Timer
{
	private static Logger logger = LogManager.getLogger();

	private GardenFeature feature;
	private Duration duration;

	private Future<Void> timerFuture = null;

	private LocalDateTime startTimer;

	private TimerNotification timerNotifaction;



	public Timer(GardenFeature feature, Duration duration, TimerNotification timerNotifaction)
	{
		this.feature = feature;
		this.duration = duration;
		this.timerNotifaction = timerNotifaction;
	}

	public void start()
	{
		logger.error("Starting Timer for : " + this);
		
		this.startTimer = LocalDateTime.now();

		// Run the bed until the timer goes off.
		timerFuture = Delay.delay(duration, this, bed -> this.feature.softOff());
	}

	public void stop()
	{
		if (timerFuture != null)
		{
			timerFuture.cancel(false);
			timerFuture = null;
			timerNotifaction.timerFinished(this.feature);
		}

	}

	public boolean isTimerRunning()
	{
		return timerFuture != null;
	}

	public Duration timeRemaining()
	{
		LocalDateTime expectedEndTime = this.startTimer.plus(this.duration);
		return Duration.between(LocalDateTime.now(), expectedEndTime);
	}

}
