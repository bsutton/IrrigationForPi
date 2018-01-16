package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.util.function.Function;


public class Delay 
{
	public static void delay(Duration duration, EndPoint endPoint,
			 Function<EndPoint, Void> function)
	{
		new Thread(() -> {
			
			try
			{
				Thread.sleep(duration.toMillis());
				function.apply(endPoint);
			}
			catch (InterruptedException e)
			{
			}
		}).start();
		
	}


}
