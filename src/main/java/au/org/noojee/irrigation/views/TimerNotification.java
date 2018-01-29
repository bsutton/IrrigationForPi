package au.org.noojee.irrigation.views;

import java.time.Duration;

import au.org.noojee.irrigation.entities.GardenFeature;

public interface TimerNotification
{
	void timerStarted(GardenFeature feature, Duration duration);

}
