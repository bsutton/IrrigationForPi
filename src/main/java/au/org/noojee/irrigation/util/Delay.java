package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import au.org.noojee.irrigation.dao.EntityManagerRunnable;
import au.org.noojee.irrigation.types.GardenBedController;

public class Delay
{

	public static <D> Future<Void> delay(Duration duration, D device,
			Function<D, Void> function)
	{

		Callable<Void> callable = () ->
			{
				Thread.sleep(duration.toMillis());

				// Give the call back an entity manager.
				new EntityManagerRunnable(() ->
				{
					function.apply(device);
				}).run();

				

				return null;
			};

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Void> future = executorService.submit(callable);

		return future;
	}

	/*
	 * public static Future<Void> delay(Duration duration, EndPoint endPoint, Function<EndPoint, Void> function) {
	 * Callable<Void> callable = () -> { Thread.sleep(duration.toMillis()); function.apply(endPoint); return null; };
	 * ExecutorService executorService = Executors.newSingleThreadExecutor(); Future<Void> future =
	 * executorService.submit(callable); return future; } public static Future<Void> delay(Duration duration, GardenBed
	 * gardenBed, Function<GardenBed, Void> function) { Callable<Void> callable = () -> {
	 * Thread.sleep(duration.toMillis()); function.apply(gardenBed); return null; }; ExecutorService executorService =
	 * Executors.newSingleThreadExecutor(); Future<Void> future = executorService.submit(callable); return future; }
	 */

}
