package au.org.noojee.irrigation.types;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.Pin;

public class PinDependancy implements PinRelationship
{
	Logger logger = LogManager.getLogger();
	
	enum DependancyType
	{
		LeadingDelay, LagDelay
	}

	Pin primaryPin;

	/*
	 * Describes how the primary Pin is dependent on the relatedPins.
	 */
	DependancyType dependancy;

	/**
	 * the list of pins that the primary pin is dependant on.
	 */
	List<Pin> relatedPins;

	/*
	 * the duration of the lead or lag delay.
	 */
	Duration interval;

	void setOn()
	{
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() ->
			{
				try
				{
					switch (dependancy)
					{
						case LagDelay:
							primaryPin.setOn();
							Thread.sleep(interval.getSeconds() * 1000);
							for (Pin pin : relatedPins)
							{
								pin.setOn();
							}
							break;

						case LeadingDelay:
							for (Pin pin : relatedPins)
							{
								pin.setOn();
							}
							Thread.sleep(interval.getSeconds() * 1000);
							primaryPin.setOn();
							break;
					}
				}
				catch (InterruptedException e)
				{
					logger.error(e,e);
				}

			});

	}

}
