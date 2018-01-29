package au.org.noojee.irrigation.types;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.entities.EndPoint;

public class PinDependancy implements PinRelationship
{
	Logger logger = LogManager.getLogger();
	
	enum DependancyType
	{
		LeadingDelay, LagDelay
	}

	EndPoint primaryPin;

	/*
	 * Describes how the primary Pin is dependent on the relatedPins.
	 */
	DependancyType dependancy;

	/**
	 * the list of pins that the primary pin is dependant on.
	 */
	List<EndPoint> relatedPins;

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
							primaryPin.hardOn();
							Thread.sleep(interval.getSeconds() * 1000);
							for (EndPoint pin : relatedPins)
							{
								pin.hardOn();
							}
							break;

						case LeadingDelay:
							for (EndPoint pin : relatedPins)
							{
								pin.hardOn();
							}
							Thread.sleep(interval.getSeconds() * 1000);
							primaryPin.hardOn();
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
