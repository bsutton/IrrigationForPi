package au.org.noojee.irrigation.controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.util.Delay;
import au.org.noojee.irrigation.views.TimerNotification;

public class Timer
{
	private static Logger logger = LogManager.getLogger();

	private GardenFeature feature;
	private Duration duration;

	private Delay<GardenFeature> timerFuture = null;

	private LocalDateTime startTimer;

	private TimerNotification timerNotifaction;

	private String description;

	private Function<GardenFeature, Void> completionAction;

	public Timer(GardenFeature feature, String description, Duration duration,
			Function<GardenFeature, Void> completionAction, TimerNotification timerNotifaction)
	{
		this.feature = feature;
		this.description = description;
		this.duration = duration;
		this.timerNotifaction = timerNotifaction;
		this.completionAction = completionAction;
	}

	public void start()
	{
		logger.error("Starting Timer '" + this.description + "' for : " + this.feature);

		this.startTimer = LocalDateTime.now();

		// Run the bed until the timer goes off.
		// timerFuture = Delay.delay(duration, this, bed -> this.feature.softOff());
		timerFuture = new Delay<GardenFeature>().delay(description, duration, feature, timer -> applyCompletionAction());
	}
	
	/**
	 * the timer has completed normally so clean up
	 * and call the completion action.
	 * @return
	 */
	public Void applyCompletionAction()
	{
		timerFuture = null;

		TimerControl.removeTimer(this.feature);
		completionAction.apply(feature);
		
		return null;
		
	}


	/**
	 * Prematurely cancel the timer.
	 * The Completion action isn't called.
	 */
	public void cancel()
	{
		if (timerFuture != null)
		{
			logger.error("Cancelling Timer '" + this.description + "' for : " + this.feature);
			timerFuture.cancel();
			timerFuture = null;
			
			TimerControl.removeTimer(this.feature);
			if (timerNotifaction != null)
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

	public String getDescription()
	{
		return this.description;
	}


	public GardenFeature getFeature()
	{
		return this.feature;
	}
}
