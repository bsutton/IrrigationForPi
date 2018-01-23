package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import au.org.noojee.irrigation.entities.EndPoint;

public class Delay
{
	public static Future<Void> delay(Duration duration, EndPoint endPoint,
			Function<EndPoint, Void> function)
	{

		Callable<Void> callable = () ->
			{
				Thread.sleep(duration.toMillis());

				function.apply(endPoint);

				return null;
			};

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Void> future = executorService.submit(callable);

		return future;
	}

}
