package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.EntityManagerRunnable;

public class Delay<F>
{
	private static Logger logger = LogManager.getLogger();
	
	Future<Void> future;
	
	private String description;

	private Duration duration;

	private F feature;
	
	
	public  Delay<F> delay(String description, Duration duration, F feature,
			Function<F, Void> function)
	{
		logger.error("Delay starting  '" + description + "' Duration: " + duration+ " for : " + feature );
		
		this.description = description;
		this.duration = duration;
		this.feature = feature;
		
		Callable<Void> callable = () ->
			{
				Thread.sleep(duration.toMillis());

				// Give the call back an entity manager.
				new EntityManagerRunnable(() ->
				{
					logger.error("Delay completing normally '" + description + "'  Duration: " + duration + " for : " + feature );
					function.apply(feature);
				}).run();

				

				return null;
			};

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		future = executorService.submit(callable);
		
		return this;
	}
	
	public void cancel()
	{
		future.cancel(false);
		
		logger.error("Delay cancelled  '" + description + "' Duration: " + duration+ " for : " + feature );

	}

}
